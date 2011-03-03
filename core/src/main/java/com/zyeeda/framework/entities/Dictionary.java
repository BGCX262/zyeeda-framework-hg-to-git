package com.zyeeda.framework.entities;

@javax.persistence.Entity
@javax.persistence.Table(name = "ZYD_SYS_DICT")
@javax.persistence.Inheritance
@javax.persistence.DiscriminatorColumn(name = "F_DICT_TYPE")
public class Dictionary extends SimpleDomainEntity {

	private String value;
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}

}
