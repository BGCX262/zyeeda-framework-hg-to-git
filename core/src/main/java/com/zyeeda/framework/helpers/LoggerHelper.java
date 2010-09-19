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

import org.slf4j.Logger;

/**
 * Logger helper class.
 * Use loggers without checking if the level is enabled.
 * 
 * @author		Rui Tang
 * @version 	%I%, %G%
 * @since 	 	1.0
 */
public class LoggerHelper {

	public static void trace(Logger logger, String message, Object... args) {
		if (logger.isTraceEnabled()) {
			logger.trace(message, args);
		}
	}
	
	public static void trace(Logger logger, String message, Throwable t) {
		if (logger.isTraceEnabled()) {
			logger.trace(message, t);
		}
	}
	
	public static void debug(Logger logger, String message, Object... args) {
		if (logger.isDebugEnabled()) {
			logger.debug(message, args);
		}
	}
	
	public static void info(Logger logger, String message, Object... args) {
		if (logger.isInfoEnabled()) {
			logger.info(message, args);
		}
	}
	
	public static void error(Logger logger, String message, Object... args) {
		if (logger.isErrorEnabled()) {
			logger.error(message, args);
		}
	}
	
	public static void error(Logger logger, String message, Throwable t) {
		if (logger.isErrorEnabled()) {
			logger.error(message, t);
		}
	}
	
	public static void warn(Logger logger, String message, Object... args) {
		if (logger.isWarnEnabled()) {
			logger.warn(message, args);
		}
	}
}
