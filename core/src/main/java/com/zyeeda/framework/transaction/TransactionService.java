package com.zyeeda.framework.transaction;

import javax.transaction.UserTransaction;

import com.zyeeda.framework.service.Service;

public interface TransactionService extends Service {

	public UserTransaction getTransaction() throws Exception;
	
}
