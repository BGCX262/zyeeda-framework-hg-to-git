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
 * Service interface.
 *
 * @author		Rui Tang
 * @version		%I%, %G%
 * @since		1.0
 */
public interface Service {
	
	String getName();
	
    void init(Configuration config) throws Exception;
    
    void start() throws Exception;

    void stop() throws Exception;
    
    ServiceState getState();
    
    void changeState(ServiceState state);
    
    Configuration getConfiguration();
    
}
