package com.zyeeda.framework.security.internal;

import org.apache.shiro.mgt.SecurityManager;

import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.server.ApplicationServer;
import com.zyeeda.framework.service.AbstractService;

public class ShiroSecurityServiceProvider extends AbstractService implements SecurityService<SecurityManager> {

	private RoleManager roleMgr;
	
	public ShiroSecurityServiceProvider(ApplicationServer server, String name) {
		super(server, name);
		this.roleMgr = new RoleManager(this);
	}
	
	public ShiroSecurityServiceProvider(ApplicationServer server) {
		super(server, ShiroSecurityServiceProvider.class.getSimpleName());
	}

	@Override
	public SecurityManager getSecurityManager() {
		SecurityManager securityManager = new ShiroSecurityManager(this.getServer());
		return securityManager;
	}
	
	public RoleManager getRoleManager() {
		return this.roleMgr;
	}

}
