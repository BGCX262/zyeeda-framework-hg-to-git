package com.zyeeda.framework.unittest;

import org.apache.commons.configuration.Configuration;

import com.zyeeda.framework.services.Server;
import com.zyeeda.framework.services.ServiceInvocationHandler;
import com.zyeeda.framework.services.impl.TemplateServiceImpl;

public class TestServer extends Server {

	public void init(Configuration config) throws Exception {
		super.init(config);
		
		this.addService(new ServiceInvocationHandler<TemplateServiceImpl>().bind(new TemplateServiceImpl(this)));
		//this.addService(new PersistenceService(this));
	}
}
