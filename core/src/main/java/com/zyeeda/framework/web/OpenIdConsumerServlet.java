package com.zyeeda.framework.web;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.openid.OpenIdConsumer;

public class OpenIdConsumerServlet extends HttpServlet {

	private static final long serialVersionUID = 6393634729613708155L;
	
	private static final Logger logger = LoggerFactory.getLogger(OpenIdConsumerServlet.class);
	
	private static final String OPENID_CONSUMER_KEY = "openid.consumer";
	//private static final String IDENTIFIER_KEY = "openid.identifier";
	private static final String REDIRECT_TO_URL_KEY = "redirect.to.url";
	private static final String RETURN_TO_URL_KEY = "return.to.url";
	private static final String PUBLIC_IDENTIFIER_KEY = "public.identifier";
	
	private String publicIdentifier;
	private OpenIdConsumer consumer;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		this.publicIdentifier = config.getInitParameter(PUBLIC_IDENTIFIER_KEY);
		
		this.consumer = (OpenIdConsumer) this.getServletContext().getAttribute(OPENID_CONSUMER_KEY);
		if (this.consumer == null) {
			try {
				this.consumer = new OpenIdConsumer();
				this.consumer.setRedirectUrl(config.getInitParameter(REDIRECT_TO_URL_KEY));
				this.consumer.setReturnToUrl(config.getInitParameter(RETURN_TO_URL_KEY));
				this.getServletContext().setAttribute(OPENID_CONSUMER_KEY, this.consumer);
			} catch (ConsumerException e) {
				throw new ServletException(e);
			}
		}
		logger.info("Initialized OpenID Consumer Servlet.");
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.debug("servlet path info = {}", request.getPathInfo());
		
		if ("/verify".equals(request.getServletPath())) {
			logger.info("Verify OpenId auth response.");
			return;
		}
		
		try {
			this.consumer.authRequest(this.publicIdentifier, request, response);
		} catch (OpenIDException e) {
			throw new ServletException(e);
		}		
	}

}
