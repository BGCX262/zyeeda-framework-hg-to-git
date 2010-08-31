package com.zyeeda.framework.web;

import javax.servlet.ServletContext;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ModuleBuilderSource;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceResources;
import org.apache.tapestry5.ioc.def.ContributionDef;

/**
 * This class is copied from tapestry-core.
 */
public class SyntheticSymbolSourceContributionDef implements ContributionDef {

	private ServletContext context;
	
	public SyntheticSymbolSourceContributionDef(ServletContext context) {
		this.context = context;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void contribute(ModuleBuilderSource moduleSource,
			ServiceResources resources, Configuration configuration) {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void contribute(ModuleBuilderSource moduleSource, ServiceResources resources,
			OrderedConfiguration configuration) {
		
		configuration.add("ServletContext", new ServletContextSymbolProvider(context), "before:ApplicationDefaults");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void contribute(ModuleBuilderSource moduleSource,
			ServiceResources resources,
			MappedConfiguration configuration) {
	}

	@Override
	public String getServiceId() {
		return "SymbolSource";
	}
	
}
