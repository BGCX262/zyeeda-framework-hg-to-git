package com.zyeeda.framework.managers;

import javax.naming.NamingException;

import com.zyeeda.framework.entities.Department;

public interface DepartmentManager {

	public void persist(Department dept) throws NamingException;
	
	public Department find(String dn) throws NamingException;
	
}
