package com.zyeeda.framework.security.internal;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;

import com.zyeeda.framework.openid.consumer.shiro.OpenIdConsumerRealm;
import com.zyeeda.framework.security.annotations.Virtual;

@Marker(Virtual.class)
@ServiceId("virtual-consumer-security-service")
public class VirtualConsumerSecurityServiceProvider extends AbstractSecurityServiceProvider {
	
	private SecurityManager securityMgr;

	public VirtualConsumerSecurityServiceProvider(RegistryShutdownHub shutdownHub) {
		super(shutdownHub);
		
		this.securityMgr = new ShiroSecurityManager();
	}

	@Override
	public SecurityManager getSecurityManager() {
		return this.securityMgr;
	}
	
	private class ShiroSecurityManager extends DefaultSecurityManager {
		public ShiroSecurityManager() {
			super(new OpenIdConsumerRealm());
		}
	}

}
