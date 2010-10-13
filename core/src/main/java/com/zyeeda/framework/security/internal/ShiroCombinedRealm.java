package com.zyeeda.framework.security.internal;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import javax.persistence.EntityManager;
import javax.transaction.Status;
import javax.transaction.SystemException;
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
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.transaction.TransactionService;

public class ShiroCombinedRealm extends AuthorizingRealm {
	
	// Injected
	private final LdapService ldapSvc;
	private final PersistenceService persistenceSvc;
	private final TransactionService txSvc;
	private final SecurityService<?> securitySvc;
	private final Logger logger;
	
	public ShiroCombinedRealm(LdapService ldapSvc,
			PersistenceService persistenceSvc,
			TransactionService txSvc,
			SecurityService<?> securitySvc,
			Logger logger) {
		
		this.ldapSvc = ldapSvc;
		this.persistenceSvc = persistenceSvc;
		this.txSvc = txSvc;
		this.securitySvc = securitySvc;
		this.logger = logger;
	}
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		LoggerHelper.debug(logger, "authentication token type = {}", token.getClass().getName());
		
		if (!(token instanceof UsernamePasswordToken)) {
			throw new AuthenticationException("Invalid authentication token.");
		}
		
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		if (logger.isDebugEnabled()) {
			logger.debug("username = {}", upToken.getUsername());
			logger.debug("password = ******");
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
			//EntityManager session = this.persistenceSvc.openSession();
			//session.getTransaction().begin();
			
			String username = (String) this.getAvailablePrincipal(principals);
			List<?> roles = this.securitySvc.getRoleManager().getRolesBySubject(username);
			
			SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			for (Iterator<?> it = roles.iterator(); it.hasNext(); ) {
				Role role = (Role) it.next();
				if (this.logger.isDebugEnabled()) {
					logger.debug("role name = {}", role.getName());
					logger.debug("role perms = {}", role.getPermissions());
				}
				info.addRole(role.getName());
				info.addStringPermissions(role.getPermissionSet());
			}
			//session.getTransaction().commit();
			utx.commit();
			
			return info;
		} catch (Throwable t) {
			/*if (session != null && session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}*/
			try {
				if (utx != null && utx.getStatus() == Status.STATUS_ACTIVE) {
					utx.rollback();
				}
			} catch (Throwable e) {
				LoggerHelper.error(logger, e.getMessage(), e);
			}
			throw new AuthorizationException(t);
		}
	}

}
