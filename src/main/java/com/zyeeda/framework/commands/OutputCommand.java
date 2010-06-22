package com.zyeeda.framework.commands;

import java.util.Map;

public class OutputCommand implements Command {

	private Command innerCmd;
	private Object outputValue;
	
	public OutputCommand(Command innerCmd) {
		this.innerCmd = innerCmd;
	}
	
	@Override
	public String getScope() {
		return this.innerCmd.getScope();
	}
	
	@Override
	public String getOperation() {
		return this.innerCmd.getOperation();
	}
	
	@Override
	public String getExecutor() {
		return this.innerCmd.getExecutor();
	}
	
	@Override
	public String getId() {
		return this.innerCmd.getId();
	}

	@Override
	public String getParameter(String name) {
		return this.innerCmd.getParameter(name);
	}
	
	@Override
	public String[] getParameterValues(String name) {
		return this.innerCmd.getParameterValues(name);
	}

	@Override
	public Map<String, Object> getParameterMap() {
		return this.innerCmd.getParameterMap();
	}

	public void setOutputValue(Object outputValue) {
		this.outputValue = outputValue;
	}
	
	public Object getOutputValue() {
		return this.outputValue;
	}
}
