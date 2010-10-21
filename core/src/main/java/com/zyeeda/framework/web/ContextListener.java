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
package com.zyeeda.framework.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.tapestry5.ioc.IOCUtilities;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.def.ContributionDef;

import com.zyeeda.framework.config.internal.ConfigurationServiceContributionDef;
import com.zyeeda.framework.FrameworkConstants;
import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.ioc.CustomModuleDef;

/**
 * Context listener.
 * When the context is initialized, construct and start the server then bind it to JNDI.
 * 
 * @author		Rui Tang
 * @version		%I%, %G%
 * @since		1.0
 */
public class ContextListener implements ServletContextListener {

    private final static Logger logger = LoggerFactory.getLogger(ContextListener.class);

    private final RegistryBuilder builder = new RegistryBuilder();
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
    	LoggerHelper.info(logger, "context initialized");
    	
    	try {
        	ServletContext context = event.getServletContext();
            
            IOCUtilities.addDefaultModules(builder);
            ContributionDef contributionDef = new ConfigurationServiceContributionDef(context);
            builder.add(new CustomModuleDef(contributionDef));
            builder.add(this.provideExtraModules());
            Registry registry = builder.build();
            
            context.setAttribute(FrameworkConstants.SERVICE_REGISTRY, registry);
            
            registry.performRegistryStartup();
    	} catch (Throwable t) {
    		LoggerHelper.error(logger, t.getMessage(), t);
    		System.exit(1);
    	}
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent event) {
    	LoggerHelper.info(logger, "context destroyed");
    	
    	try {
	    	ServletContext context = event.getServletContext();
	    	Registry registry = (Registry) context.getAttribute(FrameworkConstants.SERVICE_REGISTRY);
	    	registry.shutdown();
	    	context.removeAttribute(FrameworkConstants.SERVICE_REGISTRY);
    	} catch (Throwable t) {
    		LoggerHelper.error(logger, t.getMessage(), t);
    		System.exit(1);
    	}
    }
    
    protected Class<?>[] provideExtraModules() {
    	return new Class<?>[0];
    }

}
