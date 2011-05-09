package com.zyeeda.framework.openid.consumer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.ParameterList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenIdConsumer {
	
	private final static Logger logger = LoggerFactory.getLogger(OpenIdConsumer.class);

	private final static String OPENID_DISCOVERED_KEY = "openid.discovered";
	
	//private String redirectUrl;
	private String returnToUrl;
	private String realm;
	
	private ConsumerManager manager;
	
	public OpenIdConsumer() throws ConsumerException {
		this.manager = new ConsumerManager();
		this.manager.setConnectTimeout(300000);
		this.manager.setSocketTimeout(300000);
        this.manager.setAssociations(new InMemoryConsumerAssociationStore());
        this.manager.setNonceVerifier(new InMemoryNonceVerifier(60000));
        this.manager.getRealmVerifier().setEnforceRpId(false);
	}
	
	public AuthRequest authRequest(String userSuppliedId,
            HttpServletRequest request,
            HttpServletResponse response) throws OpenIDException {
		
		logger.info("user supplied id = {}", userSuppliedId);
		
		List<?> discos = this.manager.discover(userSuppliedId);
		DiscoveryInformation discovered = this.manager.associate(discos);
		request.getSession().setAttribute(OPENID_DISCOVERED_KEY, discovered);
		AuthRequest authReq = this.manager.authenticate(discovered, this.returnToUrl);
		authReq.setRealm(this.realm);
		
		return authReq;
		/*
		logger.debug("discovery info version = {}", discovered.getVersion());
		if (discovered.isVersion2()) {
			request.setAttribute("message", authReq);
			request.getRequestDispatcher(this.redirectUrl).forward(request, response);
			return;
		}
		response.sendRedirect(authReq.getDestinationUrl(true));*/
	}
	
	public Identifier verifyResponse(HttpServletRequest request) throws OpenIDException {
		ParameterList response = new ParameterList(request.getParameterMap());

		DiscoveryInformation discovered = (DiscoveryInformation) request
				.getSession().getAttribute(OPENID_DISCOVERED_KEY);

		StringBuffer receivingURL = request.getRequestURL();
		String queryString = request.getQueryString();
		if (queryString != null && queryString.length() > 0) {
			receivingURL.append("?").append(request.getQueryString());
		}

		VerificationResult verification = this.manager.verify(receivingURL.toString(), response, discovered);

		Identifier verified = verification.getVerifiedId();
		return verified;
	}

	/*public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}*/
	
	public void setReturnToUrl(String returnToUrl) {
		this.returnToUrl = returnToUrl;
	}
	
	public String getReturnToUrl() {
		return this.returnToUrl;
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}
	
}
