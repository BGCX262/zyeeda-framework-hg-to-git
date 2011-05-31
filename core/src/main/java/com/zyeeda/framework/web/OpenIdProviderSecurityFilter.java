package com.zyeeda.framework.web;

import org.apache.tapestry5.ioc.Registry;

import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.security.internal.OpenIdProviderSecurityServiceProvider;
import com.zyeeda.framework.utils.IocUtils;

public class OpenIdProviderSecurityFilter extends SecurityFilter {

	@Override
	protected SecurityService<?> getSecurityService(Registry registry) {
		return registry.getService(IocUtils.getServiceId(OpenIdProviderSecurityServiceProvider.class), SecurityService.class);
	}

}
