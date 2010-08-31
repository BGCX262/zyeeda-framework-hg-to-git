package com.zyeeda.framework.persistence.internal;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;

import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.service.AbstractService;

public class HibernatePersistenceServiceProvider extends AbstractService implements PersistenceService {

	private static final String DEFAULT_PERSISTENCE_UNIT_NAME = "default";
	
	// Injected
	private final Logger logger;
	
	private final ThreadLocal<EntityManager> sessionThreadLocal = new ThreadLocal<EntityManager>();
	private EntityManagerFactory emf;
	
	public HibernatePersistenceServiceProvider(Logger logger) {
		this.logger = logger;
	}
	
	@Override
	public void start() throws Exception {
		this.emf = Persistence.createEntityManagerFactory(DEFAULT_PERSISTENCE_UNIT_NAME);
	}
	
	@Override
	public void stop() {
		this.emf.close();
		this.emf = null;
	}

	@Override
	public EntityManager openSession() {    	
        EntityManager em = this.sessionThreadLocal.get();
        if (em == null) {
            em = this.emf.createEntityManager();
            this.sessionThreadLocal.set(em);
        }

        LoggerHelper.debug(logger, "Open session.");
        return em;
	}

	@Override
    public void closeSession() {
        EntityManager em = this.sessionThreadLocal.get();
        if (em == null) {
        	LoggerHelper.debug(logger, "No session opened.");
        	return;
        }
        
        em.close();
        this.sessionThreadLocal.remove();
        LoggerHelper.debug(logger, "Close session.");
    }

}
