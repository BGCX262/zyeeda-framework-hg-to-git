package com.zyeeda.framework.unittest;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.zyeeda.framework.server.ApplicationServer;

public class TestSuiteBase {
	
	private ApplicationServer server;
	
	@BeforeSuite
	public ApplicationServer setupServer() throws Exception {
		ApplicationServer server = new ApplicationServer();
		PropertiesConfiguration config = new PropertiesConfiguration();
        config.addProperty(ApplicationServer.SERVER_ROOT, System.getProperty("serverRoot"));
        server.init(config);
		server.start();
		
		this.server = server;
		
		return server;
	}
	
	@AfterSuite
	public void tearDownServer() throws Exception {
		if (this.server != null) {
			this.server.stop();
		}
	}
	
	protected ApplicationServer getServer() {
		return this.server;
	}
	
}
