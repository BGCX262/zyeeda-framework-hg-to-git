package com.zyeeda.framework.managers;

import java.util.List;

import javax.persistence.EntityManager;

import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.persistence.PersistenceService;

public class RoleManager extends DomainEntityManager<Role, String> {
	
	public RoleManager(PersistenceService persistenceSvc) {
		super(persistenceSvc);
	}

	public List<Role> getRolesBySubject(String subject) {
		EntityManager session = this.getPersistenceService().getCurrentSession();
		List<Role> roles = session.createNamedQuery("getRolesBySubject", Role.class)
			.setParameter("subject", subject).getResultList();
		
		return roles;
	}
	
}
