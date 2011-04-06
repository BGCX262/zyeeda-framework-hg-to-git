package com.zyeeda.framework.managers.base;

import java.io.Serializable;

import org.hibernate.ejb.HibernateEntityManagerFactory;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.MetadataUtil;
import com.googlecode.genericdao.search.jpa.JPASearchProcessor;
import com.googlecode.genericdao.search.jpa.hibernate.HibernateMetadataUtil;

import com.zyeeda.framework.persistence.PersistenceService;

public class DomainEntityManager<T, ID extends Serializable> extends GenericDAOImpl<T, ID> {

	private PersistenceService persistenceSvc;
	
	public DomainEntityManager(PersistenceService persistenceSvc) {
		this.persistenceSvc = persistenceSvc;
		
		this.setEntityManager(this.persistenceSvc.getCurrentSession());
		
		HibernateEntityManagerFactory emf = (HibernateEntityManagerFactory) this.persistenceSvc.getSessionFactory();
		MetadataUtil util = HibernateMetadataUtil.getInstanceForEntityManagerFactory(emf);
		JPASearchProcessor processor = new JPASearchProcessor(util);
		this.setSearchProcessor(processor);
	}
	
	protected PersistenceService getPersistenceService() {
		return this.persistenceSvc;
	}
	
}
