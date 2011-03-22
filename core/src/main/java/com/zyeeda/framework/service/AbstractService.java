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
package com.zyeeda.framework.service;

import org.apache.commons.configuration.Configuration;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.internal.util.ClasspathResource;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.config.ConfigurationService;

/**
 * Abstract service.
 * 
 * @author		Rui Tang
 * @version 	%I%, %G%
 * @since		1.0
 */
public abstract class AbstractService implements Service, RegistryShutdownListener {
	
	private final static Logger logger = LoggerFactory.getLogger(AbstractService.class);
	
    private ServiceState state = ServiceState.NEW;

    protected AbstractService(RegistryShutdownHub shutdownHub) {
    	shutdownHub.addRegistryShutdownListener(this);
    }
    
    @Override
    public ServiceState getState() {
        return this.state;
    }
    
    @Override
    public void changeState(ServiceState state) {
    	this.state = state;
    }
    
	@Override
	public void start() throws Exception {
	}

	@Override
	public void stop() throws Exception {
	}
	
	@Override
	public void registryDidShutdown() {
		try {
			logger.info("{} stopping", this.getClass().getSimpleName());
			this.stop();
			logger.info("{} stopped", this.getClass().getSimpleName());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected String getServiceId() {
		return this.getClass().getAnnotation(ServiceId.class).value();
	}
	
	protected Configuration getConfiguration(ConfigurationService configSvc) {
		Resource configFile = new ClasspathResource(String.format("%s.properties", this.getServiceId()));
    	Configuration config = configSvc.getConfiguration(configFile);
    	return config;
	}
    
}
