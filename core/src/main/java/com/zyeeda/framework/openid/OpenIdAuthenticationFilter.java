package com.zyeeda.framework.openid;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.tapestry5.ioc.Registry;
import org.openid4java.message.AuthRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.utils.IocUtils;

public class OpenIdAuthenticationFilter extends AuthenticatingFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(OpenIdAuthenticationFilter.class);
	
	private String returnToUrl;
	private String redirectToUrl;
	
    @Override
    public void setLoginUrl(String loginUrl) {
        String previous = getLoginUrl();
        if (previous != null) {
            this.appliedPaths.remove(previous);
        }
        super.setLoginUrl(loginUrl);
        logger.trace("Adding login url to applied paths.");
        this.appliedPaths.put(getLoginUrl(), null);
    }

	@Override
	protected AuthenticationToken createToken(ServletRequest request,
			ServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		
		Registry registry = IocUtils.getRegistry(this.getServletContext());
		OpenIdConsumerService openidConsumer = registry.getService(OpenIdConsumerService.class);
		
		// 如果请求的是登录地址
		if (this.isLoginRequest(httpReq, httpRes)) {
			// 继续进入 OpenID 的登录界面
			logger.debug("OpenID auth request detected, redirect to OP endpoint.");
			AuthRequest authReq = openidConsumer.authRequest(httpReq, httpRes);
			httpReq.setAttribute("message", authReq);
			httpReq.getRequestDispatcher(this.redirectToUrl).forward(httpReq, httpRes);
			return false;
		}
		
		logger.debug("return to url = {}", this.returnToUrl);
		logger.debug("auth response url = {}", httpReq.getRequestURL().toString());
		
		// 如果请求的是验证地址
		if (this.returnToUrl.equals(httpReq.getRequestURL().toString())) {
			logger.debug("OpenID auth response detected, attempt to perform signin.");
			this.executeLogin(httpReq, httpRes);
		}
		
		// 请求其它地址
		logger.debug("Permission denied on visiting resource [{}].", httpReq.getPathInfo());
		logger.debug("Forward to authentication url [{}].", this.getLoginUrl());
		this.saveRequestAndRedirectToLogin(httpReq, httpRes);
        return false;
	}
	
	public void setReturnToUrl(String returnToUrl) {
		this.returnToUrl = returnToUrl;
	}
	
	public void setRedirectToUrl(String redirectToUrl) {
		this.redirectToUrl = redirectToUrl;
	}

}
