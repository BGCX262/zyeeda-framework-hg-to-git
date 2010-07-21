package com.zyeeda.framework.security;

import com.zyeeda.framework.server.ApplicationServer;

public interface SecurityManagerAdapter<T> {

	public ApplicationServer getServer();
	
	public T getSecurityManager();
	
}
