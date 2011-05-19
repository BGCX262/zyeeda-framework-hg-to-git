package com.zyeeda.framework.security.internal;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;

import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.service.AbstractService;

public class OpenIdConsumerSecurityServiceProvider extends AbstractService implements SecurityService<SecurityManager> {

	public OpenIdConsumerSecurityServiceProvider(RegistryShutdownHub shutdownHub) {
		super(shutdownHub);
	}

	@Override
	public SecurityManager getSecurityManager() {
		return SecurityUtils.getSecurityManager();
	}

	@Override
	public String getCurrentUser() {
		Subject current = SecurityUtils.getSubject();
		return current.getPrincipal().toString();
	}

}
