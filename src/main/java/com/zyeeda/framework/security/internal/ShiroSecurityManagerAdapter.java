package com.zyeeda.framework.security.internal;

import org.apache.shiro.mgt.SecurityManager;

import com.zyeeda.framework.security.SecurityManagerAdapter;
import com.zyeeda.framework.server.ApplicationServer;

public class ShiroSecurityManagerAdapter implements SecurityManagerAdapter<SecurityManager> {

	private ApplicationServer server;
	private SecurityManager securityManager;
	
	public ShiroSecurityManagerAdapter(ApplicationServer server, SecurityManager securityManager) {
		this.server = server;
		this.securityManager = securityManager;
	}

	@Override
	public ApplicationServer getServer() {
		return this.server;
	}

	@Override
	public SecurityManager getSecurityManager() {
		return this.securityManager;
	}
	
}
