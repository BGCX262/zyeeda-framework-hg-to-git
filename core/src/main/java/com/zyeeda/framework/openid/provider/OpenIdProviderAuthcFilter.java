package com.zyeeda.framework.openid.provider;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.tapestry5.ioc.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.utils.IocUtils;

public class OpenIdProviderAuthcFilter extends FormAuthenticationFilter {
	
	private final static Logger logger = LoggerFactory.getLogger(OpenIdProviderAuthcFilter.class);

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		logger.debug("on access denied");
		
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		
		Registry registry = IocUtils.getRegistry(this.getServletContext());
		OpenIdProviderService opSvc = registry.getService(OpenIdProviderService.class);
		
		// 访问登录页面
		if (this.isLoginRequest(httpReq, httpRes)) {
			logger.debug("Sign in request detected");
			// 提交数据到登录页面
			if (this.isLoginSubmission(httpReq, httpRes)) {
				logger.debug("Sign in submission request detected!");
				boolean success = this.executeLogin(httpReq, httpRes);
				logger.debug("sign in result = {}", success);
				if (success) {
					httpRes.sendRedirect(this.getServletContext().getContextPath() + opSvc.getEndpointCompleteUrl());
				}
			}
			
			// 直接访问登录页面
			logger.debug("Sign in view request detected!");
			return true;
		}
		
		// 访问其它页面，转发到登录页面
		logger.debug("Redirect to sign in page {}", this.getLoginUrl());
		httpRes.sendRedirect(this.getLoginUrl());
		return false;
	}
	
	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
            ServletRequest request, ServletResponse response) throws Exception {
		logger.debug("subject.isAuthenticated = {}", subject.isAuthenticated());
		logger.debug("subject.isRemembered = {}", subject.isRemembered());
		return true;
	}
	
	@Override
	protected boolean onLoginFailure(AuthenticationToken token,	AuthenticationException e, ServletRequest request,
			ServletResponse response) {
		logger.error(e.getMessage(), e);
		return false;
	}
	
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        boolean result = super.isAccessAllowed(request, response, mappedValue);
        logger.debug("is access allowed = {}", result);
        return result;
    }
}
