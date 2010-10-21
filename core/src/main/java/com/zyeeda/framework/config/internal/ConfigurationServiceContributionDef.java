package com.zyeeda.framework.config.internal;

import javax.servlet.ServletContext;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ModuleBuilderSource;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.def.ContributionDef;

public class ConfigurationServiceContributionDef implements ContributionDef {

	private ServletContext context;
	
	public ConfigurationServiceContributionDef(ServletContext context) {
		this.context = context;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void contribute(ModuleBuilderSource moduleSource, ServiceResources resources,
			Configuration configuration) {
		
		configuration.add(this.context);
	}

	@Override
	public void contribute(ModuleBuilderSource moduleSource, ServiceResources resources,
			OrderedConfiguration configuration) {
	}

	@Override
	public void contribute(ModuleBuilderSource moduleSource, ServiceResources resources,
			MappedConfiguration configuration) {
	}

	@Override
	public String getServiceId() {
		return DefaultConfigurationServiceProvider.class.getAnnotation(ServiceId.class).value();
	}
	
}
