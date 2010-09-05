package com.zyeeda.framework.security.internal;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.slf4j.Logger;

import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.service.AbstractService;

@ServiceId("ShiroSecurityServiceProvider")
@Marker(Primary.class)
public class ShiroSecurityServiceProvider extends AbstractService implements SecurityService<SecurityManager> {

	// Injected
	private final LdapService ldapSvc;
	private final PersistenceService persistenceSvc;
	private final SecurityService<?> securitySvc;
	private final Logger logger;
	
	private RoleManager roleMgr;
	
	public ShiroSecurityServiceProvider(LdapService ldapSvc,
			PersistenceService persistenceSvc,
			SecurityService<?> securitySvc,
			Logger logger) {
		
		this.ldapSvc = ldapSvc;
		this.persistenceSvc = persistenceSvc;
		this.securitySvc = securitySvc;
		this.logger = logger;
		
		this.roleMgr = new RoleManager(this);
	}

	@Override
	public SecurityManager getSecurityManager() {
		return new ShiroSecurityManager(
				this.ldapSvc, this.persistenceSvc, this.securitySvc, this.logger);
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
