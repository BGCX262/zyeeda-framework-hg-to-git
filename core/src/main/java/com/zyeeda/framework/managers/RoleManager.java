package com.zyeeda.framework.managers;

import java.util.List;
import java.util.Set;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.viewmodels.RoleVo;
import com.zyeeda.framework.viewmodels.UserVo;

public interface RoleManager extends GenericDAO<Role, String> {

		public  List<Role> getRoleBySubject(String subject);
		
		public List<Role> getRoleDistinct(String hql);
		
		public List<RoleVo> roleToVo(List<Role> listRole);
		
		public List<UserVo> getUserVoByRole(Role role);
		
		public List<RoleVo> deptToVo(List<Role> listRole);
		
		public Set<String> getListMenuAuth(List<Role> roles);
		
}
