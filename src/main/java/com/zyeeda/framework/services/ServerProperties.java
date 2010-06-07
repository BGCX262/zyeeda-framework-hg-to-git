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

import java.util.Properties;
import java.util.Enumeration;

import org.slf4j.Logger;

/**
 * Server properties.
 *
 * @author		Rui Tang
 * @version		%I%, %G%
 * @since		1.0
 */
@Deprecated
public class ServerProperties extends Properties {

	private static final long serialVersionUID = -428103055421634070L;
	
	public static final String TEMPLATE_REPOSITORY_ROOT_KEY = "web.site.template.root";
	public static final String SERIAL_NUMBER_LENGTH = "serial.number.length";
	public static final String RENAME_SPLIT_SYMBOL_KEY = "rename_split_symbol";
	
    private static final String DEFAULT_WEB_SITE_TEMPLATE = "/WEB-INF/tpl/";
    private static final String DEFAULT_SERIAL_NUMBER_LENGTH = "10";
    private static final String RENAME_SPLIT_SYMBOL_DEFAULT_VALUE = "##";

    public ServerProperties() {
        super();
        this.setProperty(TEMPLATE_REPOSITORY_ROOT_KEY, DEFAULT_WEB_SITE_TEMPLATE);
        this.setProperty(SERIAL_NUMBER_LENGTH, DEFAULT_SERIAL_NUMBER_LENGTH);
        this.setProperty(RENAME_SPLIT_SYMBOL_KEY, RENAME_SPLIT_SYMBOL_DEFAULT_VALUE);
    }

    public void dump(Logger logger) {
        for (Enumeration<?> e = this.propertyNames(); e.hasMoreElements();) {
            String key = (String)e.nextElement();
            logger.debug("{} = {}", key, this.getProperty(key));
        }
    }
    
    public int getIntProperty(String key) {
    	String prop = this.getProperty(key);
    	return Integer.parseInt(prop);
    }

}
