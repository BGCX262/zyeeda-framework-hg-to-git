package com.zyeeda.framework.security.internal;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;

import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.service.AbstractService;

@Marker(Primary.class)
@ServiceId("shiro-security-service")
public class ShiroSecurityServiceProvider extends AbstractService implements SecurityService<SecurityManager> {

	// Injected
	private final LdapService ldapSvc;
	private final PersistenceService persistenceSvc;
	
	private RoleManager roleMgr;
	
	public ShiroSecurityServiceProvider(LdapService ldapSvc,
			@Primary PersistenceService persistenceSvc,
			RegistryShutdownHub shutdownHub) {
		
		super(shutdownHub);
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
	
	/**
	 * 获取当前登录用户的唯一标识。
	 * 如果是匿名用户，则返回 null。
	 */
	@Override
	public String getCurrentUser() {
		return "admin";
		//Subject current = SecurityUtils.getSubject();
		//return current.getPrincipal().toString();
	}
	
	private Realm getRealm() {
		return new ShiroCombinedRealm(this.ldapSvc, this.roleMgr);
	}
	
	private class ShiroSecurityManager extends DefaultWebSecurityManager {

		public ShiroSecurityManager() {
			super(getRealm());
		}
		
	}

}
