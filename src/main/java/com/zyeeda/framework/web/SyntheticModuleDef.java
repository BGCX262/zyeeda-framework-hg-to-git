package com.zyeeda.framework.web;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.util.CollectionUtils;
import org.apache.tapestry5.ioc.def.ContributionDef;
import org.apache.tapestry5.ioc.def.DecoratorDef;
import org.apache.tapestry5.ioc.def.ModuleDef;
import org.apache.tapestry5.ioc.def.ServiceDef;

/**
 * This class is copied from tapestry-core.
 */
public class SyntheticModuleDef implements ModuleDef {

	private Set<ContributionDef> defs;
	
	public SyntheticModuleDef(ContributionDef... defs) {
		this.defs = CollectionUtils.asSet(defs);
	}

	@SuppressWarnings("rawtypes")
	@Override
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
		return "SyntheticModule";
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
