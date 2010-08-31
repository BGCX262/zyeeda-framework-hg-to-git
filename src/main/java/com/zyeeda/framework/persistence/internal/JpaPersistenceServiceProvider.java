package com.zyeeda.framework.persistence.internal;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.server.ApplicationServer;
import com.zyeeda.framework.service.AbstractService;

public class JpaPersistenceServiceProvider extends AbstractService implements PersistenceService<EntityManager> {

	private static final String DEFAULT_PERSISTENCE_UNIT_NAME = "default";
	
	private static final Logger logger = LoggerFactory.getLogger(JpaPersistenceServiceProvider.class);
	
    private static final ThreadLocal<EntityManager> sessionThreadLocal = new ThreadLocal<EntityManager>();
    
	private EntityManagerFactory emf;
	
	public JpaPersistenceServiceProvider(ApplicationServer server, String name) {
		super(server, name);
	}
	
	@Override
	public void start() {
		this.emf = Persistence.createEntityManagerFactory(DEFAULT_PERSISTENCE_UNIT_NAME);
	}
	
	@Override
	public void stop() {
		this.emf.close();
		this.emf = null;
	}

	@Override
	public EntityManager openSession() {    	
        EntityManager em = sessionThreadLocal.get();
        if (em == null) {
            em = this.emf.createEntityManager();
            sessionThreadLocal.set(em);
        }

        LoggerHelper.debug(logger, "Open session.");
        return em;
	}

	@Override
    public void closeSession() {
        EntityManager em = sessionThreadLocal.get();
        if (em == null) {
        	LoggerHelper.debug(logger, "No session opened.");
        	return;
        }
        
        em.close();
        sessionThreadLocal.remove();
        LoggerHelper.debug(logger, "Close session.");
    }

}
