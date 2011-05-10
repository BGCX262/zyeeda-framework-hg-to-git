package com.zyeeda.framework.security.internal;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;

import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.security.realms.LdapRealm;
import com.zyeeda.framework.service.AbstractService;

@Marker(Primary.class)
@ServiceId("openid-provider-security-service")
public class OpenIdProviderSecurityServiceProvider extends AbstractService implements SecurityService<SecurityManager> {

	// Injected
	private final LdapService ldapSvc;
	private final SecurityManager securityMgr;
	
	public OpenIdProviderSecurityServiceProvider(
			@Primary LdapService ldapSvc,
			RegistryShutdownHub shutdownHub) {
		
		super(shutdownHub);
		this.ldapSvc = ldapSvc;
		this.securityMgr = new ShiroSecurityManager();
	}

	@Override
	public SecurityManager getSecurityManager() {
		return this.securityMgr;
	}
	
	/**
	 * 获取当前登录用户的唯一标识。
	 * 如果是匿名用户，则返回 null。
	 */
	@Override
	public String getCurrentUser() {
		return "admin";
		//Subject current = SecurityUtils.getSubject();
		//return current.getPrincipal().toString();
	}
	
	private class ShiroSecurityManager extends DefaultWebSecurityManager {
		public ShiroSecurityManager() {
			super(new LdapRealm(ldapSvc));
		}
	}

}
