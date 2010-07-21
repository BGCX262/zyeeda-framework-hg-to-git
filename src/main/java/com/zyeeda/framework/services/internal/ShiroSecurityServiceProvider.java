package com.zyeeda.framework.services.internal;

import org.apache.commons.configuration.Configuration;

import org.apache.shiro.mgt.SecurityManager;

import com.zyeeda.framework.security.internal.ShiroSecurityManager;
import com.zyeeda.framework.server.ApplicationServer;
import com.zyeeda.framework.services.SecurityService;

public class ShiroSecurityServiceProvider extends AbstractService implements SecurityService<SecurityManager> {

	public ShiroSecurityServiceProvider(ApplicationServer server, String name) {
		super(server, name);
	}
	
	public ShiroSecurityServiceProvider(ApplicationServer server) {
		super(server, ShiroSecurityServiceProvider.class.getSimpleName());
	}
	
	@Override
	public void init(Configuration config) throws Exception {
	}

	@Override
	public SecurityManager getSecurityManager() {
		ShiroSecurityManager securityManager = new ShiroSecurityManager(this.getServer());
		return securityManager;
	}

}
