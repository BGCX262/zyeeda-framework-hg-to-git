package com.zyeeda.framework.managers;

import java.util.List;

import javax.persistence.EntityManager;

import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.security.SecurityService;

public class RoleManager extends DomainEntityManager {
	
	private SecurityService<?> securitySvc;
	
	public RoleManager(SecurityService<?> securitySvc) {
		this.securitySvc = securitySvc;
	}

	public List<Role> getRolesBySubject(String subject) {
		PersistenceService persistenceSvc = this.securitySvc.getPersistenceService();
		EntityManager session = persistenceSvc.openSession();
		List<Role> roles = session.createNamedQuery("getRolesBySubject", Role.class)
			.setParameter("subject", subject).getResultList();
		
		return roles;
	}
	
}
