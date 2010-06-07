package com.zyeeda.framework.entities;

public class BaseEntity implements Entity {

	private String id;
	
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return this.id;
	}

}
