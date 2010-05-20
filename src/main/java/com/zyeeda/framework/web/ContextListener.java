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

import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.services.Server;
import com.zyeeda.framework.utils.JndiUtils;

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
    private final static String SERVER_JNDI_NAME = "server_jndi_name";

    private Server server;

    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        String serverJndiName = context.getInitParameter(SERVER_JNDI_NAME);
        if (serverJndiName == null) {
        	LoggerHelper.error(logger, "{} is not specified", SERVER_JNDI_NAME);
        	System.exit(1);
        }
        Server.setJndiName(serverJndiName);
        
        // NOTE
        // Weblogic doesn't support
        // String contextRoot = context.getRealPath("/");
        
        String contextRoot = null;
        try {
            contextRoot = context.getRealPath("/");
            LoggerHelper.debug(logger, "servlet context root = {}", contextRoot);
            
            server = (Server) JndiUtils.getObjectFromJndi(Server.getJndiName());
            Properties props = new Properties();
            props.setProperty(Server.SERVER_ROOT, contextRoot);
            server.init(props);
            
            server.start();
        } catch (Throwable e) {
        	LoggerHelper.error(logger, e.getMessage(), e);
            System.exit(1);
        }
    }

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
