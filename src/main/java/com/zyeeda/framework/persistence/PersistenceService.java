package com.zyeeda.framework.persistence;

import com.zyeeda.framework.service.Service;

public interface PersistenceService<SESSION> extends Service {

	public SESSION openSession();
	
	public void closeSession();
	
}
