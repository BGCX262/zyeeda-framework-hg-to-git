package com.zyeeda.framework.unittest;

import org.apache.commons.configuration.Configuration;

import com.zyeeda.framework.server.ApplicationServer;
import com.zyeeda.framework.services.internal.FreemarkerTemplateServiceProvider;

public class TestServer extends ApplicationServer {

	@Override
	public void init(Configuration config) throws Exception {
		super.init(config);
		
		this.addService(new FreemarkerTemplateServiceProvider(this));
		//this.addService(new PersistenceService(this));
	}
}
