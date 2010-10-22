package com.zyeeda.framework.managers;

import java.util.List;

import javax.persistence.EntityManager;

import com.zyeeda.framework.entities.Role;

public class RoleManager extends DomainEntityManager {
	
	public RoleManager(EntityManager session) {
		super(session);
	}

	public List<Role> getRolesBySubject(String subject) {
		List<Role> roles = this.getSession().createNamedQuery("getRolesBySubject", Role.class)
			.setParameter("subject", subject).getResultList();
		
		return roles;
	}
	
}
