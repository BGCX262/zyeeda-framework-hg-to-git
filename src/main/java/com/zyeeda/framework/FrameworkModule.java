package com.zyeeda.framework;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.chenillekit.core.services.ConfigurationService;
import org.slf4j.Logger;

import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.ldap.internal.SunLdapServiceProvider;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.persistence.internal.HibernatePersistenceServiceProvider;
import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.security.internal.ShiroSecurityServiceProvider;
import com.zyeeda.framework.template.TemplateService;
import com.zyeeda.framework.template.internal.FreemarkerTemplateServiceProvider;

public class FrameworkModule {

	@Marker(Primary.class)
	public TemplateService buildFreemarkerTemplateServiceProvider(
			@Inject @Symbol(FrameworkConstants.APPLICATION_ROOT) String appRoot,
			ConfigurationService configSvc,
			Logger logger) throws Exception {
		
		return new FreemarkerTemplateServiceProvider(
				appRoot, configSvc, logger);
	}
	
	@Marker(Primary.class)
	public PersistenceService buildHibernatePersistenceServiceProvider(
			Logger logger) {
		return new HibernatePersistenceServiceProvider(logger);
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
			Logger logger) {
		return new ShiroSecurityServiceProvider(ldapSvc, persistenceSvc, logger);
	}
	
	public void contributeRegistryStartup(
			OrderedConfiguration<Runnable> configuration,
			@Primary final TemplateService tplSvc,
			@Primary final PersistenceService persistenceSvc,
			@Primary final LdapService ldapSvc,
			@Primary final SecurityService<?> securitySvc) {
		
		configuration.add("ServiceStartup", new Runnable() {
			
			@Override
			public void run() {
				try {
					tplSvc.start();
					persistenceSvc.start();
					ldapSvc.start();
					securitySvc.start();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
		});
	}
}
