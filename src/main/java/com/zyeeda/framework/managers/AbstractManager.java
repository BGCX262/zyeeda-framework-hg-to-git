package com.zyeeda.framework.managers;

import com.zyeeda.framework.services.Server;

public class AbstractManager {

	private Server server;
	
	public AbstractManager(Server server) {
		this.server = server;
	}
	
	protected Server getServer() {
		return this.server;
	}
}
