package com.zyeeda.framework.managers;

import javax.persistence.EntityManager;

public class DomainEntityManager {

	private EntityManager session;
	
	public DomainEntityManager(EntityManager session) {
		this.session = session;
	}
	
	protected EntityManager getSession() {
		return this.session;
	}
	
}
