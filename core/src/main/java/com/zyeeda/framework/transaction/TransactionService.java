package com.zyeeda.framework.transaction;

import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import com.zyeeda.framework.service.Service;

public interface TransactionService extends Service {

	public TransactionManager getTransactionManager() throws TransactionServiceException;
	
	public UserTransaction getTransaction() throws TransactionServiceException;
	
	public TransactionSynchronizationRegistry getTransactionSynchronizationRegistry() throws TransactionServiceException;
	
}
