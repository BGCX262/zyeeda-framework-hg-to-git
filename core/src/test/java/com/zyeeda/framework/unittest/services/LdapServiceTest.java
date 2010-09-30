package com.zyeeda.framework.unittest.services;

import java.io.IOException;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.apache.tapestry5.ioc.Registry;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.unittest.TestSuiteBase;

@Test
public class LdapServiceTest extends TestSuiteBase {

	@Test
	public void testGetLdapService() {
		LdapService ldapSvc = this.getService();
		assertNotNull(ldapSvc);
	}
	
	@Test
	public void testGetDefaultLdapContext() throws NamingException {
		LdapService ldapSvc = this.getService();
		LdapContext ctx = ldapSvc.getLdapContext();
		assertNotNull(ctx);
	}
	
	@Test
	public void testGetLdapContext() throws NamingException, IOException {
		LdapService ldapSvc = this.getService();
		//LdapContext ctx = ldapSvc.getLdapContext("mborn", "secret");
		LdapContext ctx = ldapSvc.getLdapContext("John Fryer", "pass");
		assertNotNull(ctx);
	}
	
	private LdapService getService() {
		Registry reg = getRegistry();
		LdapService ldapSvc = reg.getService(LdapService.class);
		return ldapSvc;
	}
}
