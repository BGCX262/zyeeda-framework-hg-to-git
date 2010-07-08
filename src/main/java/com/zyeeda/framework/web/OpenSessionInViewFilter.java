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

import java.io.IOException;

import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.zyeeda.framework.GlobalConstants;
import com.zyeeda.framework.services.Server;
import com.zyeeda.framework.services.PersistenceService;
import com.zyeeda.framework.utils.JndiUtils;

/**
 * Open session in view servlet filter.
 * 
 * @author		Rui Tang
 * @version 	%I%, %G%
 * @since		1.0
 */
public class OpenSessionInViewFilter implements Filter {

    private FilterConfig config;

    @Override
    public void init(FilterConfig config) throws ServletException {
        this.config = config;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        PersistenceService svc = null;

        try {
            String jndiServerKey = this.config.getServletContext().getInitParameter(GlobalConstants.SERVER_JNDI_NAME);

            Server server = (Server) JndiUtils.getObjectFromJndi(jndiServerKey);
            svc = server.getService(PersistenceService.class);
            svc.openSession();
            chain.doFilter(request, response);
        } catch (NamingException e) {
            throw new ServletException(e);
        } finally {
            if (svc != null) {
                svc.closeSession();
            }
        }
    }

}
