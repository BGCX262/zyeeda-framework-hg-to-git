package com.zyeeda.framework.managers;

import java.util.List;

import javax.naming.NamingException;

import com.zyeeda.framework.entities.Department;
import com.zyeeda.framework.viewmodels.DepartmentVo;

public interface DepartmentManager {

	public DepartmentVo persist(Department dept) throws NamingException;
	
	public Department findById(String id) throws NamingException;
	
	public void remove(String id) throws NamingException;
	
	public Department update(Department dept) throws NamingException;
	
	public List<DepartmentVo> getDepartmentListById(String id) throws NamingException;
	
	public List<DepartmentVo> getDepartmentListById(String id, String type) throws NamingException;
	
	public List<DepartmentVo> getDepartmentListByName(String name) throws NamingException;
	
}
