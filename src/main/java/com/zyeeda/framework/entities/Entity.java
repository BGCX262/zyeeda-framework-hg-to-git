package com.zyeeda.framework.entities;

import java.io.Serializable;

public class Entity implements Serializable {

	private static final long serialVersionUID = 6570499338336870036L;

	private String id;
	
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
}
