package com.zyeeda.framework.entities;

import java.io.Serializable;

public class Department implements Serializable {

	private static final long serialVersionUID = 8606771207286469030L;
	
	private String parent;
	
	private String name;
	
	private String description;
	
	private String fax;
	
	private String telephoneNumber;

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

}
