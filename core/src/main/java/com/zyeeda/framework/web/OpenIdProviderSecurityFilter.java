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

public class OpenIdProviderSecurityFilter extends IniShiroFilter {
	
	@Override
	protected Map<String, ?> applySecurityManager(Ini ini) {
		Registry registry = IocUtils.getRegistry(this.getFilterConfig().getServletContext());
		SecurityService<?> securitySvc = registry.getService(SecurityService.class);
		SecurityManager securityMgr = (SecurityManager) securitySvc.getSecurityManager();
		
		if (!(securityMgr instanceof WebSecurityManager)) {
            String msg = "The configured security manager is not an instance of WebSecurityManager, so " +
                    "it can not be used with the Shiro servlet filter.";
            throw new ConfigurationException(msg);
        }
		this.setSecurityManager((WebSecurityManager) securitySvc.getSecurityManager());
		return null;
	}
	
	/*
	@Override
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
		this.innerFilter = new IniShiroFilter();
		
		Registry registry = IocUtils.getRegistry(this.config.getServletContext());
		SecurityService<?> securitySvc = registry.getService(SecurityService.class);
		SecurityManager securityMgr = (SecurityManager) securitySvc.getSecurityManager();
		if (!(securityMgr instanceof WebSecurityManager)) {
			throw new ConfigurationException("Incompatible security manager.");
		}
		this.innerFilter.setSecurityManager((WebSecurityManager) securitySvc.getSecurityManager());
	
		Ini ini = IniFactorySupport.loadDefaultClassPathIni();
		if (ini == null || ini.isEmpty()) {
			return;
		}
		
        Ini.Section urls = ini.getSection(IniFilterChainResolverFactory.URLS);
        Ini.Section filters = ini.getSection(IniFilterChainResolverFactory.FILTERS);
        if ((urls != null && !urls.isEmpty()) || (filters != null && !filters.isEmpty())) {
            IniFilterChainResolverFactory filterChainResolverFactory = new IniFilterChainResolverFactory(ini);
            filterChainResolverFactory.setFilterConfig(this.config);
            FilterChainResolver resolver = filterChainResolverFactory.getInstance();
            this.innerFilter.setFilterChainResolver(resolver);
        }
	}
	*/
	
	/*
	@Override
	public void destroy() {
		this.innerFilter.destroy();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		this.innerFilter.doFilter(request, response, filterChain);
	}
	*/

}
