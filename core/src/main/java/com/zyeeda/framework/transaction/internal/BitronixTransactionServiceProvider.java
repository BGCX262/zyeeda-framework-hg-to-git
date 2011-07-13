package com.zyeeda.framework.transaction.internal;

import java.net.URL;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.Configuration;
import bitronix.tm.TransactionManagerServices;

import com.zyeeda.framework.service.AbstractService;
import com.zyeeda.framework.transaction.TransactionService;
import com.zyeeda.framework.transaction.TransactionServiceException;
import com.zyeeda.framework.transaction.annotations.BTM;

@Marker(BTM.class)
@ServiceId("bitronix-transaction-service")
public class BitronixTransactionServiceProvider extends AbstractService implements TransactionService {

	private static final Logger logger = LoggerFactory.getLogger(BitronixTransactionServiceProvider.class);
	
	private final static String JNDI_USER_TRANSACTION_NAME = "btmTransactionManager";
	private final static String JNDI_TRANSACTION_SYNCHRONIZATION_REGISTRY_NAME = "btmSynchronizationRegistry";
	
	public BitronixTransactionServiceProvider(RegistryShutdownHub shutdownHub) {
		super(shutdownHub);
	}
	
	@Override
	public void start() {
		Configuration cfg = TransactionManagerServices.getConfiguration();
		URL url = this.getClass().getClassLoader().getResource("bitronix-datasources.properties");
		logger.debug("resource configuration file name = {}", url.getPath());
		cfg.setResourceConfigurationFilename(url.getPath());
		cfg.setJndiUserTransactionName(JNDI_USER_TRANSACTION_NAME);
		cfg.setJndiTransactionSynchronizationRegistryName(JNDI_TRANSACTION_SYNCHRONIZATION_REGISTRY_NAME);
		TransactionManagerServices.getTransactionManager();
	}
	
	@Override
	public void stop() {
		TransactionManagerServices.getTransactionManager().shutdown();
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public UserTransaction getTransaction() throws TransactionServiceException {
		try {
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "bitronix.tm.jndi.BitronixInitialContextFactory");
			Context ctx = new InitialContext(env);
			
			UserTransaction utx = (UserTransaction) ctx.lookup(JNDI_USER_TRANSACTION_NAME);
			
			return utx;
		} catch (NamingException e) {
			throw new TransactionServiceException(e);
		}
	}
	
	@Override
	public TransactionManager getTransactionManager() {
		return TransactionManagerServices.getTransactionManager();
	}

	/*
	@Override
	public TransactionSynchronizationRegistry getTransactionSynchronizationRegistry()
			throws TransactionServiceException {
		return TransactionManagerServices.getTransactionSynchronizationRegistry();
	}
	*/
	
}
