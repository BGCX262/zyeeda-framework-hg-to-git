package com.zyeeda.framework.security.internal;

import org.apache.shiro.mgt.SecurityManager;
import org.slf4j.Logger;

import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.service.AbstractService;

public class ShiroSecurityServiceProvider extends AbstractService implements SecurityService<SecurityManager> {

	// Injected
	private LdapService ldapSvc;
	private PersistenceService persistenceSvc;
	private Logger logger;
	
	private RoleManager roleMgr;
	
	public ShiroSecurityServiceProvider(LdapService ldapSvc,
			PersistenceService persistenceSvc,
			Logger logger) {
		
		this.ldapSvc = ldapSvc;
		this.persistenceSvc = persistenceSvc;
		this.logger = logger;
		
		this.roleMgr = new RoleManager(this);
	}

	@Override
	public SecurityManager getSecurityManager() {
		return new ShiroSecurityManager(
				this.ldapSvc, this.persistenceSvc, this.logger);
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
