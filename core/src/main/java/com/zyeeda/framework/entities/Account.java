package com.zyeeda.framework.entities;

import java.io.Serializable;

public class Account implements Serializable {

	private static final long serialVersionUID = -7523580183398096125L;
	
	private User user;

	private String uid;
	
	private String systemName;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	
}
