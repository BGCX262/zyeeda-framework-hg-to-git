package com.zyeeda.framework.managers;

import java.util.List;

import javax.naming.NamingException;

import com.zyeeda.framework.entities.Department;

public interface DepartmentManager {

	public void persist(Department dept) throws NamingException;
	
	public Department findById(String id) throws NamingException;
	
	public void remove(String id) throws NamingException;
	
	public void update(Department dept) throws NamingException;
	
	public List<Department> getDepartmentListById(String id) throws NamingException;
	
	public List<Department> getDepartmentListByName(String name) throws NamingException;
}
