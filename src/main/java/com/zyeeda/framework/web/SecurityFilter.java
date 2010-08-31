package com.zyeeda.framework.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.config.IniFilterChainResolverFactory;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.IniShiroFilter;
import org.apache.shiro.config.Ini;
import org.apache.shiro.config.IniFactorySupport;
import org.apache.shiro.mgt.SecurityManager;

import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.security.internal.ShiroSecurityServiceProvider;
import com.zyeeda.framework.server.ApplicationServer;

public class SecurityFilter implements Filter {
	
	private FilterConfig config;
	private IniShiroFilter innerFilter;
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
		this.innerFilter = new IniShiroFilter();
		
		SecurityService<SecurityManager> securityService = this.getSecurityService();
		SecurityManager securityMgr = securityService.getSecurityManager();
		if (!(securityMgr instanceof WebSecurityManager)) {
			throw new ServletException("Incompatible security manager.");
		}
		
		this.innerFilter.setSecurityManager((WebSecurityManager) securityService.getSecurityManager());
	
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

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		this.innerFilter.doFilter(request, response, filterChain);
	}
	
	@Override
	public void destroy() {
		this.innerFilter.destroy();
	}
	
	private ApplicationServer getServer() {
		return (ApplicationServer) this.config.getServletContext().getAttribute(ApplicationServer.class.getName());
	}
	
	private SecurityService<SecurityManager> getSecurityService() {
		return this.getServer().getService(ShiroSecurityServiceProvider.class);
	}

}
