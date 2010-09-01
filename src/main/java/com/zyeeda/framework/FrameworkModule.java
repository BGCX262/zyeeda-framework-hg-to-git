package com.zyeeda.framework;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.chenillekit.core.services.ConfigurationService;
import org.slf4j.Logger;

import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.ldap.internal.SunLdapServiceProvider;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.persistence.internal.HibernatePersistenceServiceProvider;
import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.security.internal.ShiroSecurityServiceProvider;
import com.zyeeda.framework.template.TemplateService;
import com.zyeeda.framework.template.internal.FreemarkerTemplateServiceProvider;
import com.zyeeda.framework.validation.ValidationService;
import com.zyeeda.framework.validation.internal.HibernateValidationServiceProvider;

public class FrameworkModule {
	
	private final Logger logger;
	
	public FrameworkModule(Logger logger) {
		this.logger = logger;
	}

	@Marker(Primary.class)
	public TemplateService buildFreemarkerTemplateServiceProvider(
			@Inject @Symbol(FrameworkConstants.APPLICATION_ROOT) String appRoot,
			ConfigurationService configSvc,
			Logger logger) throws Exception {
		
		return new FreemarkerTemplateServiceProvider(
				appRoot, configSvc, logger);
	}
	
	@Marker(Primary.class)
	public ValidationService buildHibernateValidationServiceProvider(
			@Primary PersistenceService persistenceSvc,
			Logger logger) {
		return new HibernateValidationServiceProvider(persistenceSvc, logger);
	}
	
	@Marker(Primary.class)
	public PersistenceService buildHibernatePersistenceServiceProvider(
			@Primary ValidationService validationSvc,
			Logger logger) {
		return new HibernatePersistenceServiceProvider(validationSvc, logger);
	}
	
	@Marker(Primary.class)
	public LdapService buildSunLdapServiceProvider(
			ConfigurationService configSvc,
			@Primary TemplateService tplSvc, 
			Logger logger) throws Exception {
		return new SunLdapServiceProvider(configSvc, tplSvc, logger);
	}
	
	@Marker(Primary.class)
	public SecurityService<?> buildShiroSecurityServiceProvider(
			@Primary LdapService ldapSvc,
			@Primary PersistenceService persistenceSvc,
			@Primary SecurityService<?> securitySvc,
			Logger logger) {
		return new ShiroSecurityServiceProvider(
				ldapSvc, persistenceSvc, securitySvc, logger);
	}
	
	public void contributeRegistryStartup(
			OrderedConfiguration<Runnable> configuration,
			@Primary final TemplateService tplSvc,
			@Primary final ValidationService validationSvc,
			@Primary final PersistenceService persistenceSvc,
			@Primary final LdapService ldapSvc,
			@Primary final SecurityService<?> securitySvc) {
		
		configuration.add("ServiceStartup", new Runnable() {
			
			@Override
			public void run() {
				try {
					tplSvc.start();
					validationSvc.start();
					persistenceSvc.start();
					ldapSvc.start();
					securitySvc.start();
				} catch (Throwable t) {
					LoggerHelper.error(logger, t.getMessage(), t);
					throw new RuntimeException(t);
				}
			}
			
		});
	}
}
