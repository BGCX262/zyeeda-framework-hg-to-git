package com.zyeeda.drivebox.services.base;

import javax.servlet.ServletContext;

import org.apache.tapestry5.ioc.Registry;

import com.zyeeda.framework.FrameworkConstants;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.persistence.internal.DefaultPersistenceServiceProvider;
import com.zyeeda.framework.utils.IocUtils;

public class ResourceService {
	
	private ServletContext ctx;
	private Registry reg;

	public ResourceService(ServletContext ctx) {
		this.ctx = ctx;
		this.reg = (Registry) this.ctx.getAttribute(FrameworkConstants.SERVICE_REGISTRY);
	}
	
	protected ServletContext getServletContext() {
		return this.ctx;
	}
	
	protected Registry getServiceRegistry() {
		return this.reg;
	}
	
	protected PersistenceService getPersistenceService() {
		return this.reg.getService(IocUtils.getServiceId(DefaultPersistenceServiceProvider.class), PersistenceService.class);
	}
	
}
