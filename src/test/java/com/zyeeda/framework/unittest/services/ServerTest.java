/*package com.zyeeda.framework.unittest.services;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import javax.persistence.EntityManager;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.ldap.internal.SunJndiLdapServiceProvider;
import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.persistence.internal.JpaPersistenceServiceProvider;
import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.security.internal.ShiroSecurityServiceProvider;
import com.zyeeda.framework.server.ApplicationServer;
import com.zyeeda.framework.template.TemplateService;
import com.zyeeda.framework.template.internal.FreemarkerTemplateServiceProvider;
import com.zyeeda.framework.unittest.TestSuiteBase;

import static org.testng.Assert.*;

//@Test
public class ServerTest extends TestSuiteBase {
	
	private ApplicationServer server;
	
	//@BeforeTest
	public void setUp() throws Exception {
		this.server = this.getServer();
	}

	//@Test
	public void testInitServer() {
		assertNotNull(server);
	}
	
	//@Test
	public void testGetTemplateService() {
		TemplateService tplSvc = this.server.getService(FreemarkerTemplateServiceProvider.class);
		assertNotNull(tplSvc);
	}
	
	//@Test
	public void testGetSecurityService() {
		SecurityService<?> securitySvc = this.server.getService(ShiroSecurityServiceProvider.class);
		assertNotNull(securitySvc);
	}
	
	//@Test
	public void testGetLdapService() {
		LdapService ldapSvc = this.server.getService(SunJndiLdapServiceProvider.class);
		assertNotNull(ldapSvc);
	}
	
	//@Test
	public void testGetLdapContext() throws NamingException, IOException {
		LdapService ldapSvc = this.server.getService(SunJndiLdapServiceProvider.class);
		LdapContext ctx = ldapSvc.getLdapContext("mborn", "secret");
		assertNotNull(ctx);
	}
	
	//@Test
	public void testGetSystemLdapContext() throws NamingException {
		LdapService ldapSvc = this.server.getService(SunJndiLdapServiceProvider.class);
		LdapContext ctx = ldapSvc.getLdapContext();
		assertNotNull(ctx);
	}
	
	//@Test
	public void testGetPersistenceService() {
		PersistenceService<EntityManager> persistenceSvc = this.server.getService(JpaPersistenceServiceProvider.class);
		assertNotNull(persistenceSvc);
	}
	
	//@Test
	public void testGetRolesBySubject() throws Throwable {
		SecurityService<?> securitySvc = this.server.getService(ShiroSecurityServiceProvider.class);
		RoleManager roleMgr = securitySvc.getRoleManager();
		List<Role> roles = roleMgr.getRolesBySubject("aeinstein");
		assertEquals(1, roles.size());
	}
}*/
