package com.zyeeda.framework.openid;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.tapestry5.ioc.Registry;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.utils.IocUtils;

public class OpenIdAuthenticationFilter extends AuthenticatingFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(OpenIdAuthenticationFilter.class);
	
	private String returnToUrl;
	
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
		
		HttpServletRequest httpReq = (HttpServletRequest) request;
		
		Registry registry = IocUtils.getRegistry(this.getServletContext());
		OpenIdConsumerService openidConsumer = registry.getService(OpenIdConsumerService.class);
		Identifier id = openidConsumer.verifyResponse(httpReq);
		
		logger.info("Create OpenID authentication info.");
		AuthenticationToken token = new OpenIdAuthenticationToken(id);
		return token;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		
		logger.debug("Access denied.");
		
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		
		Registry registry = IocUtils.getRegistry(this.getServletContext());
		OpenIdConsumerService openidConsumer = registry.getService(OpenIdConsumerService.class);
		
		// 如果请求的是登录地址
		if (this.isLoginRequest(httpReq, httpRes)) {
			// 继续进入 OpenID 的登录界面
			logger.debug("OpenID login request detected, redirect to OP endpoint.");
			AuthRequest authReq = openidConsumer.authRequest(httpReq, httpRes);
			httpReq.setAttribute("message", authReq);
			//httpReq.getRequestDispatcher(this.redirectToUrl).forward(httpReq, httpRes);
			return true;
		}
		
		logger.debug("return to url = {}", this.returnToUrl);
		logger.debug("auth response url = {}", httpReq.getRequestURI());
		
		// 如果请求的是验证地址
		if (this.returnToUrl.equals(httpReq.getRequestURI())) {
			logger.debug("OpenID verify request detected, attempt to perform signin.");
			boolean success = this.executeLogin(httpReq, httpRes);
			logger.debug("OpenID login result = {}", success);
			if (success) {
				this.issueSuccessRedirect(httpReq, httpRes);
			} else {
				httpRes.setStatus(HttpServletResponse.SC_FORBIDDEN);
			}
			
			return false;
		}
		
		// 请求其它地址
		logger.debug("Permission denied on visiting resource [{}].", httpReq.getPathInfo());
		logger.debug("Forward to authentication url [{}].", this.getLoginUrl());
		this.saveRequest(request);
		httpRes.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
	}
	
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        boolean result = super.isAccessAllowed(request, response, mappedValue);
        logger.debug("isAccessAllowed = {}", result);
        return result;
    }
	
	@Override
	protected boolean onLoginFailure(AuthenticationToken token,	AuthenticationException e, ServletRequest request,
			ServletResponse response) {
		logger.error(e.getMessage(), e);
		return false;
	}
	
	public void setReturnToUrl(String returnToUrl) {
		this.returnToUrl = returnToUrl;
	}

}
