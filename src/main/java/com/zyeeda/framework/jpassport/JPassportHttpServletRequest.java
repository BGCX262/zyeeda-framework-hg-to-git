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
package com.zyeeda.framework.jpassport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.helpers.LoggerHelper;

/**
 * JPassport implementation of {@link javax.servlet.http.HttpServletRequest HttpServletRequest}.
 * 
 * @author		Rui Tang
 * @version 	%I%, %G%
 * @since		1.0
 */
public class JPassportHttpServletRequest extends HttpServletRequestWrapper {

	private static final Logger logger = LoggerFactory.getLogger(JPassportHttpServletRequest.class);
	
	private String remoteUser;
	
	public JPassportHttpServletRequest(HttpServletRequest request) {
		super(request);
	}
	
	public void setRemoteUser(String remoteUser) {
		LoggerHelper.debug(logger, "remote user = {}", remoteUser);
		this.remoteUser = remoteUser;
	}
	
	@Override
	public String getRemoteUser() {
		return this.remoteUser;
	}

}
