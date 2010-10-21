package com.zyeeda.framework.security.internal;

import org.apache.shiro.mgt.SecurityManager;
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
import com.zyeeda.framework.transaction.TransactionService;

@ServiceId("shiro-security-service-provider")
@Marker(Primary.class)
public class ShiroSecurityServiceProvider extends AbstractService implements SecurityService<SecurityManager> {

	// Injected
	private final LdapService ldapSvc;
	private final PersistenceService persistenceSvc;
	private final TransactionService txSvc;
	
	private RoleManager roleMgr;
	
	public ShiroSecurityServiceProvider(LdapService ldapSvc,
			@Primary PersistenceService persistenceSvc,
			@Primary TransactionService txSvc,
			Logger logger,
			RegistryShutdownHub shutdownHub) {
		
		super(logger, shutdownHub);
		this.ldapSvc = ldapSvc;
		this.persistenceSvc = persistenceSvc;
		this.txSvc = txSvc;
		
		this.roleMgr = new RoleManager(this);
	}

	@Override
	public SecurityManager getSecurityManager() {
		return new ShiroSecurityManager(
				this.ldapSvc, this.txSvc, this.roleMgr, this.getLogger());
	}
	
	@Override
	public RoleManager getRoleManager() {
		return this.roleMgr;
	}
	
	@Override
	public PersistenceService getPersistenceService() {
		return this.persistenceSvc;
	}
	
	private class ShiroSecurityManager extends DefaultWebSecurityManager {

		public ShiroSecurityManager(LdapService ldapSvc,
				TransactionService txSvc,
				RoleManager roleMgr,
				Logger logger) {
			super(new ShiroCombinedRealm(ldapSvc, txSvc, roleMgr, logger));
		}
		
	}

}
