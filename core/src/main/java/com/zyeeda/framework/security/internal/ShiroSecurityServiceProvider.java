package com.zyeeda.framework.security.internal;

import org.apache.shiro.mgt.SecurityManager;
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

@ServiceId("ShiroSecurityServiceProvider")
@Marker(Primary.class)
public class ShiroSecurityServiceProvider extends AbstractService implements SecurityService<SecurityManager> {

	// Injected
	private final LdapService ldapSvc;
	private final PersistenceService persistenceSvc;
	private final TransactionService txSvc;
	private final SecurityService<?> securitySvc;
	
	private RoleManager roleMgr;
	
	public ShiroSecurityServiceProvider(LdapService ldapSvc,
			@Primary PersistenceService persistenceSvc,
			@Primary TransactionService txSvc,
			SecurityService<?> securitySvc,
			Logger logger,
			RegistryShutdownHub shutdownHub) {
		
		super(logger, shutdownHub);
		this.ldapSvc = ldapSvc;
		this.persistenceSvc = persistenceSvc;
		this.txSvc = txSvc;
		this.securitySvc = securitySvc;
		
		this.roleMgr = new RoleManager(this);
	}

	@Override
	public SecurityManager getSecurityManager() {
		return new ShiroSecurityManager(
				this.ldapSvc, this.persistenceSvc, this.txSvc, this.securitySvc, this.getLogger());
	}
	
	@Override
	public RoleManager getRoleManager() {
		return this.roleMgr;
	}
	
	@Override
	public PersistenceService getPersistenceService() {
		return this.persistenceSvc;
	}

}
