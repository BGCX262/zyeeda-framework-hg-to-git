package com.zyeeda.framework.managers;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.persistence.internal.JpaPersistenceServiceProvider;
import com.zyeeda.framework.security.SecurityService;

public class RoleManager extends DomainEntityManager {
	
	private SecurityService<?> securityService;
	
	public RoleManager(SecurityService<?> securityService) {
		this.securityService = securityService;
	}

	public List<Role> getRolesBySubject(String subject) throws Throwable {
		PersistenceService<EntityManager> persistenceSvc = this.getPersistenceService();
		EntityManager em = persistenceSvc.openSession();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			List<Role> roles = em.createNamedQuery("getRolesBySubject", Role.class)
				.setParameter("subject", subject).getResultList();
			tx.commit();
			
			return roles;
		} catch (Throwable t) {
			tx.rollback();
			throw t;
		}
	}
	
	private PersistenceService<EntityManager> getPersistenceService() {
		return this.securityService.getServer().getService(JpaPersistenceServiceProvider.class);
	}
	
}
