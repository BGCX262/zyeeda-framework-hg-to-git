package com.zyeeda.framework.openid.consumer;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OpenIdConsumerRealm extends AuthorizingRealm {
	
	private static final Logger logger = LoggerFactory.getLogger(OpenIdConsumerRealm.class);
	
	public OpenIdConsumerRealm() {
		this.setAuthenticationTokenClass(OpenIdAuthenticationToken.class);
		this.setCredentialsMatcher(new BypassCredentialsMatcher());
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		return null;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		logger.debug("authentication token type = {}", token.getClass().getName());
		if (!(token instanceof OpenIdAuthenticationToken)) {
			throw new AuthenticationException("Invalid authentication token.");
		}
		
		Object principal = token.getPrincipal();
		if (principal == null) {
			throw new AuthenticationException("OpenID authenticate failed.");
		}
		
		return new SimpleAuthenticationInfo(principal, null, this.getName());
	}
	
}
