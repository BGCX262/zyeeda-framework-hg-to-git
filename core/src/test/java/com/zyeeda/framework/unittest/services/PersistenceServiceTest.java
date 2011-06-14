package com.zyeeda.framework.unittest.services;

import javax.persistence.EntityManager;

import org.testng.annotations.Test;

import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.persistence.internal.DefaultPersistenceServiceProvider;
import com.zyeeda.framework.unittest.TestSuiteBase;
import com.zyeeda.framework.utils.IocUtils;

@Test
public class PersistenceServiceTest extends TestSuiteBase {

	@Test(enabled = false)
	public void testSaveRole() {
		Role r = new Role();
		PersistenceService svc = this.getService();
		EntityManager session = null;
		try {
			session = svc.openSession();
			session.getTransaction().begin();
			session.persist(r);
			session.getTransaction().commit();
		} catch (Throwable t) {
			session.getTransaction().rollback();
			t.printStackTrace();
		} finally {
			svc.closeSession();
		}
	}
	
	private PersistenceService getService() {
		return getRegistry().getService(IocUtils.getServiceId(DefaultPersistenceServiceProvider.class), PersistenceService.class);
	}
}
