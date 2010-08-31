package com.zyeeda.framework.persistence;

import javax.persistence.EntityManager;

import com.zyeeda.framework.service.Service;

public interface PersistenceService extends Service {

	public EntityManager openSession();
	
	public void closeSession();
	
}
