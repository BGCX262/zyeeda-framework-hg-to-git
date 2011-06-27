package com.zyeeda.framework.vos;

import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import com.zyeeda.framework.entities.Role;

@XmlRootElement(name = "roles")
public class Roles {

	private Collection<Role> roles;
	
	public void setRole(Collection<Role> roles) {
		this.roles = roles;
	}
	
	public Collection<Role> getRole() {
		return this.roles;
	}
	
}
