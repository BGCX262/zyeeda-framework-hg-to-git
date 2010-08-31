package com.zyeeda.framework.security.internal;

import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;

import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.persistence.PersistenceService;

public class ShiroSecurityManager extends DefaultWebSecurityManager {

	public ShiroSecurityManager(LdapService ldapSvc,
			PersistenceService persistenceSvc,
			Logger logger) {
		super(new ShiroCombinedRealm(ldapSvc, persistenceSvc, logger));
	}
	
}
