package com.zyeeda.framework.managers;

import java.util.List;

import org.hibernate.Session;

import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.security.SecurityService;

public class RoleManager extends DomainEntityManager {
	
	private SecurityService<?> securitySvc;
	
	public RoleManager(SecurityService<?> securitySvc) {
		this.securitySvc = securitySvc;
	}

	public List<?> getRolesBySubject(String subject) throws Throwable {
		PersistenceService persistenceSvc = this.securitySvc.getPersistenceService();
		Session session = persistenceSvc.openSession();
		try {
			session.getTransaction().begin();
			List<?> roles = session.getNamedQuery("getRolesBySubject")
				.setParameter("subject", subject).list();
			session.getTransaction().commit();
			
			return roles;
		} catch (Throwable t) {
			session.getTransaction().rollback();
			throw t;
		}
	}
	
}
