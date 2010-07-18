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
package com.zyeeda.framework.services.internal;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.services.AbstractService;
import com.zyeeda.framework.services.PersistenceService;
import com.zyeeda.framework.services.ApplicationServer;

/**
 * Persistence service using Hibernate.
 * 
 * @author		Rui Tang
 * @version 	%I%, %G%
 * @since		1.0
 */
public class HibernatePersistenceServiceProvider extends AbstractService implements PersistenceService {
	
	private static final Logger logger = LoggerFactory.getLogger(HibernatePersistenceServiceProvider.class);
	
    private static final ThreadLocal<Session> sessionThreadLocal = new ThreadLocal<Session>();
    private static final ThreadLocal<Integer> countThreadLocal = new ThreadLocal<Integer>();
    private Configuration config;
    private SessionFactory sessionFactory;
    
    public HibernatePersistenceServiceProvider(ApplicationServer server, String name) {
    	super(server, name);
    }
    
    public HibernatePersistenceServiceProvider(ApplicationServer server) {
    	super(server, HibernatePersistenceServiceProvider.class.getSimpleName());
    }

	@Override
	public void start() throws Exception {
    	this.config = new Configuration().configure();
        this.sessionFactory = config.buildSessionFactory();
	}

	@Override
	public void stop() throws Exception {
        this.sessionFactory.close();
        this.sessionFactory = null;
        this.config = null;
	}
	
	@Override
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
    }

	@Override
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
    }

    private void realCloseSession() {
        Session session = sessionThreadLocal.get();
        sessionThreadLocal.remove();
        countThreadLocal.remove();

        if (session != null) {
            session.close();
        }

        LoggerHelper.debug(logger, "会话已关闭");
    }
    
}
