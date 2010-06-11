package com.zyeeda.framework.unittest;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.zyeeda.framework.services.Server;
import com.zyeeda.framework.utils.JndiUtils;

public class TestSuiteBase {
	
	@BeforeSuite
	public Server bindServer() throws Exception {
		Server server = new TestServer();
		PropertiesConfiguration config = new PropertiesConfiguration();
        config.addProperty(Server.SERVER_ROOT, System.getProperty("serverRoot"));
        server.init(config);
		server.start();
		
		InitialContext initCtx = new InitialContext();
		initCtx.createSubcontext("java:comp");
		initCtx.createSubcontext("java:comp/env");
		initCtx.createSubcontext("java:comp/env/zyeeda");
		initCtx.bind("java:comp/env/zyeeda/TestServer", server);
		
		return server;
	}
	
	@AfterSuite
	public void unbindServer() throws Exception {
		Server server = (Server) JndiUtils.getObjectFromJndi("java:comp/env/zyeeda/TestServer");
		if (server != null) {
			server.stop();
		}
		
		Context initCtx = new InitialContext();
		
		initCtx.unbind("java:comp/env/zyeeda/TestServer");
		initCtx.destroySubcontext("java:comp/env/zyeeda");
		initCtx.destroySubcontext("java:comp/env");
		initCtx.destroySubcontext("java:comp");
	}
	
	protected Server getServer() throws Exception {
		return (Server) JndiUtils.getObjectFromJndi("java:comp/env/zyeeda/TestServer");
	}
	
}
