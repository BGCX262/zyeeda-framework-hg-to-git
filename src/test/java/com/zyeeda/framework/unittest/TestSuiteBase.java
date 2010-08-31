package com.zyeeda.framework.unittest;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.tapestry5.ioc.Registry;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.zyeeda.framework.FrameworkConstants;
import com.zyeeda.framework.FrameworkModule;
import com.zyeeda.framework.unittest.services.ServletContextMock;
import com.zyeeda.framework.web.ContextListener;

public class TestSuiteBase {
	
	private static ServletContext context;
	
	@BeforeSuite
	public static void setupContext() {
		context = new ServletContextMock();
		
		ContextListener ctxListener = new ContextListener() {
			
			@Override
			protected Class<?>[] provideExtraModules() {
				return new Class<?>[] {
						FrameworkModule.class
				};
			}
			
		};
		
		ServletContextEvent event = createStrictMock(ServletContextEvent.class);
		expect(event.getServletContext()).andReturn(context).anyTimes();
		replay(event);
		
		ctxListener.contextInitialized(event);
	}
	
	@AfterSuite
	public static void tearDownContext() {
		ContextListener ctxListener = new ContextListener();
		ServletContextEvent event = createStrictMock(ServletContextEvent.class);
		expect(event.getServletContext()).andReturn(context).anyTimes();
		replay(event);
		
		ctxListener.contextDestroyed(event);
	}
	
	protected static Registry getRegistry() {
		return (Registry) context.getAttribute(FrameworkConstants.SERVICE_REGISTRY);
	}
	
}
