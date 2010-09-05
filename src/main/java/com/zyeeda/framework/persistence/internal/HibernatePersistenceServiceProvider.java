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
package com.zyeeda.framework.persistence.internal;

import java.util.Properties;

import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.beanvalidation.BeanValidationEventListener;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEventListener;
import org.slf4j.Logger;

import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.service.AbstractService;
import com.zyeeda.framework.validation.ValidationService;

/**
 * Persistence service using Hibernate.
 * 
 * @author		Rui Tang
 * @version 	%I%, %G%
 * @since		1.0
 */
@ServiceId("HibernatePersistenceServiceProvider")
@Marker(Primary.class)
public class HibernatePersistenceServiceProvider extends AbstractService implements PersistenceService {
	
    private final ThreadLocal<Session> sessionThreadLocal = new ThreadLocal<Session>();
    //private final ThreadLocal<Integer> countThreadLocal = new ThreadLocal<Integer>();
    
    // Injected
    private final ValidationService validationSvc;
    private final Logger logger;
    
    private SessionFactory sessionFactory;
    
    public HibernatePersistenceServiceProvider(ValidationService validationSvc, Logger logger) {
    	this.validationSvc = validationSvc;
    	this.logger = logger;
    }

	@Override
	public void start() throws Exception {
    	Configuration config = new AnnotationConfiguration().configure();
    	config.getEventListeners().setPreInsertEventListeners(
    			new PreInsertEventListener[] {
    					new BeanValidationEventListener(this.validationSvc.getValidatorFactory(), new Properties())});
    	config.getEventListeners().setPreUpdateEventListeners(
    			new PreUpdateEventListener[] {
    					new BeanValidationEventListener(this.validationSvc.getValidatorFactory(), new Properties())});
    	config.getEventListeners().setPreDeleteEventListeners(
    			new PreDeleteEventListener[] {
    					new BeanValidationEventListener(this.validationSvc.getValidatorFactory(), new Properties())});
        
    	this.sessionFactory = config.buildSessionFactory();
	}

	@Override
	public void stop() throws Exception {
        this.sessionFactory.close();
        this.sessionFactory = null;
	}
	
	@Override
	public Session openSession() {
		Session session = this.sessionThreadLocal.get();
        if (session == null) {
            session = this.sessionFactory.openSession();
            this.sessionThreadLocal.set(session);
        }

        LoggerHelper.debug(logger, "Open session.");
        return session;
	}

	@Override
    public void closeSession() {
        Session session = this.sessionThreadLocal.get();
        if (session == null) {
        	LoggerHelper.debug(logger, "No session opened.");
        	return;
        }
        
        session.close();
        this.sessionThreadLocal.remove();
        LoggerHelper.debug(logger, "Close session.");
    }
	
	/*@Override
    public Session openSession() {
    	Integer count = countThreadLocal.get();
    	if (count == null) {
    		count = 0;
    	}
    	countThreadLocal.set(++count);
    	
        Session session = sessionThreadLocal.get();
        if (session == null) {
            session = this.sessionFactory.openSession();
            sessionThreadLocal.set(session);
        }

        return session;
    }*/

	/*@Override
    public void closeSession() {
    	Integer count = countThreadLocal.get();
    	if (count == null || count == 0) {
    		LoggerHelper.trace(logger, "会话未开启");
    		return;
    	}
    	countThreadLocal.set(--count);
    	if (count > 0) {
    		LoggerHelper.trace(logger, "会话未关闭");
    		return;
    	}
    	this.realCloseSession();
    }*/

    /*private void realCloseSession() {
        Session session = sessionThreadLocal.get();
        sessionThreadLocal.remove();
        countThreadLocal.remove();

        if (session != null) {
            session.close();
        }

        LoggerHelper.debug(logger, "会话已关闭");
    }*/
    
}
