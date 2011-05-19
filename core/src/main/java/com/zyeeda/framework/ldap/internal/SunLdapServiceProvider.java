package com.zyeeda.framework.ldap.internal;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.realm.ldap.LdapUtils;
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

@ServiceId("sun-ldap-service")
@Marker(Primary.class)
public class SunLdapServiceProvider extends AbstractService implements LdapService {
	
	private static final Logger logger = LoggerFactory.getLogger(SunLdapServiceProvider.class);
	
	private static final String PROVIDER_URL = "providerUrl";
	private static final String SECURITY_AUTHENTICATION = "securityAuthentication";
	private static final String SYSTEM_SECURITY_PRINCIPAL = "systemSecurityPrincipal";
	private static final String SYSTEM_SECURITY_CREDENTIALS = "systemSecurityCredentials";
	private static final String SECURITY_PRINCIPAL_TEMPLATE = "securityPrincipalTemplate";
	private static final String BASE_DN = "baseDN";
	
	private static final String DEFAULT_INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	private static final String DEFAULT_SECURITY_AUTHENTICATION = "simple";
	private static final String DEFAULT_SECURITY_PRINCIPAL_TEMPLATE = "(uid=%s)";
	private static final String DEFAULT_BASE_DN = "";
	
	private String providerUrl;
	private String securityAuthentication;
	private String systemSecurityPrincipal;
	private String systemSecurityCredentials;
	private String securityPrincipalTemplate;
	private String baseDn;
	
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
		this.securityPrincipalTemplate = config.getString(SECURITY_PRINCIPAL_TEMPLATE, DEFAULT_SECURITY_PRINCIPAL_TEMPLATE);
		this.baseDn = config.getString(BASE_DN, DEFAULT_BASE_DN);
		
		logger.debug("provider url = {}", this.providerUrl);
		logger.debug("security authentication = {}", this.securityAuthentication);
		logger.debug("system security principal = {}", this.systemSecurityPrincipal);
		logger.debug("system security credentials = ******");
		logger.debug("security principal template = {}", this.securityPrincipalTemplate);
		logger.debug("base dn = {}", this.baseDn);
	}

	@Override
	public LdapContext getLdapContext() throws NamingException {
		Hashtable<String, String> env = this.setupEnvironment();
		env.put(Context.SECURITY_PRINCIPAL, this.systemSecurityPrincipal);
		env.put(Context.SECURITY_CREDENTIALS, this.systemSecurityCredentials);
		return new InitialLdapContext(env, null);
	}

	@Override
	public LdapContext getLdapContext(String username, String password)	throws NamingException, IOException {
		logger.debug("username = {}", username);
		logger.debug("password = ******");
		
		LdapContext ctx = null;
		try {
			ctx = this.getLdapContext();
			SearchControls sc = new SearchControls();
			sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> ne = ctx.search(this.baseDn, String.format(this.securityPrincipalTemplate, username), sc);
			SearchResult result = ne.hasMore() ? ne.next() : null;
			if (result == null) {
				throw new NamingException("User not found.");
			}
			if (ne.hasMore()) {
				throw new NamingException("More than one user has the same name.");
			}
			
			String principal = result.getNameInNamespace();
			logger.debug("searched principal = {}", principal);
			
			Hashtable<String, String> env = this.setupEnvironment();
			env.put(Context.SECURITY_PRINCIPAL, principal);
			env.put(Context.SECURITY_CREDENTIALS, password);
			return new InitialLdapContext(env, null);
		} finally {
			LdapUtils.closeContext(ctx);
		}
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
