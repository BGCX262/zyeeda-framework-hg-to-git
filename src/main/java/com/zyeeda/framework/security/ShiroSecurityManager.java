package com.zyeeda.framework.security;

import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

import com.zyeeda.framework.server.ApplicationServer;

public class ShiroSecurityManager extends DefaultWebSecurityManager {

	private ApplicationServer server;
	
	public ShiroSecurityManager(ApplicationServer server) {
		super(new ShiroCombinedRealm(server));
		this.server = server;
	}
	
	public ApplicationServer getServer() {
		return this.server;
	}
	
}
