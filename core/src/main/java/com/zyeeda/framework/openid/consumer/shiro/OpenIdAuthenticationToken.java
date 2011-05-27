package com.zyeeda.framework.openid.consumer.shiro;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.openid4java.discovery.Identifier;

public class OpenIdAuthenticationToken implements AuthenticationToken {

	private static final long serialVersionUID = 7305997052059544245L;
	
	private String userId;
	
	public OpenIdAuthenticationToken(Identifier id) {
		String url = id.getIdentifier();
		this.userId = StringUtils.substringAfterLast(url, "id=");
	}
	
	public OpenIdAuthenticationToken(String userId) {
		this.userId = userId;
	}

	@Override
	public Object getPrincipal() {
		return this.userId;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

}
