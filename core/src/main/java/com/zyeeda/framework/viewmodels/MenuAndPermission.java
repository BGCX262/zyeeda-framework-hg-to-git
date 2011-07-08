package com.zyeeda.framework.viewmodels;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "menuAndPermission")
public class MenuAndPermission {
	
	private List<MenuVo> listMenu = new ArrayList<MenuVo>();
	
	private List<PermissionVo> listPermission = new ArrayList<PermissionVo>();
	
	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<MenuVo> getListMenu() {
		return listMenu;
	}

	public void setListMenu(List<MenuVo> listMenu) {
		this.listMenu = listMenu;
	}

	public List<PermissionVo> getListPermission() {
		return listPermission;
	}

	public void setListPermission(List<PermissionVo> listPermission) {
		this.listPermission = listPermission;
	}
}
