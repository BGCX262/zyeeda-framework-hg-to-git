package com.zyeeda.framework.web;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.FrameworkConstants;

/**
 * This class is copied from tapestry-core.
 */
public class ServletContextSymbolProvider implements SymbolProvider {
	
	private final static Logger logger = LoggerFactory.getLogger(ContextListener.class);

	private final Map<String, String> properties = CollectionFactory.newCaseInsensitiveMap();

    public ServletContextSymbolProvider(ServletContext context)
    {
    	String contextRoot = context.getRealPath("/");
    	properties.put(FrameworkConstants.APPLICATION_ROOT, contextRoot);
    	
        for (String name : InternalUtils.toList(context.getInitParameterNames()))
        {
            properties.put(name, context.getInitParameter(name));
        }
        
        if (logger.isDebugEnabled()) {
        	for (Iterator<Entry<String, String>> it = properties.entrySet().iterator(); it.hasNext(); ) {
        		Entry<String, String> entry = it.next();
        		logger.debug(entry.getKey() + " = " + entry.getValue());
        	}
        }
    }

    @Override
    public String valueForSymbol(String symbolName)
    {
        return properties.get(symbolName);
    }

}
