package com.zyeeda.framework.openid.provider;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.apache.tapestry5.ioc.Registry;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.DirectError;
import org.openid4java.message.Message;
import org.openid4java.message.ParameterList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.utils.IocUtils;

public class OpenIdProviderEndpointFilter extends PathMatchingFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(OpenIdProviderEndpointFilter.class);
	
	private static final String OP_LOCAL_IDENTIFIER_TEMPLATE = "%s://%s:%s%s/provider/user.jsp?id=%s";
	private static final String FULL_ENDPOINT_URL_TEMPLATE = "%s://%s:%s%s/provider/endpoint";
	
	@Override
	protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		
		Registry registry = IocUtils.getRegistry(this.getServletContext());
		OpenIdProviderService opSvc = registry.getService(OpenIdProviderService.class);
		
		ParameterList params = null;
		if (this.pathsMatch(opSvc.getEndpointCompleteUrl(), httpReq)) {
			logger.debug("Endpoint complete request detected!");
			params = (ParameterList) SecurityUtils.getSubject().getSession().getAttribute("params");
		} else {
			params = new ParameterList(httpReq.getParameterMap());
		}
		
        String mode = params.hasParameter("openid.mode") ? params.getParameterValue("openid.mode") : null;
        logger.debug("OpenID mode = {}", mode);
        
        if ("associate".equals(mode)) {
        	logger.debug("OpenID request mode [associate] detected!");
        	Message message = opSvc.associateRequest(params);
        	this.outputMessage(message, httpRes);
        	return false;
        }
        
        if ("check_authentication".equals(mode)) {
        	logger.debug("OpenID request mode [check_authentication] detected!");
        	Message message = opSvc.verifyRequest(params);
        	this.outputMessage(message, httpRes);
        	return false;
        }
        
        if ("checkid_setup".equals(mode) || "checkid_immediate".equals(mode)) {
        	logger.debug("OpenID request mode [checkid_immediate] or [checkid_setup] detected!");
        	Subject s = SecurityUtils.getSubject();
        	if (s.isAuthenticated()) {
                String userSelectedId = (s.getPrincipals().iterator().next()).toString();
                userSelectedId = String.format(OP_LOCAL_IDENTIFIER_TEMPLATE, httpReq.getScheme(), httpReq.getServerName(), 
                		httpReq.getServerPort(), httpReq.getContextPath(), userSelectedId);
                String userSelectedClaimedId = userSelectedId;
                
                logger.debug("user selected id = {}", userSelectedId);
                logger.debug("user selected claimed id = {}", userSelectedClaimedId);
                
                String fullEndpointUrl = String.format(FULL_ENDPOINT_URL_TEMPLATE, httpReq.getScheme(), httpReq.getServerName(),
                		httpReq.getServerPort(), httpReq.getContextPath());
                logger.debug("full endpoint url = {}", fullEndpointUrl);
                Message message = opSvc.authResponse(params, userSelectedId, userSelectedClaimedId, s.isAuthenticated(), fullEndpointUrl);
                
                if (message instanceof AuthSuccess) {
                    httpRes.sendRedirect(((AuthSuccess) message).getDestinationUrl(true));
                    return false;
                }
                // TODO
                httpRes.getWriter().print("<pre>" + message.keyValueFormEncoding() + "</pre>");
                return false;
        	}
        	
        	httpReq.getSession().setAttribute("params", params);
        	SecurityUtils.getSubject().getSession().setAttribute("params", params);
        	return true;
        }
        
        logger.debug("Unknown OpenID request mode [{}]", mode);
        Message message = DirectError.createDirectError("Invalid OpenID request!");
        this.outputMessage(message, httpRes);
        return false;
	}
	
	private void outputMessage(Message message, ServletResponse response) throws IOException {
		String messageText = message.keyValueFormEncoding();
    	response.getWriter().print(messageText);
	}


/*
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		logger.debug("on access denied");
		
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		
		// 访问登录页面
		if (this.isLoginRequest(httpReq, httpRes)) {
			logger.debug("this is sign in request");
			// POST 数据到登录界面
			if (this.isLoginSubmission(httpReq, httpRes)) {
				logger.debug("Sign in page submission request detected!");
				return this.executeLogin(httpReq, httpRes);
			}
			
			// 直接访问登录界面，允许
			logger.debug("Sign in page view request detected!");
			
			SecurityUtils.getSubject().getSession().setAttribute("params", params);
			return true;
		}
		
		// 访问其它页面，转发到登录页面
		logger.debug("Redirect to sign in page {}", this.getLoginUrl());
		httpRes.sendRedirect(this.getLoginUrl());
		return false;
	}
	*/
	
	/*
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
			ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		
		Registry registry = IocUtils.getRegistry(this.getServletContext());
		OpenIdProviderService opSvc = registry.getService(OpenIdProviderService.class);
		
		Message message = opSvc.authResponse(httpReq, httpRes);
		if (message instanceof AuthSuccess) {
			AuthSuccess authSuccess = (AuthSuccess) message;
			httpRes.sendRedirect(authSuccess.getDestinationUrl(true));
		} else {
			// TODO
		}
		return false;
	}
	*/
	/*
	protected boolean onLoginFailure(AuthenticationToken token,	AuthenticationException e,
			ServletRequest request,	ServletResponse response) {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		try {
			httpReq.getRequestDispatcher(this.getLoginUrl()).forward(httpReq, httpRes);
		} catch (ServletException exception) {
			throw new RuntimeException(exception);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		return false;
	}
*/
	

}
