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
package com.zyeeda.framework.helpers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.template.TemplateService;

import freemarker.template.TemplateException;

/**
 * Template helper class.
 * 
 * @author 		Rui Tang
 * @version 	%I%, %G%
 * @since		1.0
 */
public class TemplateHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(TemplateHelper.class);

	public static void paintException(TemplateService tplSvc, PrintWriter out, List<String> errorMessages) throws IOException, TemplateException {
		LoggerHelper.debug(logger, "errorMessages = {}", errorMessages);
		
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("errorMessages", errorMessages);
		
		tplSvc.paint("/lib/zyeeda/default_error_view.ftl", out, args);
	}
	
	public static void paintException(TemplateService tplSvc, PrintWriter out, String errorMessage) throws IOException, TemplateException {
		LoggerHelper.debug(logger, "errorMessage = {}", errorMessage);
		
		if (errorMessage == null) {
			paintException(tplSvc, out, (List<String>) null);
			return;
		}
		
		List<String> errorMessages = new ArrayList<String>(1);
		errorMessages.add(errorMessage);
		
		paintException(tplSvc, out, errorMessages);
	}
	
}
