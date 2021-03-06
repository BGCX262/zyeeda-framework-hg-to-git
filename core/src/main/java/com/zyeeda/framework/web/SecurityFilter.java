package com.zyeeda.framework.web;

import java.util.Map;

import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.IniShiroFilter;
import org.apache.tapestry5.ioc.Registry;

import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.utils.IocUtils;

public abstract class SecurityFilter extends IniShiroFilter {
	
	@Override
	protected Map<String, ?> applySecurityManager(Ini ini) {
		Registry registry = IocUtils.getRegistry(this.getFilterConfig().getServletContext());
		SecurityService<?> securitySvc = this.getSecurityService(registry);
		SecurityManager securityMgr = (SecurityManager) securitySvc.getSecurityManager();
		
		if (!(securityMgr instanceof WebSecurityManager)) {
            String msg = "The configured security manager is not an instance of WebSecurityManager, so " +
                    "it can not be used with the Shiro servlet filter.";
            throw new ConfigurationException(msg);
        }
		this.setSecurityManager((WebSecurityManager) securitySvc.getSecurityManager());
		return null;
	}
	
	protected abstract SecurityService<?> getSecurityService(Registry registry);

}
