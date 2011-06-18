package com.zyeeda.framework.nosql.internal;

import org.apache.commons.configuration.Configuration;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.Mongo;
import com.zyeeda.framework.config.ConfigurationService;
import com.zyeeda.framework.nosql.MongoDbService;
import com.zyeeda.framework.service.AbstractService;

@Marker(Primary.class)
@ServiceId("default-mongodb-service")
public class DefaultMongoDbServiceProvider extends AbstractService implements
		MongoDbService {
	
	private final static Logger logger = LoggerFactory.getLogger(DefaultMongoDbServiceProvider.class);
	
	private final static String HOST_KEY = "host";
	private final static String PORT_KEY = "port";
	private final static String SYSTEM_USERNAME_KEY = "systemUsername";
	private final static String SYSTEM_PASSWORD_KEY = "systemPassword";
	private final static String DEFAULT_DATABASE_NAME_KEY = "defaultDatabaseName";
	
	private final static String DEFAULT_DEFAULT_DATABASE_NAME = "zyeeda-framework";
	
	private ConfigurationService configSvc;
	
	private String host;
	private int port;
	private String systemUsername;
	private String defaultDatabaseName;
	
	private Configuration config;
	private Mongo mongo;

	public DefaultMongoDbServiceProvider(RegistryShutdownHub shutdownHub,
			@Primary ConfigurationService configSvc) {
		super(shutdownHub);
		
		this.configSvc = configSvc;
		
		Configuration config = this.getConfiguration(this.configSvc);
		this.init(config);
	}
	
	private void init(Configuration config) {
		this.config = config;
		
		this.host = config.getString(HOST_KEY, DBAddress.defaultHost());
		this.port = config.getInt(PORT_KEY, DBAddress.defaultPort());
		this.systemUsername = config.getString(SYSTEM_USERNAME_KEY);
		this.defaultDatabaseName = config.getString(DEFAULT_DATABASE_NAME_KEY, DEFAULT_DEFAULT_DATABASE_NAME);
		
		if (logger.isDebugEnabled()) {
			logger.debug("{} = {}", HOST_KEY, this.host);
			logger.debug("{} = {}", PORT_KEY, this.port);
			logger.debug("{} = {}", SYSTEM_USERNAME_KEY, this.systemUsername);
			logger.debug("{} = {}", SYSTEM_PASSWORD_KEY, "******");
			logger.debug("{} = {}", DEFAULT_DATABASE_NAME_KEY, this.defaultDatabaseName);
		}
	}
	
	@Override
	public void start() throws Exception {
		this.mongo = new Mongo(this.host, this.port);
		DB adminDb = this.mongo.getDB("admin");
		String systemPassword = this.config.getString(SYSTEM_PASSWORD_KEY);
		if (this.systemUsername != null && systemPassword != null) {
			adminDb.authenticate(this.systemUsername, systemPassword.toCharArray());
		}
	}
	
	@Override
	public void stop() {
		this.mongo.close();
	}

	@Override
	public Mongo getMongo() {
		return this.mongo;
	}

	@Override
	public DB getDefaultDatabase() {
		return this.getDatabase(this.defaultDatabaseName);
	}

	@Override
	public DB getDatabase(String name) {
		return this.mongo.getDB(name);
	}

}
