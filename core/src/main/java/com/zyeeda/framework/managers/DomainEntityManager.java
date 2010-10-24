package com.zyeeda.framework.managers;

import com.zyeeda.framework.persistence.PersistenceService;

public class DomainEntityManager {

	private PersistenceService persistenceSvc;
	
	public DomainEntityManager(PersistenceService persistenceSvc) {
		this.persistenceSvc = persistenceSvc;
	}
	
	protected PersistenceService getPersistenceService() {
		return this.persistenceSvc;
	}
	
}
