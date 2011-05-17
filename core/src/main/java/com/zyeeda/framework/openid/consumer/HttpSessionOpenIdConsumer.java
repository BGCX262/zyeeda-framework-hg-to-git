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

public class HttpSessionOpenIdConsumer {
	
	private final static Logger logger = LoggerFactory.getLogger(HttpSessionOpenIdConsumer.class);

	protected final static String OPENID_DISCOVERED_KEY = "openid.discovered";
	
	private String returnToUrl;
	private String realm;
	
	private ConsumerManager manager;
	
	public HttpSessionOpenIdConsumer() throws ConsumerException {
		this.manager = new ConsumerManager();
		this.manager.setConnectTimeout(300000);
		this.manager.setSocketTimeout(300000);
        this.manager.setAssociations(new InMemoryConsumerAssociationStore());
        this.manager.setNonceVerifier(new InMemoryNonceVerifier(60000));
        this.manager.getRealmVerifier().setEnforceRpId(false);
	}
	
	public AuthRequest authRequest(String userSuppliedId,
            HttpServletRequest httpReq,
            HttpServletResponse httpRes) throws OpenIDException {
		
		logger.info("user supplied id = {}", userSuppliedId);
		
		List<?> discos = this.manager.discover(userSuppliedId);
		DiscoveryInformation discovered = this.manager.associate(discos);
		this.storeDiscoveryInfo(httpReq, discovered);
		AuthRequest authReq = this.manager.authenticate(discovered, this.returnToUrl);
		authReq.setRealm(this.realm);
		
		return authReq;
	}
	
	public Identifier verifyResponse(HttpServletRequest httpReq) throws OpenIDException {
		ParameterList response = new ParameterList(httpReq.getParameterMap());

		DiscoveryInformation discovered = this.retrieveDiscoveryInfo(httpReq);

		StringBuffer receivingURL = httpReq.getRequestURL();
		String queryString = httpReq.getQueryString();
		if (queryString != null && queryString.length() > 0) {
			receivingURL.append("?").append(httpReq.getQueryString());
		}

		VerificationResult verification = this.manager.verify(receivingURL.toString(), response, discovered);

		Identifier verified = verification.getVerifiedId();
		return verified;
	}
	
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
	
	protected void storeDiscoveryInfo(HttpServletRequest httpReq, DiscoveryInformation discovered) {
		httpReq.getSession().setAttribute(OPENID_DISCOVERED_KEY, discovered);
	}
	
	protected DiscoveryInformation retrieveDiscoveryInfo(HttpServletRequest httpReq) {
		return (DiscoveryInformation) httpReq.getSession().getAttribute(OPENID_DISCOVERED_KEY);
	}
	
}
