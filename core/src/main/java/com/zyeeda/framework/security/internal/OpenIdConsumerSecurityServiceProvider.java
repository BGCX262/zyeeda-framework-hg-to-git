package com.zyeeda.framework.security.internal;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;

import com.zyeeda.framework.openid.consumer.shiro.OpenIdConsumerRealm;

@Marker(Primary.class)
@ServiceId("openid-consumer-security-service")
public class OpenIdConsumerSecurityServiceProvider extends AbstractSecurityServiceProvider {
	
	private SecurityManager securityMgr;

	public OpenIdConsumerSecurityServiceProvider(RegistryShutdownHub shutdownHub) {
		super(shutdownHub);
		
		this.securityMgr = new ShiroSecurityManager();
	}

	@Override
	public SecurityManager getSecurityManager() {
		return this.securityMgr;
	}
	
	private class ShiroSecurityManager extends DefaultWebSecurityManager {
		public ShiroSecurityManager() {
			super(new OpenIdConsumerRealm());
		}
	}

}
