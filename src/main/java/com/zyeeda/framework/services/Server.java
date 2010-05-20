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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple application server.
 * 
 * @author		Rui Tang
 * @version		%I%, %G%
 * @since		1.0
 */
public class Server extends AbstractService {

	public static final String SERVER_ROOT = "serverRoot";
	public static final String PROPERTIES_FILE_NAME = "server.properties";
	
	private static final Logger logger = LoggerFactory.getLogger(Server.class);

	private File serverRoot;
	private ServerProperties serverProperties;

	private List<Service> serviceList = new LinkedList<Service>();
	private Map<Class<? extends Service>, Service> serviceMap = new HashMap<Class<? extends Service>, Service>();
	
	@Override
	public void init(Properties properties) throws Exception {
		this.serverRoot = new File(properties.getProperty(SERVER_ROOT));
		if (!this.serverRoot.exists()) {
			throw new FileNotFoundException(this.serverRoot.toString());
		}
		
		this.serverProperties = new ServerProperties();
		this.loadProperties();
	}

	private void loadProperties() throws IOException {
		InputStream is = this.getClass().getResourceAsStream(PROPERTIES_FILE_NAME);
		if (is != null) {
			InputStreamReader isr = null;
			try {
				isr = new InputStreamReader(is, "UTF-8");
				this.serverProperties.load(isr);
				if (logger.isDebugEnabled()) {
					this.serverProperties.dump(logger);
				}
			} finally {
				if (isr != null) {
					isr.close();
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
			service.init(this.serverProperties);
			service.start();
		}
	}
	
	@Override
	public void stop() throws Exception {
		for (Iterator<Service> it = this.serviceList.iterator(); it.hasNext(); ) {
			Service service = it.next();
			service.stop();
			it.remove();
			this.serviceMap.remove(service.getClass());
		}
	}
	
	protected void addService(Service service) {
		this.serviceList.add(service);
		this.serviceMap.put(service.getClass(), service);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Service> T getService(Class<T> clazz) {
		return (T) this.serviceMap.get(clazz);
	}
	
	public ServerProperties getProperties() {
		return this.serverProperties;
	}

	public File mapPath(String relativePath) {
		return new File(serverRoot, relativePath);
	}

	/*public void start0() throws Exception {
		this.loadProperties();

		JbpmService jbpmSvc = new JbpmService();
		jbpmSvc.init();
		jbpmSvc.start();
		this.serviceMap.put(jbpmSvc.getClass(), jbpmSvc);

		TemplateService templateSvc = new TemplateService();
		templateSvc.init(mapPath(this.serverProperties
				.getProperty(ServerProperties.WEB_SITE_TEMPLATE_ROOT_KEY)));
		templateSvc.start();
		this.serviceMap.put(templateSvc.getClass(), templateSvc);

		this.initManagers();
		this.initProcesses();
	}

	public void stop0() throws Exception {
		this.destroyProcesses();
		this.destroyManagers();
		
		TemplateService templateSvc = this.getService(TemplateService.class);
		templateSvc.stop();
		this.serviceMap.remove(templateSvc.getClass());

		JbpmService jbpmSvc = this.getService(JbpmService.class);
		jbpmSvc.stop();
		this.serviceMap.remove(jbpmSvc.getClass());
	}
	
	private void initManagers() throws SecurityException, 
			IllegalArgumentException, InstantiationException, 
			IllegalAccessException, ClassNotFoundException, 
			NoSuchMethodException, InvocationTargetException {
		for (String className : this.managerClassNames) {
			Manager obj = (Manager) this.getInstance(className);
			this.managerMap.put(obj.getClass(), obj);
		}
	}

	private void initProcesses() throws SecurityException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			NoSuchMethodException, InvocationTargetException {
		for (String className : this.processClassNames) {
			Process obj = (Process) this.getInstance(className);
			this.processMap.put(obj.getClass(), obj);
		}
	}
	
	private void destroyManagers() {
		this.managerMap.clear();
	}
	
	private void destroyProcesses() {
		this.processMap.clear();
	}

	private Object getInstance(String className) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InvocationTargetException {
		LoggerHelper.debug(logger, "class name = {}", className);
		Class<?> clazz = getClass().getClassLoader().loadClass(className);
		Constructor<?> ctor = clazz.getConstructor(this.getClass());
		return ctor.newInstance(this);
	}

	@SuppressWarnings("unchecked")
	public <T> T getProcess(Class<T> clazz) {
		return (T) this.processMap.get(clazz);
	}*/

}
