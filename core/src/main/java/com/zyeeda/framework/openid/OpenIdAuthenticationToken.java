package com.zyeeda.framework.openid;

import org.apache.shiro.authc.AuthenticationToken;
import org.openid4java.discovery.Identifier;

public class OpenIdAuthenticationToken implements AuthenticationToken {

	private static final long serialVersionUID = 7305997052059544245L;
	
	private Identifier id;
	
	public OpenIdAuthenticationToken(Identifier id) {
		this.id = id;
	}

	@Override
	public Object getPrincipal() {
		if (id == null) {
			return null;
		}
		return this.id.getIdentifier();
	}

	@Override
	public Object getCredentials() {
		return null;
	}

}
