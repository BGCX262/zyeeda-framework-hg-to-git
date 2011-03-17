package com.zyeeda.framework.template;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.zyeeda.framework.service.Service;

public interface TemplateService extends Service {

	public void paint(String tplPath, Writer out, Map<String, Object> args) throws IOException;

	public void paint(String tplPath, Writer out) throws IOException;
	
	public String render(String template, Map<String, Object> args) throws IOException;

}
