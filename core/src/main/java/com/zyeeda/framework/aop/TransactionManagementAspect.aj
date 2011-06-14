package com.zyeeda.framework.aop;

import javax.servlet.ServletContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.apache.tapestry5.ioc.Registry;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.FrameworkConstants;
import com.zyeeda.framework.aop.Transactional;
import com.zyeeda.framework.transaction.TransactionService;
import com.zyeeda.framework.transaction.internal.BitronixTransactionServiceProvider;
import com.zyeeda.framework.utils.IocUtils;

public aspect TransactionManagementAspect {
	
	private final static Logger logger = LoggerFactory.getLogger(TransactionManagementAspect.class);
	
	pointcut txEnabledMethod(ServletContext ctx) : 
		execution(@Transactional public * *.*(ServletContext, ..)) 
		&& args(ctx);
	
	@SuppressAjWarnings("adviceDidNotMatch")
	Object around(ServletContext ctx) : txEnabledMethod(ctx) {
		Registry reg = (Registry) ctx.getAttribute(FrameworkConstants.SERVICE_REGISTRY);
		
		TransactionService txSvc = reg.getService(IocUtils.getServiceId(BitronixTransactionServiceProvider.class), TransactionService.class);
		UserTransaction utx = null;
		try {
			utx = txSvc.getTransaction();
			if (utx.getStatus() == Status.STATUS_ACTIVE) {
				logger.debug("transaction is active");
				return proceed(ctx);
			}

			logger.debug("trying to begin transaction");
			utx.begin();
			logger.debug("transaction begin");
			Object retValue = proceed(ctx);
			utx.commit();
			logger.debug("transaction commit");
			return retValue;
		} catch (Throwable t) {
			if (utx != null) {
				try {
					utx.rollback();
				} catch (Throwable t2) {
					logger.error("Transaction roll back failed.", t2);
				}
				logger.debug("transaction rollback");
			}
			throw new RuntimeException(t);
		}
	}

}