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

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * Freemarker template service.
 *
 * @author		Rui Tang
 * @version		%I%, %G%
 * @since		1.0
 */
public class TemplateService extends AbstractService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);
    
    private File tplRoot;
    private Configuration config;

    @Override
    public void init(Properties properties) {
    	this.tplRoot = new File(properties.getProperty(ServerProperties.TEMPLATE_REPOSITORY_ROOT_KEY));
    }

    @Override
    public void start() throws Exception {
        this.config = new Configuration();
        this.config.setDirectoryForTemplateLoading(this.tplRoot);
        this.config.setDefaultEncoding("UTF-8");
        this.config.setOutputEncoding("UTF-8");
        this.config.setDateFormat("yyyy-MM-dd");
        this.config.setTimeFormat("HH:mm:ss");
        this.config.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");
        this.config.setObjectWrapper(new DefaultObjectWrapper());
        this.config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }
    
    public void stop() throws Exception {
    	this.config = null;
    	this.tplRoot = null;
    }

    public void paint(String tplPath, Writer out, Map<?, ?> args) throws IOException, TemplateException {
		if (logger.isDebugEnabled()) {
			logger.debug("painting template = {}", tplPath);
			logger.debug("template varables = {}", args);
		}
		Template template = this.config.getTemplate(tplPath);
		template.process(args, out);
    }

    public void paint(String tplPath, Writer out) throws IOException,TemplateException {
		Map<String, Object> args = new HashMap<String, Object>();
		this.paint(tplPath, out, args);
    }

}
