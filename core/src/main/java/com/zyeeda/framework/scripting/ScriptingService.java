package com.zyeeda.framework.scripting;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import javax.script.ScriptException;

import com.zyeeda.framework.service.Service;

public interface ScriptingService extends Service {

	public Object eval(File scriptFile, Map<String, Object> args) throws FileNotFoundException, ScriptException;
	
}
