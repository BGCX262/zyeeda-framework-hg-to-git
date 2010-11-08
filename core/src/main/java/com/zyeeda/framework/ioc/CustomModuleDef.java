package com.zyeeda.framework.ioc;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.util.CollectionUtils;
import org.apache.tapestry5.ioc.def.ContributionDef;
import org.apache.tapestry5.ioc.def.DecoratorDef;
import org.apache.tapestry5.ioc.def.ModuleDef;
import org.apache.tapestry5.ioc.def.ServiceDef;

public class CustomModuleDef implements ModuleDef {

	private Set<ContributionDef> defs;
	
	public CustomModuleDef(ContributionDef... defs) {
		this.defs = CollectionUtils.asSet(defs);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class getBuilderClass() {
		return null;
	}

	@Override
	public Set<ContributionDef> getContributionDefs() {
		return this.defs;
	}

	@Override
	public Set<DecoratorDef> getDecoratorDefs() {
		return new HashSet<DecoratorDef>();
	}

	@Override
	public String getLoggerName() {
		return "CustomModule";
	}

	@Override
	public ServiceDef getServiceDef(String serviceId) {
		return null;
	}

	@Override
	public Set<String> getServiceIds() {
		return new HashSet<String>();
	}

}
