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

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.helpers.LoggerHelper;

/**
 * Simple application server.
 * 
 * @author		Rui Tang
 * @version		%I%, %G%
 * @since		1.0
 */
public class Server implements Service {

	private static final Logger logger = LoggerFactory.getLogger(Server.class);
	
	private static final String CONFIGURATION_FILE_NAME = "/server.config.xml";
	public static final String SERVER_ROOT = "serverRoot";
	
	private File serverRoot;
	private XMLConfiguration serverConfig;
	private ServiceState state = ServiceState.NEW;

	private List<Service> serviceList = new LinkedList<Service>();
	private Map<String, Service> serviceMap = new HashMap<String, Service>();
	
	@Override
	public void init(Configuration config) throws Exception {
		String serverRootString = config.getString(SERVER_ROOT);
		LoggerHelper.debug(logger, "server root = {}", serverRootString);
		
		this.serverRoot = new File(serverRootString);
		this.serverConfig = new XMLConfiguration();
		
		InputStream is = this.getClass().getResourceAsStream(CONFIGURATION_FILE_NAME);
		LoggerHelper.debug(logger, "config file input stream = {}", is);
		if (is != null) {
			InputStreamReader reader = null;
			try {
				reader = new InputStreamReader(is, "UTF-8");
				this.serverConfig.load(reader);
				
				if (logger.isDebugEnabled()) {
					logger.debug("server config = {}", this.serverConfig);
					if (this.serverConfig != null) {
						for (Iterator<?> it = this.serverConfig.getKeys(); it.hasNext(); ) {
							String key = (String) it.next();
							logger.debug("{} = {}", key, this.serverConfig.getProperty(key));
						}
					}
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
				if (is != null) {
					is.close();
				}
			}
		}
	}

	@Override
	public void start() throws Exception {
		for (Service service : this.serviceList) {
			if (logger.isDebugEnabled()) {
				logger.debug("proxy service class name = {}", service.getClass().getSimpleName());
				logger.debug("proxy service name = {}", service.getName());
			}
			service.init(this.serverConfig.configurationAt(service.getName()));
			service.start();
		}
	}
	
	@Override
	public void stop() throws Exception {
		for (Iterator<Service> it = this.serviceList.iterator(); it.hasNext(); ) {
			Service service = it.next();
			service.stop();
			it.remove();
			this.serviceMap.remove(service.getName());
		}
	}
	
	protected void addService(Service service) throws ServiceNameDuplicateException {
		Service tempSvc = this.serviceMap.get(service.getName());
		if (tempSvc != null) {
			throw new ServiceNameDuplicateException(service.getName());
		}
		
		this.serviceList.add(service);
		this.serviceMap.put(service.getName(), service);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Service> T getService(String serviceName) {
		return (T) this.serviceMap.get(serviceName);
	}
	
	public Configuration getConfiguration() {
		return this.serverConfig;
	}

	public File mapPath(String relativePath) {
		return new File(serverRoot, relativePath);
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
    public String getName() {
    	return "server";
    }

}
