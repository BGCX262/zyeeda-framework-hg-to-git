package com.zyeeda.drivebox.ioc;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.quartz.Job;

import com.zyeeda.drivebox.jobs.TestJob;
import com.zyeeda.framework.config.ConfigurationService;
import com.zyeeda.framework.config.internal.DefaultConfigurationServiceProvider;
import com.zyeeda.framework.knowledge.KnowledgeService;
import com.zyeeda.framework.knowledge.internal.DroolsKnowledgeServiceProvider;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.ldap.internal.SunLdapServiceProvider;
import com.zyeeda.framework.nosql.MongoDbService;
import com.zyeeda.framework.nosql.internal.DefaultMongoDbServiceProvider;
import com.zyeeda.framework.openid.consumer.OpenIdConsumerService;
import com.zyeeda.framework.openid.consumer.internal.DefaultOpenIdConsumerServiceProvider;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.persistence.annotations.DroolsTask;
import com.zyeeda.framework.persistence.internal.DefaultPersistenceServiceProvider;
import com.zyeeda.framework.persistence.internal.DroolsTaskPersistenceServiceProvider;
import com.zyeeda.framework.scheduler.SchedulerService;
import com.zyeeda.framework.scheduler.internal.QuartzSchedulerServiceProvider;
import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.security.annotations.Virtual;
import com.zyeeda.framework.security.internal.OpenIdConsumerSecurityServiceProvider;
import com.zyeeda.framework.security.internal.VirtualConsumerSecurityServiceProvider;
import com.zyeeda.framework.sync.UserSyncService;
import com.zyeeda.framework.sync.internal.HttpClientUserSyncServiceProvider;
import com.zyeeda.framework.template.TemplateService;
import com.zyeeda.framework.template.internal.FreemarkerTemplateServiceProvider;
import com.zyeeda.framework.transaction.TransactionService;
import com.zyeeda.framework.transaction.internal.DefaultTransactionServiceProvider;
import com.zyeeda.framework.validation.ValidationService;
import com.zyeeda.framework.validation.internal.HibernateValidationServiceProvider;

public class Module {
	
	public static void bind(ServiceBinder binder) {
		binder.bind(ConfigurationService.class, DefaultConfigurationServiceProvider.class);
		binder.bind(TemplateService.class, FreemarkerTemplateServiceProvider.class);
		binder.bind(PersistenceService.class, DefaultPersistenceServiceProvider.class);
		binder.bind(PersistenceService.class, DroolsTaskPersistenceServiceProvider.class);
		binder.bind(ValidationService.class, HibernateValidationServiceProvider.class);
		binder.bind(LdapService.class, SunLdapServiceProvider.class);
		binder.bind(SecurityService.class, OpenIdConsumerSecurityServiceProvider.class);
		binder.bind(SecurityService.class, VirtualConsumerSecurityServiceProvider.class);
		binder.bind(KnowledgeService.class, DroolsKnowledgeServiceProvider.class);
		binder.bind(TransactionService.class, DefaultTransactionServiceProvider.class);
		binder.bind(OpenIdConsumerService.class, DefaultOpenIdConsumerServiceProvider.class);
		binder.bind(UserSyncService.class, HttpClientUserSyncServiceProvider.class);
		binder.bind(SchedulerService.class, QuartzSchedulerServiceProvider.class);
		binder.bind(MongoDbService.class, DefaultMongoDbServiceProvider.class);
		
		binder.bind(Job.class, TestJob.class);
	}
	
	@Startup
	public static void startServices(
			@Primary final ConfigurationService configSvc,
			@Primary final TemplateService tplSvc,
			@Primary final TransactionService txSvc,
			@Primary final ValidationService validationSvc,
			@Primary final PersistenceService defaultPersistenceSvc,
			@DroolsTask final PersistenceService droolsTaskPersistenceSvc,
			@Primary final LdapService ldapSvc,
			@Primary final SecurityService<?> securitySvc,
			@Virtual final SecurityService<?> virtualSecuritySvc,
			@Primary final KnowledgeService knowledgeSvc,
			@Primary final OpenIdConsumerService consumerSvc,
			@Primary final UserSyncService userSyncSvc,
			@Primary final SchedulerService<?> schedulerSvc,
			@Primary final MongoDbService mongodbSvc) throws Exception {
		
		configSvc.start();
		tplSvc.start();
		//txSvc.start();
		//validationSvc.start();
		//defaultPersistenceSvc.start();
		//droolsTaskPersistenceSvc.start();
		ldapSvc.start();
		securitySvc.start();
		virtualSecuritySvc.start();
		//knowledgeSvc.start();
		consumerSvc.start();
		//userSyncSvc.start();
		//schedulerSvc.start();
		mongodbSvc.start();
		
	}

}
