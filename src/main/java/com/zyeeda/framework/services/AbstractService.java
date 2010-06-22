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
package com.zyeeda.framework.services;

import org.apache.commons.configuration.Configuration;

/**
 * Abstract service.
 * 
 * @author		Rui Tang
 * @version 	%I%, %G%
 * @since		1.0
 */
public abstract class AbstractService implements Service {
	
	private Configuration config;
	private String name;
	private Server server;
    private ServiceState state = ServiceState.NEW;
    
    public AbstractService(String name, Server server) {
    	this.name = name;
    	this.server = server;
    }
    
    @Override
    public String getName() {
    	return this.name;
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
    public void init(Configuration config) throws Exception {
    	this.config = config;
    }
    
	@Override
	public void start() throws Exception {
	}

	@Override
	public void stop() throws Exception {
	}
    
    @Override
    public Configuration getConfiguration() {
    	return this.config;
    }
    
    public Server getServer() {
    	return this.server;
    }
    
}
