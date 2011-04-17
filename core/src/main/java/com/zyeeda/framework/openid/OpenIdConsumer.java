package com.zyeeda.framework.openid;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenIdConsumer {
	
	private final static Logger logger = LoggerFactory.getLogger(OpenIdConsumer.class);

	private final static String OPENID_DISCOVERED_KEY = "openid.discovered";
	
	private String redirectUrl;
	private String returnToUrl;
	
	private ConsumerManager manager;
	
	public OpenIdConsumer() throws ConsumerException {
		this.manager = new ConsumerManager();
        this.manager.setAssociations(new InMemoryConsumerAssociationStore());
        this.manager.setNonceVerifier(new InMemoryNonceVerifier(5000));
        this.manager.getRealmVerifier().setEnforceRpId(false);
	}
	
	public void authRequest(String userSuppliedId,
            HttpServletRequest request,
            HttpServletResponse response) throws OpenIDException, IOException, ServletException {
		
		logger.info("user supplied id = {}", userSuppliedId);
		
		List<?> discos = this.manager.discover(userSuppliedId);
		DiscoveryInformation discovered = this.manager.associate(discos);
		request.getSession().setAttribute(OPENID_DISCOVERED_KEY, discovered);
		AuthRequest authReq = this.manager.authenticate(discovered, this.returnToUrl);
		
		logger.debug("discovery info version = {}", discovered.getVersion());
		if (!discovered.isVersion2()) {
			response.sendRedirect(authReq.getDestinationUrl(true));
		} else {
			request.setAttribute("message", authReq);
			request.getRequestDispatcher(this.redirectUrl).forward(request, response);
		}
	}
	
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	
	public void setReturnToUrl(String returnToUrl) {
		this.returnToUrl = returnToUrl;
	}
	
	public String getReturnToUrl() {
		return this.returnToUrl;
	}
	
}
