package com.zyeeda.framework.security.internal;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.ldap.LdapUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;

import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.transaction.TransactionService;

public class ShiroCombinedRealm extends AuthorizingRealm {
	
	// Injected
	private final LdapService ldapSvc;
	private final TransactionService txSvc;
	private final RoleManager roleMgr;
	private final Logger logger;
	
	public ShiroCombinedRealm(LdapService ldapSvc,
			TransactionService txSvc,
			RoleManager roleMgr,
			Logger logger) {
		
		this.ldapSvc = ldapSvc;
		this.txSvc = txSvc;
		this.roleMgr = roleMgr;
		this.logger = logger;
	}
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		LoggerHelper.debug(this.logger, "authentication token type = {}", token.getClass().getName());
		
		if (!(token instanceof UsernamePasswordToken)) {
			throw new AuthenticationException("Invalid authentication token.");
		}
		
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("username = {}", upToken.getUsername());
			this.logger.debug("password = ******");
		}
		
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
		UserTransaction utx = null;
		try {
			utx = this.txSvc.getTransaction();
			utx.begin();
			
			String username = (String) this.getAvailablePrincipal(principals);
			List<?> roles = this.roleMgr.getRolesBySubject(username);
			
			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			for (Iterator<?> it = roles.iterator(); it.hasNext(); ) {
				Role role = (Role) it.next();
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("role name = {}", role.getName());
					this.logger.debug("role perms = {}", role.getPermissions());
				}
				info.addRole(role.getName());
				info.addStringPermissions(role.getPermissionSet());
			}
			utx.commit();
			
			return info;
		} catch (Throwable t) {
			try {
				if (utx != null && utx.getStatus() == Status.STATUS_ACTIVE) {
					utx.rollback();
				}
			} catch (Throwable e) {
				LoggerHelper.error(this.logger, e.getMessage(), e);
			}
			throw new AuthorizationException(t);
		}
	}

}
