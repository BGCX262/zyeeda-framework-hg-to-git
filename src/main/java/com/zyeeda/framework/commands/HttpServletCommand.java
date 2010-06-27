/*
 * Copyright 2010 Zyeeda Co. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.zyeeda.framework.commands;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.helpers.LoggerHelper;

public class HttpServletCommand implements Command {

    private final static Logger logger = LoggerFactory.getLogger(HttpServletCommand.class);

    private HttpServletRequest request;
    private HttpServletResponse response;
    
    private String scope;
    private String operation;
    private Object returnValue;

    public HttpServletCommand(HttpServletRequest request, HttpServletResponse response) throws IllegalRequestException {
    	this.request = request;
    	this.response = response;
    	
    	String path = request.getServletPath();
    	LoggerHelper.debug(logger, "servlet path = {}", path);
    	String[] parts = StringUtils.split(path, '/');
    	
    	if (parts.length != 2) {
    		throw new IllegalRequestException(request);
    	}
    	
    	this.scope = parts[0];
    	this.operation = parts[1];
    }
    
    @Override
    public String getScope() {
    	return this.scope;
    }

    @Override
    public String getOperation() {
        return this.operation;
    }
    
    @Override
    public String getExecutor() {
        return this.request.getRemoteUser();
    }
    
    @Override
    public String getId() {
    	return this.getParameter("id");
    }

	@Override
	@SuppressWarnings("unchecked")
    public Map<String, Object> getParameterMap() {
        return this.request.getParameterMap();
    }

    @Override
    public String getParameter(String name) {
        return this.request.getParameter(name);
    }

    @Override
    public String[] getParameterValues(String name) {
        return this.request.getParameterValues(name);
    }
	
	public HttpServletRequest getRequest() {
		return this.request;
	}
	
	public HttpServletResponse getResponse() {
		return this.response;
	}
	
	@Override
	public void setReturnValue(Object outputValue) {
		this.returnValue = outputValue;
	}
	
	@Override
	public Object getReturnValue() {
		return this.returnValue;
	}

}
