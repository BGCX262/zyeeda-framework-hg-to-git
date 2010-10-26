package com.zyeeda.framework.security.internal;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.slf4j.Logger;

import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.service.AbstractService;

@Marker(Primary.class)
@ServiceId("shiro-security-service-provider")
public class ShiroSecurityServiceProvider extends AbstractService implements SecurityService<SecurityManager> {

	// Injected
	private final LdapService ldapSvc;
	private final PersistenceService persistenceSvc;
	
	private RoleManager roleMgr;
	
	public ShiroSecurityServiceProvider(LdapService ldapSvc,
			@Primary PersistenceService persistenceSvc,
			Logger logger,
			RegistryShutdownHub shutdownHub) {
		
		super(logger, shutdownHub);
		this.ldapSvc = ldapSvc;
		this.persistenceSvc = persistenceSvc;
		
		this.roleMgr = new RoleManager(this.persistenceSvc);
	}

	@Override
	public SecurityManager getSecurityManager() {
		return new ShiroSecurityManager();
	}
	
	@Override
	public RoleManager getRoleManager() {
		return this.roleMgr;
	}
	
	private Realm getRealm() {
		return new ShiroCombinedRealm(this.ldapSvc, this.roleMgr, this.getLogger());
	}
	
	private class ShiroSecurityManager extends DefaultWebSecurityManager {

		public ShiroSecurityManager() {
			super(getRealm());
		}
		
	}

}
