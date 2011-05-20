package com.zyeeda.drivebox.ioc;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.Startup;

import com.zyeeda.framework.config.ConfigurationService;
import com.zyeeda.framework.config.internal.DefaultConfigurationServiceProvider;
import com.zyeeda.framework.openid.consumer.OpenIdConsumerService;
import com.zyeeda.framework.openid.consumer.internal.DefaultOpenIdConsumerServiceProvider;

public class DriveboxModule {
	
	public static void bind(ServiceBinder binder) {
		binder.bind(ConfigurationService.class, DefaultConfigurationServiceProvider.class);
		binder.bind(OpenIdConsumerService.class, DefaultOpenIdConsumerServiceProvider.class);
	}
	
	@Startup
	public static void startServices(
			@Primary final ConfigurationService configSvc,
			@Primary final OpenIdConsumerService consumerSvc) throws Exception {
		
		configSvc.start();
		consumerSvc.start();
	}

}
