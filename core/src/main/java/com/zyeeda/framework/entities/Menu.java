package com.zyeeda.framework.entities;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "menuVo")
public class Menu {
	private String id;
	private String name;
	private String auth;
	private Set<Menu> permissionSet = new HashSet<Menu>();
	private Menu parentMenu;

	public Menu getParentMenu() {
		return parentMenu;
	}

	public void setParentMenu(Menu parentMenu) {
		this.parentMenu = parentMenu;
	}


	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<Menu> getPermissionSet() {
		return permissionSet;
	}

	public void setPermissionSet(Set<Menu> permissionSet) {
		this.permissionSet = permissionSet;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
