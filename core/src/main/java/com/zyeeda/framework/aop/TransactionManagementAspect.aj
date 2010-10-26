package com.zyeeda.framework.aop;

import javax.servlet.ServletContext;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.tapestry5.ioc.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.FrameworkConstants;
import com.zyeeda.framework.aop.Transactional;
import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.transaction.TransactionService;
import com.zyeeda.framework.transaction.internal.BitronixTransactionServiceProvider;
import com.zyeeda.framework.utils.IocUtils;

public aspect TransactionManagementAspect {
	
	private final static Logger logger = LoggerFactory.getLogger(TransactionManagementAspect.class);
	
	pointcut txEnabledMethod(ServletContext ctx) : 
		execution(@Transactional public * *.*(ServletContext, ..)) 
		&& args(ctx);
	
	Object around(ServletContext ctx) : txEnabledMethod(ctx) {
		Registry reg = (Registry) ctx.getAttribute(FrameworkConstants.SERVICE_REGISTRY);
		
		TransactionService txSvc = reg.getService(IocUtils.getServiceId(BitronixTransactionServiceProvider.class), TransactionService.class);
		UserTransaction utx = null;
		try {
			utx = txSvc.getTransaction();
			if (utx.getStatus() == Status.STATUS_ACTIVE) {
				LoggerHelper.info(logger, "transaction is active");
				return proceed(ctx);
			}

			LoggerHelper.info(logger, "trying to begin transaction");
			utx.begin();
			LoggerHelper.info(logger, "transaction begin");
			Object retValue = proceed(ctx);
			utx.commit();
			LoggerHelper.info(logger, "transaction commit");
			return retValue;
		} catch (Throwable t) {
			try {
				if (utx != null && utx.getStatus() == Status.STATUS_ACTIVE) {
					utx.rollback();
					LoggerHelper.info(logger, "transaction rollback");
				}
			} catch (IllegalStateException e) {
				LoggerHelper.error(logger, e.getMessage(), e);
			} catch (SecurityException e) {
				LoggerHelper.error(logger, e.getMessage(), e);
			} catch (SystemException e) {
				LoggerHelper.error(logger, e.getMessage(), e);
			}
			throw new RuntimeException(t);
		}
	}


}