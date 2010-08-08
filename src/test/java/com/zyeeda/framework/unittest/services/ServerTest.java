package com.zyeeda.framework.unittest.services;

import java.io.IOException;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.zyeeda.framework.server.ApplicationServer;
import com.zyeeda.framework.services.LdapService;
import com.zyeeda.framework.services.SecurityService;
import com.zyeeda.framework.services.TemplateService;
import com.zyeeda.framework.services.internal.FreemarkerTemplateServiceProvider;
import com.zyeeda.framework.services.internal.ShiroSecurityServiceProvider;
import com.zyeeda.framework.services.internal.SunJndiLdapServiceProvider;
import com.zyeeda.framework.unittest.TestSuiteBase;

import static org.testng.Assert.*;

@Test
public class ServerTest extends TestSuiteBase {
	
	private ApplicationServer server;
	
	@BeforeTest
	public void setUp() throws Exception {
		this.server = this.getServer();
	}

	@Test
	public void testInitServer() {
		assertNotNull(server);
	}
	
	@Test
	public void testGetTemplateService() {
		TemplateService tplSvc = this.server.getService(FreemarkerTemplateServiceProvider.class);
		assertNotNull(tplSvc);
	}
	
	@Test
	public void testGetSecurityService() {
		SecurityService<?> securitySvc = this.server.getService(ShiroSecurityServiceProvider.class);
		assertNotNull(securitySvc);
	}
	
	@Test
	public void testGetLdapService() {
		LdapService ldapSvc = this.server.getService(SunJndiLdapServiceProvider.class);
		assertNotNull(ldapSvc);
	}
	
	@Test
	public void testGetLdapContext() throws NamingException, IOException {
		LdapService ldapSvc = this.server.getService(SunJndiLdapServiceProvider.class);
		LdapContext ctx = ldapSvc.getLdapContext("mborn", "secret");
		assertNotNull(ctx);
	}
	
	@Test
	public void testGetSystemLdapContext() throws NamingException {
		LdapService ldapSvc = this.server.getService(SunJndiLdapServiceProvider.class);
		LdapContext ctx = ldapSvc.getLdapContext();
		assertNotNull(ctx);
	}
}
