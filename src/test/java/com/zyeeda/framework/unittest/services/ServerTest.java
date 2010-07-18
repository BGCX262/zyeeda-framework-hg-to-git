package com.zyeeda.framework.unittest.services;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.zyeeda.framework.services.ApplicationServer;
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
}
