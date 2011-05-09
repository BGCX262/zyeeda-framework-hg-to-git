package com.zyeeda.framework.openid.provider;

import org.openid4java.message.Message;
import org.openid4java.message.ParameterList;

import com.zyeeda.framework.service.Service;

public interface OpenIdProviderService extends Service {

	public Message associateRequest(ParameterList params);
	
	public Message verifyRequest(ParameterList params);
	
	public Message authResponse(ParameterList params, String userSelectedId,
			String userSelectedClaimedId, boolean authenticatedAndApproved, String opEndpointUrl);
	
	public String getEndpointUrl();
	
	public String getEndpointCompleteUrl();
	
}
