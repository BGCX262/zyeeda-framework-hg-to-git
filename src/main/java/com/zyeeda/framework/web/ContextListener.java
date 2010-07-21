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

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.server.ApplicationServer;

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

    private ApplicationServer server;
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
    	try {
        	ServletContext context = event.getServletContext();
            String contextRoot = context.getRealPath("/");
            LoggerHelper.debug(logger, "servlet context root = {}", contextRoot);
            
            ApplicationServer server = new ApplicationServer();
            
            PropertiesConfiguration config = new PropertiesConfiguration();
            config.addProperty(ApplicationServer.SERVER_ROOT, contextRoot);
            server.init(config);
            
            server.start();
            
            context.setAttribute(ApplicationServer.class.getName(), server);
    	} catch (Throwable t) {
        	LoggerHelper.error(logger, t.getMessage(), t);
            System.exit(1);
    	}
    }

    /*@Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        String serverJndiName = context.getInitParameter(GlobalConstants.SERVER_JNDI_NAME);
        if (serverJndiName == null) {
        	LoggerHelper.error(logger, "{} is not specified", GlobalConstants.SERVER_JNDI_NAME);
        	System.exit(1);
        }
        
        // NOTE
        // Weblogic doesn't support
        // String contextRoot = context.getRealPath("/");
        
        String contextRoot = null;
        try {
            contextRoot = context.getRealPath("/");
            LoggerHelper.debug(logger, "servlet context root = {}", contextRoot);
            
            server = (Server) JndiUtils.getObjectFromJndi(serverJndiName);
            
            PropertiesConfiguration config = new PropertiesConfiguration();
            config.addProperty(Server.SERVER_ROOT, contextRoot);
            server.init(config);
            
            server.start();
        } catch (Throwable e) {
        	LoggerHelper.error(logger, e.getMessage(), e);
            System.exit(1);
        }
    }*/

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        try {
            if (server != null) {
                server.stop();
            }
        } catch (Throwable e) {
        	LoggerHelper.error(logger, e.getMessage(), e);
        }
    }
}
