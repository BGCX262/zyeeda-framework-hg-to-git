package com.zyeeda.framework.services;

import org.hibernate.Session;

import com.zyeeda.framework.managers.EntityManager;

public interface PersistenceService extends Service {

	public Session openSession();
	
	public void closeSession();
	
	public EntityManager getEntityManager();
	
}
