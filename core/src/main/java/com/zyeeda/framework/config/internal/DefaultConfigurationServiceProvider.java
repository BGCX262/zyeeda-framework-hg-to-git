package com.zyeeda.framework.config.internal;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletContext;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;

import com.zyeeda.framework.config.ConfigurationService;
import com.zyeeda.framework.service.AbstractService;

@ServiceId("default-configuration-service")
@Marker(Primary.class)
public class DefaultConfigurationServiceProvider extends AbstractService implements ConfigurationService {
	
	private ServletContext context;
	
	public DefaultConfigurationServiceProvider(
			Collection<ServletContext> contexts, RegistryShutdownHub shutdownHub) {
		
		super(shutdownHub);
		
		if (contexts.size() != 1) {
			throw new IllegalStateException("There should be one and only one context.");
		}
		this.context = contexts.iterator().next();
	}

	@Override
	public Configuration getConfiguration(Resource resource) {
		if (resource == null) {
			throw new IllegalArgumentException("Argument [resource] should not be null.");
		}
		
		if (!resource.exists()) {
			throw new RuntimeException(String.format("Resource [%s] not found.", resource.toString()));
		}
		
		try {
            if (resource.getFile().endsWith(".xml")) {
            	XMLConfiguration xmlConfig = new XMLConfiguration();
            	xmlConfig.load(resource.openStream(), "UTF-8");
            	return xmlConfig;
            }
            
            if (resource.getFile().endsWith(".properties")) {
            	PropertiesConfiguration propConfig = new PropertiesConfiguration();
            	propConfig.load(resource.openStream(), "UTF-8");
            	return propConfig;
            }
            
            throw new RuntimeException(String.format("Unsupported configuration file type. [%s]", resource.toString()));
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
        	throw new RuntimeException(e);
		}
	}

	@Override
	public File getApplicationRoot() {
		String contextRoot = this.context.getRealPath("/");
		return new File(contextRoot);
	}
	
	public String getContextPath() {
		return this.context.getContextPath();
	}
	
	@Override
	public String mapPath(String path) {
		File f = new File(this.getApplicationRoot(), path);
		return f.getAbsolutePath();
	}

	@Override
	public String getContextParameter(String name) {
		return this.context.getInitParameter(name);
	}

}
