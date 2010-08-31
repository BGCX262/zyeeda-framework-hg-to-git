package com.zyeeda.framework.ldap.internal;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.apache.commons.configuration.Configuration;
import org.apache.shiro.realm.ldap.DefaultLdapContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.service.AbstractService;

@Deprecated
public class ShiroLdapServiceProvider extends AbstractService implements LdapService {
	
	private static final Logger logger = LoggerFactory.getLogger(ShiroLdapServiceProvider.class);

	private static final String AUTHENTICATION = "authentication";
	private static final String PRINCIPAL_SUFFIX = "principalSuffix";
	private static final String SEARCH_BASE = "searchBase";
	private static final String URL = "url";
	private static final String REFERRAL  = "referral";
	private static final String SYSTEM_USERNAME = "systemUsername";
	private static final String SYSTEM_PASSWORD = "systemPassword";
	private static final String USE_POOLING = "usePooling";
	
	private static final String DEFAULT_AUTHENTICATION = "simple";
	private static final String DEFAULT_REFERRAL = "follow";
	private static final boolean DEFAULT_USE_POOLING = true;
	
	private String authentication;
	private String principalSuffix;
	private String searchBase;
	private String url;
	private String referral;
	private String systemUsername;
	private String systemPassword;
	private boolean usePooling;
	
	private DefaultLdapContextFactory factory;
	
	public void init(Configuration config) throws Exception {
		this.authentication = config.getString(AUTHENTICATION, DEFAULT_AUTHENTICATION);
		this.principalSuffix = config.getString(PRINCIPAL_SUFFIX);
		this.searchBase = config.getString(SEARCH_BASE);
		this.url = config.getString(URL);
		this.referral = config.getString(REFERRAL, DEFAULT_REFERRAL);
		this.systemUsername = config.getString(SYSTEM_USERNAME);
		this.systemPassword = config.getString(SYSTEM_PASSWORD);
		this.usePooling = config.getBoolean(USE_POOLING, DEFAULT_USE_POOLING);
		
		if (logger.isDebugEnabled()) {
			logger.debug("authentication = {}", this.authentication);
			logger.debug("principalSuffix = {}", this.principalSuffix);
			logger.debug("searchBase = {}", this.searchBase);
			logger.debug("url = {}", this.url);
			logger.debug("referral = {}", this.referral);
			logger.debug("system username = {}", this.systemUsername);
			logger.debug("system password = ******");
			logger.debug("use pooling = {}", this.usePooling);
		}
	}
	
	@Override
	public void start() throws Exception {
		this.factory = new DefaultLdapContextFactory();
		this.factory.setAuthentication(this.authentication);
		this.factory.setPrincipalSuffix(this.principalSuffix);
		this.factory.setSearchBase(this.searchBase);
		this.factory.setUrl(this.url);
		this.factory.setReferral(this.referral);
		this.factory.setSystemUsername(this.systemUsername);
		this.factory.setSystemPassword(this.systemPassword);
		this.factory.setUsePooling(this.usePooling);
		
	}

	@Override
	public LdapContext getLdapContext() throws NamingException {
		return this.factory.getSystemLdapContext();
	}

	@Override
	public LdapContext getLdapContext(String username, String password)	throws NamingException {
		return this.factory.getLdapContext(username, password);
	}

}
