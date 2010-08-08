package com.zyeeda.framework.services;

public interface PersistenceService<SESSION> extends Service {

	public SESSION openSession();
	
	public void closeSession();
	
}
