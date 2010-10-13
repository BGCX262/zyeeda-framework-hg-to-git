package com.zyeeda.framework.config.internal;

import java.io.File;
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
import org.slf4j.Logger;

import com.zyeeda.framework.config.ConfigurationService;
import com.zyeeda.framework.service.AbstractService;

@ServiceId("DefaultConfigurationServiceProvider")
@Marker(Primary.class)
public class DefaultConfigurationServiceProvider extends AbstractService implements ConfigurationService {
	
	private ServletContext context;
	
	public DefaultConfigurationServiceProvider(
			Collection<ServletContext> contexts, Logger logger, RegistryShutdownHub shutdownHub) {
		
		super(logger, shutdownHub);
		
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
		
		Configuration config = null;
		try {
            if (resource.getFile().endsWith(".xml")) {
                config = new XMLConfiguration(resource.toURL());
            } else if (resource.getFile().endsWith(".properties")) {
            	config = new PropertiesConfiguration(resource.toURL());
            } else {
                throw new RuntimeException(String.format("Unsupported configuration file type. [%s]", resource.toString()));
            }
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }

        return config;
	}

	@Override
	public File getApplicationRoot() {
		String contextRoot = this.context.getRealPath("/");
		return new File(contextRoot);
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
