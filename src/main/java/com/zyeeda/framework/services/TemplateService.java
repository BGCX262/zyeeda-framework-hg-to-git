package com.zyeeda.framework.services;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public interface TemplateService extends Service {

	public void paint(String tplPath, Writer out, Map<?, ?> args) throws IOException;

	public void paint(String tplPath, Writer out) throws IOException;
	
	public String render(String template, Map<?, ?> args) throws IOException;

}
