/*
 * Copyright 2010 Zyeeda Co. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.zyeeda.framework.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.transaction.UserTransaction;

import org.apache.tapestry5.ioc.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.FrameworkConstants;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.persistence.internal.DefaultPersistenceServiceProvider;
import com.zyeeda.framework.transaction.TransactionService;
import com.zyeeda.framework.transaction.internal.DefaultTransactionServiceProvider;
import com.zyeeda.framework.utils.IocUtils;

/**
 * Open session in view servlet filter.
 * 
 * @author		Rui Tang
 * @version 	%I%, %G%
 * @since		1.0
 */
public class OpenSessionInViewFilter implements Filter {
	
	private final static Logger logger = LoggerFactory.getLogger(OpenSessionInViewFilter.class);
	
    private FilterConfig config;

    @Override
    public void init(FilterConfig config) throws ServletException {
        this.config = config;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	
    	Registry reg = (Registry) this.config.getServletContext().getAttribute(FrameworkConstants.SERVICE_REGISTRY);
    	
    	PersistenceService persistenceSvc = null;
        UserTransaction utx = null;
        try {
        	TransactionService txSvc = reg.getService(IocUtils.getServiceId(DefaultTransactionServiceProvider.class), TransactionService.class);
        	persistenceSvc = reg.getService(IocUtils.getServiceId(DefaultPersistenceServiceProvider.class), PersistenceService.class);
        	
        	utx = txSvc.getTransaction();
        	logger.debug("tx status before begin = {}", utx.getStatus());
        	utx.begin();
        	logger.debug("tx status after begin = {}", utx.getStatus());
            persistenceSvc.openSession();
            chain.doFilter(request, response);
            logger.debug("tx status before commit = {}", utx.getStatus());
            utx.commit();
            logger.debug("tx status after commit = {}", utx.getStatus());
        } catch (Throwable t) {
        	try {
				if (utx != null) {
					logger.debug("tx status before rollback = {}", utx.getStatus());
					utx.rollback();
					logger.debug("tx status after successfully rollback = {}", utx.getStatus());
				}
			} catch (Throwable t2) {
				logger.error("Cannot rollback transaction.", t2);
			}
        	throw new ServletException(t);
		} finally {
            if (persistenceSvc != null) {
                persistenceSvc.closeSession();
            }
        }
    }
}
