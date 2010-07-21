package com.zyeeda.framework.services;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;


import freemarker.template.TemplateException;

public interface TemplateService extends Service {

	public void paint(String tplPath, Writer out, Map<?, ?> args) throws IOException, TemplateException;
	
}
