package com.zyeeda.framework.unittest.services;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.zyeeda.framework.server.ApplicationServer;
import com.zyeeda.framework.services.SecurityService;
import com.zyeeda.framework.services.internal.ShiroSecurityServiceProvider;
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
	
	public void testGetSecurityService() {
		SecurityService<?> securitySvc = this.server.getService(ShiroSecurityServiceProvider.class);
		assertNotNull(securitySvc);
	}
}
