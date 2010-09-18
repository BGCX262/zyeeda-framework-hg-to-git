package com.zyeeda.framework.config;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;

import com.zyeeda.framework.service.Service;

public interface ConfigurationService extends Service, RegistryShutdownListener {

	Configuration getConfiguration(Resource resource);
	
	File getApplicationRoot();
	
	String getContextParameter(String name);
	
}
