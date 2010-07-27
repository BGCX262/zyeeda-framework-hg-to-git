package com.zyeeda.framework.security;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.helpers.LoggerHelper;

public class CombinedRealm extends AuthorizingRealm {
	
	private static final Logger logger = LoggerFactory.getLogger(CombinedRealm.class);

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		LoggerHelper.debug(logger, "authentication token type = {}", token.getClass().getName());
		
		if (token instanceof UsernamePasswordToken) {
			UsernamePasswordToken upToken = (UsernamePasswordToken) token;
			if (logger.isDebugEnabled()) {
				logger.debug("username = {}", upToken.getUsername());
				logger.debug("password = {}", upToken.getPassword());
			}
			return new SimpleAuthenticationInfo(upToken.getUsername(), upToken.getPassword(), this.getName());
		}
		
		return new SimpleAuthenticationInfo("admin", "admin".toCharArray(), this.getName());
	}

}
