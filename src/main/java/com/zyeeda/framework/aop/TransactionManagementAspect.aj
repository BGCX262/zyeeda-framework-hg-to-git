package com.zyeeda.framework.aop;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.LoggerSource;
import org.slf4j.Logger;

import com.zyeeda.framework.FrameworkConstants;
import com.zyeeda.framework.aop.Transaction;
import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.persistence.PersistenceService;

public aspect TransactionManagementAspect {
	
	pointcut txEnabledMethod(ServletContext ctx) : 
		execution(@Transaction public * *.*(ServletContext, ..)) 
		&& args(ctx);
	
	Object around(ServletContext ctx) : txEnabledMethod(ctx) {
		Registry reg = (Registry) ctx.getAttribute(FrameworkConstants.SERVICE_REGISTRY);
		
		LoggerSource loggerSource = reg.getService(LoggerSource.class);
		Logger logger = loggerSource.getLogger(TransactionManagementAspect.class);
		
		PersistenceService persistenceSvc = reg.getService(PersistenceService.class);
		EntityManager session = persistenceSvc.openSession();
		try {
			LoggerHelper.info(logger, "trying to begin transaction");
			
			if (session.getTransaction().isActive()) {
				LoggerHelper.info(logger, "transaction is action");
				return proceed(ctx);
			}
			
			session.getTransaction().begin();
			LoggerHelper.info(logger, "transaction begin");
			Object retValue = proceed(ctx);
			session.getTransaction().commit();
			LoggerHelper.info(logger, "transaction commit");
			return retValue;
		} catch (Throwable t) {
			session.getTransaction().rollback();
			LoggerHelper.info(logger, "transaction rollback");
			throw new RuntimeException(t);
		}
	}


}