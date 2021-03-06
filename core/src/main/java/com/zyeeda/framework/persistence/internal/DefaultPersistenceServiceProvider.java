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
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.hibernate.cfg.beanvalidation.BeanValidationEventListener;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.zyeeda.framework.persistence.AutoRevisionEventListener;
import com.zyeeda.framework.persistence.AutoRevisionEventListener;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.validation.ValidationService;

/**
 * Persistence service using Hibernate.
 * 
 * @author		Rui Tang
 * @version 	%I%, %G%
 * @since		1.0
 */
@ServiceId("default-persistence-service")
@Marker(Primary.class)
public class DefaultPersistenceServiceProvider extends AbstractPersistenceServiceProvider implements PersistenceService {
	
	private final static Logger logger = LoggerFactory.getLogger(DefaultPersistenceServiceProvider.class);
	
	private final static String PERSISTENCE_UNIT_NAME = "default";
	//private final static String ZYEEDA_FRAMEWORK_ENTITY_CLASSES_MANIFEST_ENTRY_NAME = "Zyeeda-Framework-Entity-Classes";
    
    // Injected
    private final ValidationService validationSvc;
    private final SecurityService<?> securitySvc;
    
    public DefaultPersistenceServiceProvider(
    		@Primary ValidationService validationSvc, 
    		@Primary SecurityService<?> securitySvc, 
    		RegistryShutdownHub shutdownHub) {
    	super(shutdownHub);
    	this.validationSvc = validationSvc;
    	this.securitySvc = securitySvc;
    }

	@Override
	public void start() throws Exception {
		Ejb3Configuration config = new Ejb3Configuration().configure(PERSISTENCE_UNIT_NAME, null);
		
		config.getEventListeners().setPreInsertEventListeners(
    			new PreInsertEventListener[] {
    					new BeanValidationEventListener(this.validationSvc.getPreInsertValidatorFactory(), new Properties()),
    					new AutoRevisionEventListener(this.securitySvc)
    			});
    	config.getEventListeners().setPreUpdateEventListeners(
    			new PreUpdateEventListener[] {
    					new BeanValidationEventListener(this.validationSvc.getPreUpdateValidatorFactory(), new Properties()),
    					new AutoRevisionEventListener(this.securitySvc)
    			});
    	config.getEventListeners().setPreDeleteEventListeners(
    			new PreDeleteEventListener[] {
    					new BeanValidationEventListener(this.validationSvc.getPreDeleteValidatorFactory(), new Properties())
    			});
    	
    	//this.addMappingClasses(config, ZYEEDA_FRAMEWORK_ENTITY_CLASSES_MANIFEST_ENTRY_NAME);
    	
    	this.setSessionFactory(config.buildEntityManagerFactory());
	}
	
	@Override
	protected Logger getLogger() {
		return logger;
	}

}
