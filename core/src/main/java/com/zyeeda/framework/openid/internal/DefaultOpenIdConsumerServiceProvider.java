package com.zyeeda.framework.openid.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.openid4java.OpenIDException;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;

import com.zyeeda.framework.config.ConfigurationService;
import com.zyeeda.framework.openid.OpenIdConsumer;
import com.zyeeda.framework.openid.OpenIdConsumerService;
import com.zyeeda.framework.service.AbstractService;

@ServiceId("default-openid-consumer-service")
@Marker(Primary.class)
public class DefaultOpenIdConsumerServiceProvider extends AbstractService
		implements OpenIdConsumerService {

	private final static String RETURN_TO_URL_KEY = "returnToUrl";
	private final static String PUBLIC_IDENTIFIER_KEY = "publicIdentifier";
	private final static String REALM_KEY = "realm";
	
	private final static String DEFAULT_RETURN_TO_URL = "/accounts/openid/verify";
	
	private String returnToUrl;
	private String publicIdentifier;
	private String realm;
	
	private OpenIdConsumer consumer;
	
	public DefaultOpenIdConsumerServiceProvider(
			@Primary ConfigurationService configSvc,
			RegistryShutdownHub shutdownHub) {
		
		super(shutdownHub);
		
		Configuration config = this.getConfiguration(configSvc);
		this.init(config);
	}
	
	private void init(Configuration config) {
		this.returnToUrl = config.getString(RETURN_TO_URL_KEY, DEFAULT_RETURN_TO_URL);
		this.publicIdentifier = config.getString(PUBLIC_IDENTIFIER_KEY);
		this.realm = config.getString(REALM_KEY);
	}
	
	@Override
	public void start() throws Exception {
		this.consumer = new OpenIdConsumer();
		this.consumer.setReturnToUrl(this.returnToUrl);
		this.consumer.setRealm(this.realm);
	}
	
	@Override
	public void stop() {
		this.consumer = null;
	}

	@Override
	public AuthRequest authRequest(HttpServletRequest request, HttpServletResponse response) throws OpenIDException {
		return this.consumer.authRequest(this.publicIdentifier, request, response);
	}

	@Override
	public Identifier verifyResponse(HttpServletRequest request) throws OpenIDException {
		return this.consumer.verifyResponse(request);
	}

}
