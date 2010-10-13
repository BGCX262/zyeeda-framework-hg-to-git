package com.zyeeda.framework.transaction;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.zyeeda.framework.service.Service;

public interface TransactionService extends Service {

	public TransactionManager getTransactionManager();
	
	public UserTransaction getTransaction() throws Exception;
	
}
