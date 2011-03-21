package com.zyeeda.drivebox.managers.internal;

import java.io.Serializable;

import org.hibernate.ejb.HibernateEntityManagerFactory;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.MetadataUtil;
import com.googlecode.genericdao.search.jpa.JPASearchProcessor;
import com.googlecode.genericdao.search.jpa.hibernate.HibernateMetadataUtil;
import com.zyeeda.framework.persistence.PersistenceService;

public class DefaultManager<T, ID extends Serializable> extends GenericDAOImpl<T, ID> {

	public DefaultManager(PersistenceService persistenceSvc) {
		System.out.println(persistenceSvc.getCurrentSession() + "************");
		this.setEntityManager(persistenceSvc.getCurrentSession());
		
		MetadataUtil util = HibernateMetadataUtil.getInstanceForEntityManagerFactory(
				(HibernateEntityManagerFactory) (persistenceSvc.getSessionFactory()));
		JPASearchProcessor processor = new JPASearchProcessor(util);
		this.setSearchProcessor(processor);
	}
}
