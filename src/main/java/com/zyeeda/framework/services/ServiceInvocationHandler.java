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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.helpers.LoggerHelper;

/**
 * Service invocation handler.
 * To do the common things when starting, stopping or initializing services.
 * This class will simplify the implementation of concrete services.
 * 
 * @author		Rui Tang
 * @version		%I%, %G%
 * @since		1.0
 */
public class ServiceInvocationHandler<T extends Service> implements InvocationHandler {

	private final static Logger logger = LoggerFactory.getLogger(ServiceInvocationHandler.class);

	private T originalService;
	
	@SuppressWarnings("unchecked")
	public Service bind(T originalService) {
		if (originalService == null) {
			throw new IllegalArgumentException("参数为空");
		}
		
		this.originalService = originalService;
		LoggerHelper.debug(logger, "original service = {}", this.originalService);
		
		Service proxy = (T) Proxy.newProxyInstance(
				this.originalService.getClass().getClassLoader(),
				new Class<?>[] {Service.class},
				this);
		
		return proxy;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String methodName = method.getName();
		LoggerHelper.debug(logger, "calling method = {}", methodName);
		
		if ("init".equals(methodName)) {
			if (ServiceState.NEW != this.originalService.getState()
					&& ServiceState.STOPPED != this.originalService.getState()) {
				LoggerHelper.warn(logger, "{} is not in NEW or STOPPED state, but in {} state", this.getServiceName(), this.originalService.getState());
				return false;
			}
			
			Object result = method.invoke(this.originalService, args);
			this.originalService.changeState(ServiceState.READY);
			return result;
		}
		
		if ("start".equals(methodName)) {
			if (ServiceState.READY != this.originalService.getState()) {
				LoggerHelper.warn(logger, "{} is not in READY state, but in {} state", this.getServiceName(), this.originalService.getState());
				return false;
			}

			this.originalService.changeState(ServiceState.STARTING);
			LoggerHelper.info(logger, "starting {}", this.getServiceName());
			Object result = method.invoke(this.originalService, args);
			LoggerHelper.info(logger, "{} started", this.getServiceName());
			this.originalService.changeState(ServiceState.RUNNING);

			return result;
		}
		
		if ("stop".equals(methodName)) {
			if (ServiceState.RUNNING != this.originalService.getState()) {
				LoggerHelper.warn(logger, "{} is not in RUNNING state, but in {} state", this.getServiceName(), this.originalService.getState());
				return false;
			}

			this.originalService.changeState(ServiceState.STARTING);
			LoggerHelper.info(logger, "stopping {}", this.getServiceName());
			Object result = method.invoke(this.originalService, args);
			LoggerHelper.info(logger, "{} stopped", this.getServiceName());
			this.originalService.changeState(ServiceState.STOPPED);
			
			return result;
		}
		
		return method.invoke(this.originalService, args);
	}
	
	private String getServiceName() {
		return this.originalService.getName();
	}

}
