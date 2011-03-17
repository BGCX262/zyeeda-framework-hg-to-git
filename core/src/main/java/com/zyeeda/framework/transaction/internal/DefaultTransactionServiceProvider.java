package com.zyeeda.framework.transaction.internal;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;
import javax.transaction.TransactionManager;

import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;

import com.zyeeda.framework.service.AbstractService;
import com.zyeeda.framework.transaction.TransactionService;
import com.zyeeda.framework.transaction.TransactionServiceException;

@Marker(Primary.class)
@ServiceId("default-transaction-service")
public class DefaultTransactionServiceProvider extends AbstractService implements TransactionService {

	public DefaultTransactionServiceProvider(RegistryShutdownHub shutdownHub) {
		super(shutdownHub);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public UserTransaction getTransaction() throws TransactionServiceException {
		try {
			Hashtable env = new Hashtable();
			Context ctx = new InitialContext(env);
			
			UserTransaction utx = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
			
			return utx;
		} catch (NamingException e) {
			throw new TransactionServiceException(e);
		}
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public TransactionManager getTransactionManager() throws TransactionServiceException {
		try {
			Hashtable env = new Hashtable();
			Context ctx = new InitialContext(env);
			
			TransactionManager tm = (TransactionManager) ctx.lookup("java:comp/TransactionManager");
			
			return tm;
		} catch (NamingException e) {
			throw new TransactionServiceException(e);
		}
	}

}
