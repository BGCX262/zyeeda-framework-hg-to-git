package com.zyeeda.framework.unittest;

import org.apache.commons.configuration.Configuration;

import com.zyeeda.framework.services.Server;
import com.zyeeda.framework.services.ServiceInvocationHandler;
import com.zyeeda.framework.services.TemplateService;
import com.zyeeda.framework.services.impl.TemplateServiceImpl;

public class TestServer extends Server {

	@Override
	public void init(Configuration config) throws Exception {
		super.init(config);
		
		TemplateService tplSvc = new TemplateServiceImpl(this);
		this.addService(new ServiceInvocationHandler<TemplateService>().bind(tplSvc));
		//this.addService(new PersistenceService(this));
	}
}
