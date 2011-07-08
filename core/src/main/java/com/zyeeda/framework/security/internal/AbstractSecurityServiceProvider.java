package com.zyeeda.framework.security.internal;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;

import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.service.AbstractService;

public abstract class AbstractSecurityServiceProvider extends AbstractService implements
		SecurityService<SecurityManager> {
	
	public AbstractSecurityServiceProvider(RegistryShutdownHub shutdownHub) {
		super(shutdownHub);
	}

	@Override
	public abstract SecurityManager getSecurityManager();

	@Override
	public String getCurrentUser() {
//		Subject current = SecurityUtils.getSubject();
//		Object principal = current.getPrincipal();
//		if (principal == null) {
//			throw new AuthenticationException("Subject not signed in.");
//		}
		return  "admin";//principal.toString();
	}

}
