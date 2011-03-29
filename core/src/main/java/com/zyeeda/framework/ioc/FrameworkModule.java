package com.zyeeda.framework.ioc;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.config.ConfigurationService;
import com.zyeeda.framework.config.internal.DefaultConfigurationServiceProvider;
import com.zyeeda.framework.ioc.annotations.AttachmentPersistence;
import com.zyeeda.framework.ioc.annotations.DroolsTaskPersistence;
import com.zyeeda.framework.knowledge.KnowledgeService;
import com.zyeeda.framework.knowledge.internal.DroolsKnowledgeServiceProvider;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.ldap.internal.SunLdapServiceProvider;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.persistence.internal.AttachmentPersistenceServiceProvider;
import com.zyeeda.framework.persistence.internal.DroolsTaskPersistenceServiceProvider;
import com.zyeeda.framework.persistence.internal.DefaultPersistenceServiceProvider;
import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.security.internal.ShiroSecurityServiceProvider;
import com.zyeeda.framework.template.TemplateService;
import com.zyeeda.framework.template.internal.FreemarkerTemplateServiceProvider;
import com.zyeeda.framework.transaction.TransactionService;
import com.zyeeda.framework.transaction.internal.DefaultTransactionServiceProvider;
import com.zyeeda.framework.validation.ValidationService;
import com.zyeeda.framework.validation.internal.HibernateValidationServiceProvider;

public class FrameworkModule {
	
	private final static Logger logger = LoggerFactory.getLogger(FrameworkModule.class);
	
	public static void bind(ServiceBinder binder) {
		binder.bind(ConfigurationService.class, DefaultConfigurationServiceProvider.class);
		binder.bind(TemplateService.class, FreemarkerTemplateServiceProvider.class);
		binder.bind(PersistenceService.class, DefaultPersistenceServiceProvider.class);
		binder.bind(PersistenceService.class, DroolsTaskPersistenceServiceProvider.class);
		binder.bind(PersistenceService.class, AttachmentPersistenceServiceProvider.class);
		binder.bind(ValidationService.class, HibernateValidationServiceProvider.class);
		binder.bind(LdapService.class, SunLdapServiceProvider.class);
		binder.bind(SecurityService.class, ShiroSecurityServiceProvider.class);
		binder.bind(KnowledgeService.class, DroolsKnowledgeServiceProvider.class);
		binder.bind(TransactionService.class, DefaultTransactionServiceProvider.class);
	}
	
	public void contributeRegistryStartup(
			OrderedConfiguration<Runnable> configuration,
			@Primary final ConfigurationService configSvc,
			@Primary final TemplateService tplSvc,
			@Primary final TransactionService txSvc,
			@Primary final ValidationService validationSvc,
			@Primary final PersistenceService defaultPersistenceSvc,
			@DroolsTaskPersistence final PersistenceService droolsTaskPersistenceSvc,
			@AttachmentPersistence final PersistenceService attachPersistenceSvc,
			@Primary final LdapService ldapSvc,
			@Primary final SecurityService<?> securitySvc,
			@Primary final KnowledgeService knowledgeSvc) {
		
		configuration.add("ServiceStartup", new Runnable() {
			
			@Override
			public void run() {
				try {
					configSvc.start();
					tplSvc.start();
					txSvc.start();
					validationSvc.start();
					defaultPersistenceSvc.start();
					droolsTaskPersistenceSvc.start();
					attachPersistenceSvc.start();
					ldapSvc.start();
					securitySvc.start();
					knowledgeSvc.start();
				} catch (Throwable t) {
					logger.error("Service repository start up failed.", t);
					throw new RuntimeException(t);
				}
			}
			
		});
	}

}
