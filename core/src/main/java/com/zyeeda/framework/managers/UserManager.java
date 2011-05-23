package com.zyeeda.framework.managers;

import java.text.ParseException;
import java.util.List;

import javax.naming.NamingException;

import com.zyeeda.framework.entities.User;

public interface UserManager {

	public void persist(User user) throws NamingException;
	
	public void remove(String id) throws NamingException;
	
	public void update(User user) throws NamingException, ParseException;
	
	public User findById(String id) throws NamingException, ParseException;
	
	public List<User> findByDepartmentId(String id) throws NamingException;
	
	public List<User> findByName(String name) throws NamingException;
	
	public void updatePassword(String id, String password) throws NamingException, ParseException;
	
	public void setVisible(Boolean visible, String ...ids) throws NamingException, ParseException;
	
}
