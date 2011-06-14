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
package com.zyeeda.framework.template.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.config.ConfigurationService;
import com.zyeeda.framework.service.AbstractService;
import com.zyeeda.framework.template.TemplateService;
import com.zyeeda.framework.template.TemplateServiceException;

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
@ServiceId("freemarker-template-service")
@Marker(Primary.class)
public class FreemarkerTemplateServiceProvider extends AbstractService implements TemplateService {

	private static final Logger logger = LoggerFactory.getLogger(FreemarkerTemplateServiceProvider.class);
	
    private static final String TEMPLATE_REPOSITORY_ROOT = "templateRepositoryRoot";
    private static final String DATE_FORMAT = "dateFormat";
    private static final String TIME_FORMAT = "timeFormat";
    private static final String DATETIME_FORMAT = "datetimeFormat";
    
    private static final String DEFAULT_TEMPLATE_REPOSITORY_ROOT = "WEB-INF/templates";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_TIME_FORMAT = "hh:mm:ss";
    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd hh:mm:ss";

    // Injected
    private ConfigurationService configSvc;
    
    private File repoRoot;
    private String dateFormat;
    private String timeFormat;
    private String datetimeFormat;
    
    private freemarker.template.Configuration config;
    
    public FreemarkerTemplateServiceProvider(
    		ConfigurationService configSvc, 
    		RegistryShutdownHub shutdownHub) throws Exception {
    	
    	super(shutdownHub);
    	this.configSvc = configSvc;
    	
    	this.init(this.getConfiguration(this.configSvc));
    }

    private void init(org.apache.commons.configuration.Configuration config) throws Exception {
    	
    	String repoRoot = config.getString(TEMPLATE_REPOSITORY_ROOT, DEFAULT_TEMPLATE_REPOSITORY_ROOT);
    	logger.debug("template repository root = {}", repoRoot);
    	
    	this.repoRoot = new File(this.configSvc.getApplicationRoot(), repoRoot);
    	if (!this.repoRoot.exists()) {
    		throw new FileNotFoundException(this.repoRoot.toString());
    	}
    	
    	this.dateFormat = config.getString(DATE_FORMAT, DEFAULT_DATE_FORMAT);
    	this.timeFormat = config.getString(TIME_FORMAT, DEFAULT_TIME_FORMAT);
    	this.datetimeFormat = config.getString(DATETIME_FORMAT, DEFAULT_DATETIME_FORMAT);
    	
    	logger.debug("template root = {}", this.repoRoot);
		logger.debug("date format = {}", this.dateFormat);
		logger.debug("time format = {}", this.timeFormat);
		logger.debug("datetime format = {}", this.dateFormat);
    }

    @Override
    public void start() throws Exception {
        this.config = new freemarker.template.Configuration();
        this.config.setDirectoryForTemplateLoading(this.repoRoot);
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
    	this.repoRoot = null;
    	this.dateFormat = null;
    	this.timeFormat = null;
    	this.datetimeFormat = null;
    }

    @Override
    public void paint(String tplPath, Writer out, Map<String, Object> args) throws IOException {
    	logger.debug("painting template = {}", tplPath);
		logger.debug("template varables = {}", args);
		Template template = this.config.getTemplate(tplPath);
		try {
			this.putBuildinVariables(args);
			template.process(args, out);
		} catch (TemplateException e) {
			throw new TemplateServiceException(e);
		}
    }

    @Override
    public void paint(String tplPath, Writer out) throws IOException {
		Map<String, Object> args = new HashMap<String, Object>();
		this.paint(tplPath, out, args);
    }
    
    @Override
    public String render(String template, Map<String, Object> args) throws IOException {
    	Reader reader = null;
    	Writer writer = null;
    	try {
	    	reader = new StringReader(template);
	    	Template tpl = new Template(null, reader, this.config);
	    	writer = new StringWriter();
	    	this.putBuildinVariables(args);
	    	tpl.process(args, writer);
	    	writer.flush();
	    	return writer.toString();
    	} catch (TemplateException e) {
    		throw new TemplateServiceException(e);
		} finally {
    		if (reader != null) {
    			reader.close();
    		}
    		if (writer != null) {
    			writer.close();
    		}
    	}
    }
    
    private void putBuildinVariables(Map<String, Object> args) {
    	args.put("APPLICATION_ROOT", this.configSvc.getApplicationRoot());
    	args.put("CONTEXT_PATH", this.configSvc.getContextPath());
    	
    	System.out.println(args);
    }
    
}
