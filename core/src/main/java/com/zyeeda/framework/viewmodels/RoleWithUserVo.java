package com.zyeeda.framework.viewmodels;

import java.util.ArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.zyeeda.framework.viewmodels.PermissionVo;


@XmlRootElement(name = "roleWithUserVo")
public class RoleWithUserVo {
	private List<UserNameVo> userName = new ArrayList<UserNameVo>();
	
	private List<PermissionVo> permission = new ArrayList<PermissionVo>();
	
	public List<PermissionVo> getPermission() {
		return permission;
	}

	public void setPermission(List<PermissionVo> permission) {
		this.permission = permission;
	}

	public List<UserNameVo> getUserName() {
		return userName;
	}

	public void setUserName(List<UserNameVo> userName) {
		this.userName = userName;
	}


	

}
