package com.zyeeda.framework.services;

import org.hibernate.Session;


public interface PersistenceService extends Service {

	public Session openSession();
	
	public void closeSession();
	
}
