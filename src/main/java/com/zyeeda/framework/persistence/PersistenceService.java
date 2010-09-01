package com.zyeeda.framework.persistence;

import org.hibernate.Session;

import com.zyeeda.framework.service.Service;

public interface PersistenceService extends Service {

	public Session openSession();
	
	public void closeSession();
	
}
