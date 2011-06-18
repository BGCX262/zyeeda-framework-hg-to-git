package com.zyeeda.framework.managers;

import java.util.List;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.zyeeda.framework.entities.Role;

public interface RoleManager extends GenericDAO<Role, String> {

		public Role getRoleById(String hql);
		
		public  List<Role> getRoleBySubject(String subject);
}
