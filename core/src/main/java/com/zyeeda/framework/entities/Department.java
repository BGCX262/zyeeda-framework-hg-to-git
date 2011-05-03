package com.zyeeda.framework.entities;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.zyeeda.framework.entities.base.SimpleDomainEntity;

@XmlRootElement(name  = "department")
public class Department extends SimpleDomainEntity implements Serializable {
	
	private static final long serialVersionUID = 8606771207286469030L;
	
	private String baseDn;
	
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

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
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
