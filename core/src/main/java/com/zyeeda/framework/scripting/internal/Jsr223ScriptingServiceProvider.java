package com.zyeeda.framework.scripting.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.configuration.Configuration;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.config.ConfigurationService;
import com.zyeeda.framework.scripting.ScriptingService;
import com.zyeeda.framework.service.AbstractService;

@ServiceId("jsr223-scripting-service-provider")
@Marker(Primary.class)
public class Jsr223ScriptingServiceProvider extends AbstractService implements ScriptingService {

	private static final Logger logger = LoggerFactory.getLogger(Jsr223ScriptingServiceProvider.class);
	
	private static final String SCRIPT_REPOSITORY_ROOT = "scriptRepositoryRoot";
	
	private static final String DEFAULT_SCRIPT_REPOSITORY_ROOT = "WEB-INF/scripts";
	private static final String DEFAULT_SCRIPT_ENGINE_NAME = "groovy";
	
	private final ConfigurationService configSvc;
	
	private File repoRoot;
	private ScriptEngineManager manager;
	
	public Jsr223ScriptingServiceProvider(ConfigurationService configSvc,
			RegistryShutdownHub shutdownHub) throws Exception {
		super(shutdownHub);
		
		this.configSvc = configSvc;
		
		Configuration config = this.getConfiguration(this.configSvc);
		this.init(config);
	}
	
	private void init(Configuration config) throws Exception {
		String repoRoot = config.getString(SCRIPT_REPOSITORY_ROOT, DEFAULT_SCRIPT_REPOSITORY_ROOT);
		logger.debug("script repository root = {}", repoRoot);
		
		this.repoRoot = new File(this.configSvc.getApplicationRoot(), repoRoot);
		if (!this.repoRoot.exists()) {
    		throw new FileNotFoundException(this.repoRoot.toString());
    	}
	}
	
	@Override
	public void start() {
		this.manager = new ScriptEngineManager(this.getClass().getClassLoader());
	}

	@Override
	public Object eval(File scriptFile, Map<String, Object> args) throws FileNotFoundException, ScriptException {
    	ScriptEngine engine = manager.getEngineByName(DEFAULT_SCRIPT_ENGINE_NAME);

        Bindings bindings = engine.createBindings();
        bindings.putAll(args);
        FileReader reader = null;
        try {
            reader = new FileReader(scriptFile);
            return engine.eval(reader, bindings);
        } finally {
        	if (reader != null) {
        		try {
        			reader.close();
        		} catch (Throwable t) {
        			logger.error("Close script file reader failed.", t);
        		}
        	}
        }
	}

}
