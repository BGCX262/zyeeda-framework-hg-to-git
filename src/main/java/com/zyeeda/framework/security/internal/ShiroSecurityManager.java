package com.zyeeda.framework.security.internal;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

import com.zyeeda.framework.security.CombinedRealm;
import com.zyeeda.framework.server.ApplicationServer;

public class ShiroSecurityManager extends DefaultWebSecurityManager {

	private ApplicationServer server;
	
	public ShiroSecurityManager(ApplicationServer server) {
		super();
		this.server = server;
		setRealm(getRealm());
	}
	
	private Realm getRealm() {
		CombinedRealm realm = new CombinedRealm();
		return realm;
	}
	
	public ApplicationServer getServer() {
		return this.server;
	}
	
}
