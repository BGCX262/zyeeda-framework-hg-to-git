package com.zyeeda.framework.security.internal;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;

import com.zyeeda.framework.openid.consumer.shiro.BypassCredentialsMatcher;
import com.zyeeda.framework.openid.consumer.shiro.OpenIdConsumerRealm;
import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.service.AbstractService;

@Marker(Primary.class)
@ServiceId("openid-consumer-security-service")
public class OpenIdConsumerSecurityServiceProvider extends AbstractService implements SecurityService<SecurityManager> {
	
	private SecurityManager securityMgr;

	public OpenIdConsumerSecurityServiceProvider(RegistryShutdownHub shutdownHub) {
		super(shutdownHub);
		
		this.securityMgr = new ShiroSecurityManager();
	}

	@Override
	public SecurityManager getSecurityManager() {
		return this.securityMgr;
	}

	@Override
	public String getCurrentUser() {
		Subject current = SecurityUtils.getSubject();
		return current.getPrincipal().toString();
	}
	
	private class ShiroSecurityManager extends DefaultWebSecurityManager {
		public ShiroSecurityManager() {
			super();
			OpenIdConsumerRealm realm = new OpenIdConsumerRealm();
			CredentialsMatcher matcher = new BypassCredentialsMatcher();
			realm.setCredentialsMatcher(matcher);
			this.setRealm(realm);
		}
	}

}
