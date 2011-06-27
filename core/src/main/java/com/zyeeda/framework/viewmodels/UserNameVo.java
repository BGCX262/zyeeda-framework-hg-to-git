package com.zyeeda.framework.viewmodels;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "userName")
public class UserNameVo {
	
	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
