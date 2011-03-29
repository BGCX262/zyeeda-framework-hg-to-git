package com.zyeeda.framework.persistence.internal;

import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.hibernate.ejb.Ejb3Configuration;

import com.zyeeda.framework.ioc.annotations.AttachmentPersistence;
import com.zyeeda.framework.persistence.PersistenceService;

@ServiceId("attachment-persistence-service")
@Marker(AttachmentPersistence.class)
public class AttachmentPersistenceServiceProvider extends
		AbstractPersistenceServiceProvider implements PersistenceService {

	private final static String PERSISTENCE_UNIT_NAME = "attachment";
	
	public AttachmentPersistenceServiceProvider(
			RegistryShutdownHub shutdownHub) {
		super(shutdownHub);
	}
	
	@Override
	public void start() throws Exception {
		Ejb3Configuration config = new Ejb3Configuration().configure(PERSISTENCE_UNIT_NAME, null);
		this.setSessionFactory(config.buildEntityManagerFactory());
	}

}
