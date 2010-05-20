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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Charactor encoding filter.
 * This filter is to fix the encoding problems that are caused by incorrect implementation of Web browsers. 
 *
 * @author		Rui Tang
 * @version		%I%, %G%
 * @since		1.0
 */
public class CharacterEncodingFilter implements Filter {
	
    private String encoding;
    private boolean forceEncoding = false;

    @Override
    public void init(FilterConfig config) throws ServletException {
        encoding = config.getInitParameter("encoding");
        forceEncoding = BooleanUtils.toBoolean(config.getInitParameter("forceEncoding"));
        if (StringUtils.isBlank(encoding)) {
            this.setEncoding("UTF-8");
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {
    	if (this.encoding != null && (this.forceEncoding || StringUtils.isBlank(req.getCharacterEncoding()))) {
    		req.setCharacterEncoding(encoding);
            if (this.forceEncoding) {
                res.setCharacterEncoding(this.encoding);
            }
    	} 
    	
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }

    public void setForceEncoding(boolean forceEncoding) {
        this.forceEncoding = forceEncoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

}
