package com.zyeeda.framework.unittest.services;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class ServletContextMock implements ServletContext {
	
	private Map<String, Object> attributes = new HashMap<String, Object>();

	@Override
	public Object getAttribute(String name) {
		return this.attributes.get(name);
	}

	@Override
	public Enumeration<?> getAttributeNames() {
		return null;
	}

	@Override
	public ServletContext getContext(String arg0) {
		return null;
	}

	@Override
	public String getContextPath() {
		return null;
	}

	@Override
	public String getInitParameter(String arg0) {
		return null;
	}

	@Override
	public Enumeration<?> getInitParameterNames() {
		return new Enumeration<Object>() {

			@Override
			public boolean hasMoreElements() {
				return false;
			}

			@Override
			public Object nextElement() {
				return null;
			}
			
		};
	}

	@Override
	public int getMajorVersion() {
		return 0;
	}

	@Override
	public String getMimeType(String arg0) {
		return null;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String arg0) {
		return null;
	}

	@Override
	public String getRealPath(String path) {
		return System.getProperty("framework.application.root");
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return null;
	}

	@Override
	public URL getResource(String arg0) throws MalformedURLException {
		return null;
	}

	@Override
	public InputStream getResourceAsStream(String arg0) {
		return null;
	}

	@Override
	public Set<?> getResourcePaths(String arg0) {
		return null;
	}

	@Override
	public String getServerInfo() {
		return null;
	}

	@Override
	public Servlet getServlet(String arg0) throws ServletException {
		return null;
	}

	@Override
	public String getServletContextName() {
		return null;
	}

	@Override
	public Enumeration<?> getServletNames() {
		return null;
	}

	@Override
	public Enumeration<?> getServlets() {
		return null;
	}

	@Override
	public void log(String arg0) {
	}

	@Override
	public void log(Exception arg0, String arg1) {
	}

	@Override
	public void log(String arg0, Throwable arg1) {
	}

	@Override
	public void removeAttribute(String name) {
		this.attributes.remove(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		this.attributes.put(name, value);
	}

}
