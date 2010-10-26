package com.zyeeda.framework.utils;

import org.apache.tapestry5.ioc.LoggerSource;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.slf4j.Logger;

public class IocUtils {

	public static String getServiceId(Class<?> clazz) {
		ServiceId svcId = clazz.getAnnotation(ServiceId.class);
		if (svcId == null) {
			throw new RuntimeException("No [ServiceId] annotation defined.");
		}
		return svcId.value();
	}
	
	public static Logger getLogger(Registry reg, Class<?> clazz) {
		LoggerSource loggerSource = reg.getService(LoggerSource.class);
		return loggerSource.getLogger(clazz);
	}
	
}
