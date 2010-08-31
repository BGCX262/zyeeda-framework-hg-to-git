package com.zyeeda.framework.web;

import java.util.Map;

import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.config.Ini;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.web.config.WebIniSecurityManagerFactory;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.IniShiroFilter;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.tapestry5.ioc.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.FrameworkConstants;
import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.security.internal.ShiroSecurityManager;

public class ShiroSecurityFilter extends IniShiroFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(ShiroSecurityFilter.class);

	@Override
	protected Map<String, ?> applySecurityManager(Ini ini) {
		if (!CollectionUtils.isEmpty(ini.getSection(IniSecurityManagerFactory.MAIN_SECTION_NAME))) {
			LoggerHelper.warn(logger, "{} section will be ignored!", IniSecurityManagerFactory.MAIN_SECTION_NAME);
		}
		if (!CollectionUtils.isEmpty(ini.getSection(IniRealm.USERS_SECTION_NAME))) {
			LoggerHelper.warn(logger, "{} section will be ignored!", IniRealm.USERS_SECTION_NAME);
		}
		if (!CollectionUtils.isEmpty(ini.getSection(IniRealm.ROLES_SECTION_NAME))) {
			LoggerHelper.warn(logger, "{} section will be ignored!", IniRealm.ROLES_SECTION_NAME);
		}
		
		Registry reg = (Registry) this.getServletContext().getAttribute(FrameworkConstants.SERVICE_REGISTRY);
		SecurityService<?> securitySvc = reg.getService(SecurityService.class);
		SecurityManager securityManager = (SecurityManager) securitySvc.getSecurityManager();
		if (!(securityManager instanceof ShiroSecurityManager)) {
			String msg = "The security manager initialized by security service is not an instance of ShiroSecurityManager, " + 
				"so it can not be used with the security servlet filter.";
			throw new ConfigurationException(msg);
		}
		
		setSecurityManager((WebSecurityManager) securityManager);
		
		// only to return values
        WebIniSecurityManagerFactory factory;
        if (CollectionUtils.isEmpty(ini)) {
            factory = new WebIniSecurityManagerFactory();
        } else {
            factory = new WebIniSecurityManagerFactory(ini);
        }
        return factory.getBeans();
	}

}
