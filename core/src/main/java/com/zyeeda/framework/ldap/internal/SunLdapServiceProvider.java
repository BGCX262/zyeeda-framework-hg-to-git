package com.zyeeda.framework.ldap.internal;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.config.ConfigurationService;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.ldap.LdapServiceException;
import com.zyeeda.framework.service.AbstractService;
import com.zyeeda.framework.template.TemplateServiceException;

@ServiceId("sun-ldap-service")
@Marker(Primary.class)
public class SunLdapServiceProvider extends AbstractService implements LdapService {
	
	private static final Logger logger = LoggerFactory.getLogger(SunLdapServiceProvider.class);
	
	private static final String PROVIDER_URL = "providerUrl";
	private static final String SECURITY_AUTHENTICATION = "securityAuthentication";
	private static final String SYSTEM_SECURITY_PRINCIPAL = "systemSecurityPrincipal";
	private static final String SYSTEM_SECURITY_CREDENTIALS = "systemSecurityCredentials";
	private static final String SECURITY_PRINCIPAL_TEMPLATE = "securityPrincipalTemplate";
	
	private static final String DEFAULT_INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	private static final String DEFAULT_SECURITY_AUTHENTICATION = "simple";
	
	private String providerUrl;
	private String securityAuthentication;
	private String systemSecurityPrincipal;
	private String systemSecurityCredentials;
	private String securityPrincipalTemplate;
	
	public SunLdapServiceProvider(
			ConfigurationService configSvc, 
			RegistryShutdownHub shutdownHub) throws Exception {
		
		super(shutdownHub);
		
		Configuration config = this.getConfiguration(configSvc);
    	this.init(config);
	}
	
	public void init(Configuration config) throws Exception {
		this.providerUrl = config.getString(PROVIDER_URL);
		this.securityAuthentication = config.getString(SECURITY_AUTHENTICATION, DEFAULT_SECURITY_AUTHENTICATION);
		this.systemSecurityPrincipal = config.getString(SYSTEM_SECURITY_PRINCIPAL);
		this.systemSecurityCredentials = config.getString(SYSTEM_SECURITY_CREDENTIALS);
		this.securityPrincipalTemplate = config.getString(SECURITY_PRINCIPAL_TEMPLATE);
		
		logger.debug("provider url = {}", this.providerUrl);
		logger.debug("security authentication = {}", this.securityAuthentication);
		logger.debug("system security principal = {}", this.systemSecurityPrincipal);
		logger.debug("system security credentials = ******");
		logger.debug("security princiapl template = {}", this.securityPrincipalTemplate);
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
		logger.debug("username = {}", username);
		logger.debug("password = ******");
		
		/*Map<String, String> args = new HashMap<String, String>(1);
		args.put("username", username);
		String principal = this.tplSvc.render(this.securityPrincipalTemplate, args);*/
		
		String principal = String.format(this.securityPrincipalTemplate, username);
		logger.debug("rendered principal = {}", principal);
		
		Hashtable<String, String> env = this.setupEnvironment();
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
	
	public static SearchControls getOneLevelScopeSearchControls() {
		SearchControls sc = SunLdapServiceProvider.getSearchControls();
		sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);

		return sc;
	}
	
	public static SearchControls getThreeLevelScopeSearchControls() {
		SearchControls sc = SunLdapServiceProvider.getSearchControls();
		sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

		return sc;
	}

	public static SearchControls getSearchControls() {
		SearchControls sc = new SearchControls();

		return sc;
	}

}
