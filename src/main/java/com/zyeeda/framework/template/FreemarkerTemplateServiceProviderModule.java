package com.zyeeda.framework.template;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.Symbol;

import org.chenillekit.core.services.ConfigurationService;

import com.zyeeda.framework.FrameworkConstants;
import com.zyeeda.framework.template.internal.FreemarkerTemplateServiceProvider;

public class FreemarkerTemplateServiceProviderModule {
    
	@Marker(Primary.class)
	public TemplateService buildFreemarkerTemplateServiceProvider(
			@Inject @Symbol(FrameworkConstants.APPLICATION_ROOT) String appRoot,
			ConfigurationService configSvc) throws Exception {
		
		return new FreemarkerTemplateServiceProvider(appRoot, configSvc);
	}
	
	public void contributeRegistryStartup(
			OrderedConfiguration<Runnable> configuration,
			@Primary final TemplateService tplSvc) {
		
		configuration.add("ServiceStartup", new Runnable() {
			
			@Override
			public void run() {
				try {
					tplSvc.start();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
		});
	}
}
