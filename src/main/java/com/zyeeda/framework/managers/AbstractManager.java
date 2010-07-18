package com.zyeeda.framework.managers;

import com.zyeeda.framework.services.ApplicationServer;

public class AbstractManager {

	private ApplicationServer server;
	
	public AbstractManager(ApplicationServer server) {
		this.server = server;
	}
	
	protected ApplicationServer getServer() {
		return this.server;
	}
}
