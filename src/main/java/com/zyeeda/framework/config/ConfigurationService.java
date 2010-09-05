package com.zyeeda.framework.config;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.tapestry5.ioc.Resource;

public interface ConfigurationService {

	Configuration getConfiguration(Resource resource);
	
	File getApplicationRoot();
	
	String getContextParameter(String name);
	
}
