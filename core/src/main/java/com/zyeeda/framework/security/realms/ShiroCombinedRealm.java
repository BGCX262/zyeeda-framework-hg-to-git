package com.zyeeda.framework.security.realms;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.ldap.LdapUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.managers.RoleManager;

public class ShiroCombinedRealm extends AuthorizingRealm {
	
	private static final Logger logger = LoggerFactory.getLogger(ShiroCombinedRealm.class);
	
	// Injected
	private final LdapService ldapSvc;
	private final RoleManager roleMgr;
	
	public ShiroCombinedRealm(LdapService ldapSvc, RoleManager roleMgr) {
		this.ldapSvc = ldapSvc;
		this.roleMgr = roleMgr;
	}
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		logger.debug("authentication token type = {}", token.getClass().getName());
		
		if (!(token instanceof UsernamePasswordToken)) {
			throw new AuthenticationException("Invalid authentication token.");
		}
		
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		logger.debug("username = {}", upToken.getUsername());
		logger.debug("password = ******");
		
		LdapContext ctx = null;
		try {
			ctx = this.ldapSvc.getLdapContext(upToken.getUsername(), new String(upToken.getPassword()));
		} catch (NamingException e) {
			throw new AuthenticationException(e);
		} catch (IOException e) {
			throw new AuthenticationException(e);
		} finally {
			LdapUtils.closeContext(ctx);
		}
		
		return new SimpleAuthenticationInfo(upToken.getUsername(), upToken.getPassword(), this.getName());
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		String username = (String) this.getAvailablePrincipal(principals);
		List<?> roles = this.roleMgr.getRolesBySubject(username);
		
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		for (Iterator<?> it = roles.iterator(); it.hasNext(); ) {
			Role role = (Role) it.next();
			logger.debug("role name = {}", role.getName());
			logger.debug("role perms = {}", role.getPermissions());
			info.addRole(role.getName());
			info.addStringPermissions(role.getPermissionSet());
		}
		
		return info;
	}

}
