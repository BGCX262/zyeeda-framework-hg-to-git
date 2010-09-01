package com.zyeeda.framework.unittest.services;

import org.hibernate.Session;
import org.testng.annotations.Test;

import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.unittest.TestSuiteBase;

@Test
public class PersistenceServiceTest extends TestSuiteBase {

	@Test(enabled = false)
	public void testSaveRole() {
		Role r = new Role();
		PersistenceService svc = this.getService();
		Session session = null;
		try {
			session = svc.openSession();
			session.getTransaction().begin();
			session.save(r);
			session.getTransaction().commit();
		} catch (Throwable t) {
			session.getTransaction().rollback();
			t.printStackTrace();
		} finally {
			svc.closeSession();
		}
	}
	
	private PersistenceService getService() {
		return getRegistry().getService(PersistenceService.class);
	}
}
