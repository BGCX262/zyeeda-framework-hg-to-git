package com.zyeeda.framework.security.internal;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

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

import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.persistence.PersistenceService;

public class ShiroCombinedRealm extends AuthorizingRealm {
	
	// Injected
	private LdapService ldapSvc;
	private PersistenceService persistenceSvc;
	private Logger logger;
	
	public ShiroCombinedRealm(LdapService ldapSvc,
			PersistenceService persistenceSvc,
			Logger logger) {
		
		this.ldapSvc = ldapSvc;
		this.persistenceSvc = persistenceSvc;
		this.logger = logger;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		EntityManager em = null;
		try {
			em = this.persistenceSvc.openSession();
			em.getTransaction().begin();
			String username = (String) this.getAvailablePrincipal(principals);
			Query query = em.createNamedQuery("getRolesBySubject");
			query.setParameter("subject", username);
			List<String> roles = query.getResultList();
			em.getTransaction().commit();
			
			return new SimpleAuthorizationInfo(new HashSet<String>(roles));
		} catch (Throwable t) {
			if (em != null) {
				em.getTransaction().rollback();
			}
			throw new AuthorizationException(t);
		} finally {
			if (em != null) {
				em.close();
			}
		}
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

}
