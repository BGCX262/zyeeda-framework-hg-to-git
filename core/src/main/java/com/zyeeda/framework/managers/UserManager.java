package com.zyeeda.framework.managers;

import java.util.List;

import javax.naming.NamingException;

import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.viewmodels.UserVo;

public interface UserManager {

	public UserVo persist(User user) throws NamingException;
	
	public UserVo findById(String id) throws NamingException;
	
	public void remove(String id) throws NamingException;
	
	public UserVo update(User user) throws NamingException;
	
	public List<UserVo> getUserListByDepartmentId(String id) throws NamingException;
	
	public List<UserVo> getUserListByName(String name) throws NamingException;
	
}
