package com.zyeeda.framework.managers;

import java.util.List;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.zyeeda.framework.entities.Role;

public interface RoleManager extends GenericDAO<Role, String> {

		public  List<Role> getRoleBySubject(String subject);
		
		public List<Role> getRoleDistinct(String hql);
}
