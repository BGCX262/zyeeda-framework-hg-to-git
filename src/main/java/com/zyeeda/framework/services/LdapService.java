package com.zyeeda.framework.services;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

public interface LdapService extends Service {

	public LdapContext getLdapContext() throws NamingException;
	
	public LdapContext getLdapContext(String username, String password) throws NamingException;
	
}
