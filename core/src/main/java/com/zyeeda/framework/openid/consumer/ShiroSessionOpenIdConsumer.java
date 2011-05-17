package com.zyeeda.framework.openid.consumer;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.discovery.DiscoveryInformation;

public class ShiroSessionOpenIdConsumer extends HttpSessionOpenIdConsumer {

	public ShiroSessionOpenIdConsumer() throws ConsumerException {
		super();
	}
	
	@Override
	protected void storeDiscoveryInfo(HttpServletRequest httpReq, DiscoveryInformation discovered) {
		SecurityUtils.getSubject().getSession().setAttribute(OPENID_DISCOVERED_KEY, discovered);
	}
	
	@Override
	protected DiscoveryInformation retrieveDiscoveryInfo(HttpServletRequest httpReq) {
		return (DiscoveryInformation) SecurityUtils.getSubject().getSession().getAttribute(OPENID_DISCOVERED_KEY);
	}

}
