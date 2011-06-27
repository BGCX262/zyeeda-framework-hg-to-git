package com.zyeeda.framework.managers.internal;
import java.util.ArrayList;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.managers.base.DomainEntityManager;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.viewmodels.RoleVo;
import com.zyeeda.framework.viewmodels.UserVo;

public class DefaultRoleManager extends DomainEntityManager<Role, String>
		implements RoleManager {
	private static final Logger logger = LoggerFactory.getLogger(DefaultRoleManager.class);

	public DefaultRoleManager(PersistenceService persistenceSvc) {
		super(persistenceSvc);
	}
	
	public Set<String> getListAuth(List<Role> roles) {
		Set<String> auths = new HashSet<String>();
		for(Role role : roles) {
			auths.addAll(role.getPermissionList());
		}
		return auths;
	}

	public  List<Role> getRoleBySubject(String subject){
		logger.debug("the value of the dept subject is = {}  ", subject);
		//EntityManager session = (EntityManager)this.getPersistenceService().getCurrentSession();
		TypedQuery<Role> query = this.em().createNamedQuery("getRolesBySubject", Role.class);
		query.setParameter("subject", subject);
		List<Role> roleList = query.getResultList();
		return roleList;
	}

	
	@SuppressWarnings("unchecked")
	public List<Role> getRoleDistinct(String hql){
		List<Role> list = new ArrayList<Role>();
		TypedQuery<Role> createNativeQuery = (TypedQuery<Role>) this.em().createNativeQuery(hql);
		TypedQuery<Role> query = createNativeQuery;
		 list = query.getResultList();
		return list;
	}
	
	public List<RoleVo> roleToVo(List<Role> listRole) {
		List<RoleVo> listRoleVo = new ArrayList<RoleVo>();
		for(Role role : listRole) {
			RoleVo roleVo = new RoleVo();
			if(role.getDeptepment() == null && role.getDeptepmentId() == null){
				roleVo.setCheckName(role.getName());
				roleVo.setLabel(role.getName());
				roleVo.setId(role.getId());
				roleVo.setLeaf(true);
				roleVo.setType("tesk");
				roleVo.setKind("user");
			} else {
				roleVo.setId(role.getDeptepmentId());
				roleVo.setCheckName(role.getDeptepment());
				roleVo.setLabel(role.getDeptepment());
				roleVo.setIo("/rest/line_location/"+role.getDeptepmentId());
				roleVo.setLeaf(false);
				roleVo.setType("io");
			}
			listRoleVo.add(roleVo);
		}
		return listRoleVo;
	}

	
	@SuppressWarnings("unchecked")
	public List<Role> getRoleDistinct(String hql){
		List<Role> list = new ArrayList<Role>();
		TypedQuery<Role> createNativeQuery = (TypedQuery<Role>) this.em().createNativeQuery(hql);
		TypedQuery<Role> query = createNativeQuery;
		 list = query.getResultList();
		return list;
	}
	
	public List<RoleVo> deptToVo(List<Role> listRole) {
		List<RoleVo> listRoleVo = new ArrayList<RoleVo>();
		for(Role role : listRole) {
			RoleVo roleVo = new RoleVo();
				roleVo.setId(role.getDeptepmentId());
				roleVo.setCheckName(role.getDeptepment());
				roleVo.setLabel(role.getDeptepment());
				roleVo.setIo("/rest/dept/"+role.getDeptepmentId());
				roleVo.setLeaf(false);
				roleVo.setKind("role");
				roleVo.setType("io");
				listRoleVo.add(roleVo);
		}
		return listRoleVo;
	}

	public List<RoleVo> roleToVo(List<Role> listRole) {
		List<RoleVo> listRoleVo = new ArrayList<RoleVo>();
		for(Role role : listRole) {
			RoleVo roleVo = new RoleVo();
				roleVo.setCheckName(role.getName());
				roleVo.setLabel(role.getName());
				roleVo.setId(role.getId());
				roleVo.setLeaf(false);
				roleVo.setType("io");
				roleVo.setKind("role");
				roleVo.setIo("/rest/roles/depts/" + role.getId());
				listRoleVo.add(roleVo);
		}
		return listRoleVo;
	}
	
	public List<UserVo> getUserVoByRole(Role role) {
		Set<String> user = role.getSubjects();
		List<UserVo>  list = new ArrayList<UserVo>();
		for(String userName:user) {
			UserVo userVo = new UserVo();
			userVo.setCheckName(userName);
			userVo.setLabel(userName);
			userVo.setType("node");
			userVo.setId(userName);
			userVo.setKind("user");
			userVo.setLeaf(true);
			list.add(userVo);
		}
		return list;
	}
}
