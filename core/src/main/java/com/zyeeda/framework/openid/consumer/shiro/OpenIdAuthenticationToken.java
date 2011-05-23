package com.zyeeda.framework.openid.consumer.shiro;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.openid4java.discovery.Identifier;

public class OpenIdAuthenticationToken implements AuthenticationToken {

	private static final long serialVersionUID = 7305997052059544245L;
	
	private Identifier id;
	private String userId;
	
	public OpenIdAuthenticationToken(Identifier id) {
		this.id = id;
		
		String url = this.id.getIdentifier();
		this.userId = StringUtils.substringAfterLast(url, "id=");
	}

	@Override
	public Object getPrincipal() {
		if (id == null) {
			return null;
		}
		return this.userId;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

}
