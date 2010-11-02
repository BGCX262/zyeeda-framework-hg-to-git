package com.zyeeda.framework.ldap.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.slf4j.Logger;

import com.zyeeda.framework.config.ConfigurationService;
import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.ldap.LdapServiceException;
import com.zyeeda.framework.service.AbstractService;
import com.zyeeda.framework.template.TemplateService;
import com.zyeeda.framework.template.TemplateServiceException;

@ServiceId("sun-ldap-service-provider")
@Marker(Primary.class)
public class SunLdapServiceProvider extends AbstractService implements LdapService {
	
	private static final String PROVIDER_URL = "providerUrl";
	private static final String SECURITY_AUTHENTICATION = "securityAuthentication";
	private static final String SYSTEM_SECURITY_PRINCIPAL = "systemSecurityPrincipal";
	private static final String SYSTEM_SECURITY_CREDENTIALS = "systemSecurityCredentials";
	private static final String SECURITY_PRINCIPAL_TEMPLATE = "securityPrincipalTemplate";
	
	private static final String DEFAULT_INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	private static final String DEFAULT_SECURITY_AUTHENTICATION = "simple";
	
	// Injected
	private final TemplateService tplSvc;
	
	private String providerUrl;
	private String securityAuthentication;
	private String systemSecurityPrincipal;
	private String systemSecurityCredentials;
	private String securityPrincipalTemplate;
	
	public SunLdapServiceProvider(
			ConfigurationService configSvc, 
			TemplateService tplSvc,
			Logger logger,
			RegistryShutdownHub shutdownHub) throws Exception {
		
		super(logger, shutdownHub);
		this.tplSvc = tplSvc;
		
		Configuration config = this.getConfiguration(configSvc);
    	this.init(config);
	}
	
	public void init(Configuration config) throws Exception {
		this.providerUrl = config.getString(PROVIDER_URL);
		this.securityAuthentication = config.getString(SECURITY_AUTHENTICATION, DEFAULT_SECURITY_AUTHENTICATION);
		this.systemSecurityPrincipal = config.getString(SYSTEM_SECURITY_PRINCIPAL);
		this.systemSecurityCredentials = config.getString(SYSTEM_SECURITY_CREDENTIALS);
		this.securityPrincipalTemplate = config.getString(SECURITY_PRINCIPAL_TEMPLATE);
		
		if (this.getLogger().isDebugEnabled()) {
			this.getLogger().debug("provider url = {}", this.providerUrl);
			this.getLogger().debug("security authentication = {}", this.securityAuthentication);
			this.getLogger().debug("system security principal = {}", this.systemSecurityPrincipal);
			this.getLogger().debug("system security credentials = ******");
			this.getLogger().debug("security princiapl template = {}", this.securityPrincipalTemplate);
		}
	}

	@Override
	public LdapContext getLdapContext() throws NamingException {
		Hashtable<String, String> env = this.setupEnvironment();
		env.put(Context.SECURITY_PRINCIPAL, this.systemSecurityPrincipal);
		env.put(Context.SECURITY_CREDENTIALS, this.systemSecurityCredentials);
		return new InitialLdapContext(env, null);
	}

	@Override
	public LdapContext getLdapContext(String username, String password)	throws NamingException, IOException, TemplateServiceException {
		if (this.getLogger().isDebugEnabled()) {
			this.getLogger().debug("username = {}", username);
			this.getLogger().debug("password = ******");
		}
		
		Hashtable<String, String> env = this.setupEnvironment();
		Map<String, String> args = new HashMap<String, String>(1);
		args.put("username", username);
		String principal = this.tplSvc.render(this.securityPrincipalTemplate, args);
		LoggerHelper.debug(this.getLogger(), "rendered principal = {}", principal);
		
		env.put(Context.SECURITY_PRINCIPAL, principal);
		env.put(Context.SECURITY_CREDENTIALS, password);
		return new InitialLdapContext(env, null);
	}
	
	private Hashtable<String, String> setupEnvironment() {
		Hashtable<String, String> env = new Hashtable<String, String>();
		
		env.put(Context.INITIAL_CONTEXT_FACTORY, DEFAULT_INITIAL_CONTEXT_FACTORY);
		if (StringUtils.isBlank(this.providerUrl)) {
			throw new LdapServiceException("The provider url must be specified in form of ldap(s)://<hostname>:<port>/<baseDN>");
		}
		env.put(Context.PROVIDER_URL, this.providerUrl);
		
		env.put(Context.SECURITY_AUTHENTICATION, this.securityAuthentication);
		return env;
	}

}
