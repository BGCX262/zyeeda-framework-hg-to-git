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
package com.zyeeda.framework.services.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.server.ApplicationServer;
import com.zyeeda.framework.services.TemplateService;

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
public class FreemarkerTemplateServiceProvider extends AbstractService implements TemplateService {

    private static final Logger logger = LoggerFactory.getLogger(FreemarkerTemplateServiceProvider.class);
    
    public static final String TEMPLATE_REPOSITORY_ROOT = "templateRepositoryRoot";
    public static final String DATE_FORMAT = "dateFormat";
    public static final String TIME_FORMAT = "timeFormat";
    public static final String DATETIME_FORMAT = "datetimeFormat";
    
    private static final String DEFAULT_TEMPLATE_REPOSITORY_ROOT = "/WEB-INF/tpl";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_TIME_FORMAT = "hh:mm:ss";
    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
    
    private File tplRoot;
    private String dateFormat;
    private String timeFormat;
    private String datetimeFormat;
    
    private Configuration config;
    
    public FreemarkerTemplateServiceProvider(ApplicationServer server, String name) {
    	super(server, name);
    }
    
    public FreemarkerTemplateServiceProvider(ApplicationServer server) {
    	super(server, FreemarkerTemplateServiceProvider.class.getSimpleName());
    }

    @Override
    public void init(org.apache.commons.configuration.Configuration config) throws Exception {
    	super.init(config);
    	
    	String tplRootString = config.getString(TEMPLATE_REPOSITORY_ROOT, DEFAULT_TEMPLATE_REPOSITORY_ROOT);
    	LoggerHelper.debug(logger, "template repository root = {}", tplRootString);
    	
    	this.tplRoot = this.getServer().mapPath(tplRootString);
    	if (!this.tplRoot.exists()) {
    		throw new FileNotFoundException(this.tplRoot.toString());
    	}
    	
    	this.dateFormat = config.getString(DATE_FORMAT, DEFAULT_DATE_FORMAT);
    	this.timeFormat = config.getString(TIME_FORMAT, DEFAULT_TIME_FORMAT);
    	this.datetimeFormat = config.getString(DATETIME_FORMAT, DEFAULT_DATETIME_FORMAT);
    	
    	if (logger.isDebugEnabled()) {
    		logger.debug("date format = {}", this.dateFormat);
    		logger.debug("time format = {}", this.timeFormat);
    		logger.debug("datetime format = {}", this.dateFormat);
    	}
    }

    @Override
    public void start() throws Exception {
        this.config = new Configuration();
        this.config.setDirectoryForTemplateLoading(this.tplRoot);
        this.config.setDefaultEncoding("UTF-8");
        this.config.setOutputEncoding("UTF-8");
        this.config.setDateFormat(this.dateFormat);
        this.config.setTimeFormat(this.timeFormat);
        this.config.setDateTimeFormat(this.datetimeFormat);
        this.config.setObjectWrapper(new DefaultObjectWrapper());
        this.config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }
    
    @Override
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
