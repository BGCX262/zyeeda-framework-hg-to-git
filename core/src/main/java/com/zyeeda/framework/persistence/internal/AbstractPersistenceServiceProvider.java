package com.zyeeda.framework.persistence.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.drools.core.util.StringUtils;
import org.hibernate.ejb.Ejb3Configuration;
import org.slf4j.Logger;

import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.service.AbstractService;

public class AbstractPersistenceServiceProvider extends AbstractService implements PersistenceService {
	
	private EntityManagerFactory sessionFactory;
	private final ThreadLocal<EntityManager> sessionThreadLocal = new ThreadLocal<EntityManager>();
	//private final ThreadLocal<Integer> countThreadLocal = new ThreadLocal<Integer>();
	
	protected AbstractPersistenceServiceProvider(Logger logger,	RegistryShutdownHub shutdownHub) {
		super(logger, shutdownHub);
	}

	protected void addMappingClasses(Ejb3Configuration config, String key) throws IOException {
		
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
			LoggerHelper.debug(this.getLogger(), "context class loader is null, use current class loader instead");
			cl = this.getClass().getClassLoader();
		}
		
		Enumeration<URL> urls = cl.getResources("META-INF/MANIFEST.MF");
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			
			InputStream is = null;
			try {
				is = url.openStream();
				Manifest mf = new Manifest(is);
				String classes = mf.getMainAttributes().getValue(key);
				if (classes == null) {
					continue;
				}
				
				String[] classArray = StringUtils.split(classes, ',');
				for (String className : classArray) {
					try {
						Class<?> clazz = cl.loadClass(className);
						config.addAnnotatedClass(clazz);
					} catch (ClassNotFoundException e) {
						LoggerHelper.warn(this.getLogger(), e.getMessage(), e);
					}
				}
			} finally {
				if (is != null) {
					is.close();
				}
			}
		}
	}
	
	@Override
	public void stop() throws Exception {
        this.sessionFactory.close();
        this.sessionFactory = null;
	}
	
	@Override
	public EntityManager openSession() {
		EntityManager session = this.sessionThreadLocal.get();
        if (session == null) {
            session = this.getSessionFactory().createEntityManager();
            LoggerHelper.info(this.getLogger(), "Open session on thread [{}].", Thread.currentThread().getName());
            this.sessionThreadLocal.set(session);
        }

        return session;
	}

	@Override
    public void closeSession() {
		EntityManager session = this.sessionThreadLocal.get();
        if (session == null) {
        	LoggerHelper.warn(this.getLogger(), "No session opened.");
        	return;
        }
        
        session.close();
        this.sessionThreadLocal.remove();
        LoggerHelper.info(this.getLogger(), "Close session on thread [{}].", Thread.currentThread().getName());
    }
	
	@Override
	public EntityManager getCurrentSession() {
		return this.sessionThreadLocal.get();
	}
	
	protected void setSessionFactory(EntityManagerFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public EntityManagerFactory getSessionFactory() {
		return this.sessionFactory;
	}
	
	/*@Override
    public Session openSession() {
    	Integer count = countThreadLocal.get();
    	if (count == null) {
    		count = 0;
    	}
    	countThreadLocal.set(++count);
    	
        Session session = sessionThreadLocal.get();
        if (session == null) {
            session = this.sessionFactory.openSession();
            sessionThreadLocal.set(session);
        }

        return session;
    }*/

	/*@Override
    public void closeSession() {
    	Integer count = countThreadLocal.get();
    	if (count == null || count == 0) {
    		LoggerHelper.trace(logger, "会话未开启");
    		return;
    	}
    	countThreadLocal.set(--count);
    	if (count > 0) {
    		LoggerHelper.trace(logger, "会话未关闭");
    		return;
    	}
    	this.realCloseSession();
    }*/

    /*private void realCloseSession() {
        Session session = sessionThreadLocal.get();
        sessionThreadLocal.remove();
        countThreadLocal.remove();

        if (session != null) {
            session.close();
        }

        LoggerHelper.debug(logger, "会话已关闭");
    }*/
	
}
