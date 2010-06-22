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

import java.util.HashMap;
import java.util.Map;

public class CustomCommand implements Command {

	private String scope;
	private String executor;
    private String operation;
    private String id;
    private Map<String, Object> parameters = new HashMap<String, Object>();
    
    public CustomCommand() {
    }
    
    public CustomCommand(Command cmd) {
    	this.scope = cmd.getScope();
    	this.executor = cmd.getExecutor();
    	this.operation = cmd.getOperation();
    	this.id = cmd.getId();
    	this.parameters = new HashMap<String, Object>(cmd.getParameterMap());
    }
    
    @Override
    public String getScope() {
    	return this.scope;
    }
    public void setScope(String scope) {
    	this.scope = scope;
    }
    
    @Override
    public String getOperation() {
    	return this.operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    @Override
    public String getExecutor() {
    	return this.executor;
    }
    public void setExecutor(String executor) {
        this.executor = executor;
    }
    
    @Override
    public String getId() {
    	return this.id;
    }
    public void setId(String id) {
    	this.id = id;
    }

    @Override
    public String getParameter(String name) {
        Object value = this.parameters.get(name);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String) value; 
        } else if (value instanceof String[]) {
            return (((String[]) value)[0]);
        }
        return value.toString();
    }
    public void addParameter(String name, String value) {
        this.parameters.put(name, value);
    }

    @Override
    public Map<String, Object> getParameterMap() {
        return this.parameters;
    }

    @Override
    public String[] getParameterValues(String name) {
        Object value = this.parameters.get(name);
        if (value == null) {
            return (String[]) null;
        } else if (value instanceof String[]) {
            return (String[]) value;
        } else if (value instanceof String) {
            String values[] = new String[1];
            values[0] = (String) value;
            return values;
        } else {
            String values[] = new String[1];
            values[0] = value.toString();
            return values;
        }
    }

}
