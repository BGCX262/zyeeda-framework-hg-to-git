package com.zyeeda.framework.utils;

import org.apache.tapestry5.ioc.annotations.ServiceId;

public class IocUtils {

	public static String getServiceId(Class<?> clazz) {
		ServiceId svcId = clazz.getAnnotation(ServiceId.class);
		if (svcId == null) {
			throw new RuntimeException("No [ServiceId] annotation defined.");
		}
		return svcId.value();
	}
	
}
