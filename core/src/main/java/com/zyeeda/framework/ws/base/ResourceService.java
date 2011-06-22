package com.zyeeda.framework.ws.base;

import javax.servlet.ServletContext;

import org.apache.tapestry5.ioc.Registry;

import com.zyeeda.framework.FrameworkConstants;
import com.zyeeda.framework.knowledge.KnowledgeService;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.ldap.internal.SunLdapServiceProvider;
import com.zyeeda.framework.nosql.MongoDbService;
import com.zyeeda.framework.nosql.internal.DefaultMongoDbServiceProvider;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.persistence.internal.DefaultPersistenceServiceProvider;
import com.zyeeda.framework.scheduler.SchedulerService;
import com.zyeeda.framework.scheduler.internal.QuartzSchedulerServiceProvider;
import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.security.internal.OpenIdConsumerSecurityServiceProvider;
import com.zyeeda.framework.sync.UserSyncService;
import com.zyeeda.framework.sync.internal.HttpClientUserSyncServiceProvider;
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
	
	protected SecurityService<?> getSecurityService() {
		return this.reg.getService(IocUtils.getServiceId(OpenIdConsumerSecurityServiceProvider.class), SecurityService.class);
	}
	
	protected LdapService getLdapService() {
		return this.reg.getService(IocUtils.getServiceId(SunLdapServiceProvider.class), LdapService.class);
	}
	
	protected UserSyncService getUserSynchService() {
		return this.reg.getService(IocUtils.getServiceId(HttpClientUserSyncServiceProvider.class), UserSyncService.class);
	}
	
	protected SchedulerService<?> getSchedulerService() {
		return this.reg.getService(IocUtils.getServiceId(QuartzSchedulerServiceProvider.class), SchedulerService.class);
	}
	
	protected KnowledgeService getKnowledgeService(){
		return this.reg.getService(KnowledgeService.class);
	}
	
	protected MongoDbService getMongoDbService() {
		return this.reg.getService(IocUtils.getServiceId(DefaultMongoDbServiceProvider.class), MongoDbService.class);
	}
	
	
}
