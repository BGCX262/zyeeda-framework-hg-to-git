package com.zyeeda.framework.server;

public class ServiceNameDuplicateException extends Exception {

	private static final long serialVersionUID = -9200882461532592204L;
	
	private String serviceName;
	
	public ServiceNameDuplicateException(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public String getServiceName() {
		return this.serviceName;
	}

}
