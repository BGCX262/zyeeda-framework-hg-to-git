package com.zyeeda.framework.transaction.internal;

import java.net.URL;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.slf4j.Logger;

import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.service.AbstractService;
import com.zyeeda.framework.transaction.TransactionService;

import bitronix.tm.Configuration;
import bitronix.tm.TransactionManagerServices;

@Marker(Primary.class)
@ServiceId("BitronixTransactionServiceProvider")
public class BitronixTransactionServiceProvider extends AbstractService implements TransactionService {

	private final static String JNDI_USER_TRANSACTION_NAME = "btmTransactionManager";
	private final static String JNDI_TRANSACTION_SYNCHRONIZATION_REGISTRY_NAME = "btmSynchronizationRegistry";
	
	public BitronixTransactionServiceProvider(Logger logger, RegistryShutdownHub shutdownHub) {
		super(logger, shutdownHub);
	}
	
	@Override
	public void start() {
		Configuration cfg = TransactionManagerServices.getConfiguration();
		URL url = this.getClass().getClassLoader().getResource("bitronix-datasources.properties");
		LoggerHelper.debug(this.getLogger(), "resource configuration file name = {}", url.getPath());
		cfg.setResourceConfigurationFilename(url.getPath());
		cfg.setJndiUserTransactionName(JNDI_USER_TRANSACTION_NAME);
		cfg.setJndiTransactionSynchronizationRegistryName(JNDI_TRANSACTION_SYNCHRONIZATION_REGISTRY_NAME);
		TransactionManagerServices.getTransactionManager();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public UserTransaction getTransaction() throws Exception {
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "bitronix.tm.jndi.BitronixInitialContextFactory");
		Context ctx = new InitialContext(env);

		return (UserTransaction) ctx.lookup(JNDI_USER_TRANSACTION_NAME);
	}
	
}
