package com.zyeeda.framework.knowledge.internal;

import java.io.File;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.configuration.Configuration;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.process.audit.JPAWorkingMemoryDbLogger;
import org.drools.process.workitem.wsht.WSHumanTaskHandler;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.task.service.TaskServer;
import org.drools.task.service.TaskService;
import org.drools.task.service.mina.MinaTaskServer;
import org.slf4j.Logger;

import com.zyeeda.framework.config.ConfigurationService;
import com.zyeeda.framework.helpers.LoggerHelper;
import com.zyeeda.framework.ioc.annotations.DroolsTask;
import com.zyeeda.framework.knowledge.KnowledgeService;
import com.zyeeda.framework.knowledge.StatefulSessionCommand;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.service.AbstractService;
import com.zyeeda.framework.transaction.TransactionService;

@ServiceId("drools-knowledge-service-provider")
@Marker(Primary.class)
public class DroolsKnowledgeServiceProvider extends AbstractService implements KnowledgeService {
	
	private static final String AUDIT_LOG_FILE_PATH = "auditLogFilePath";
	private static final String AUDIT_LOG_FLUSH_INTERVAL = "auditLogFlushInterval";
	
	private static final String DEFAUL_AUDIT_LOG_FILE_PATH = "/WEB-INF/logs/drools_audit";
	private static final int DEFAULT_AUDIT_LOG_FLUSH_INTERVAL = 60 * 60 * 1000;
	
	private final ConfigurationService configSvc;
	private final PersistenceService defaultPersistenceSvc;
	private final PersistenceService droolsTaskPersistenceSvc;
	private final TransactionService txSvc;
	
	private KnowledgeBase kbase;
	private TaskService taskService;
	private TaskServer taskServer;
	
	private String auditLogFilePath;
	private int auditLogFlushInterval;	
	
	public DroolsKnowledgeServiceProvider(
			@Primary ConfigurationService configSvc,
			@DroolsTask PersistenceService droolsTaskPersistenceSvc,
			@Primary PersistenceService defaultPersistenceSvc,
			@Primary TransactionService txSvc,
			Logger logger, RegistryShutdownHub shutdownHub) throws Exception {
		
		super(logger, shutdownHub);
		
		this.configSvc = configSvc;
		this.defaultPersistenceSvc = defaultPersistenceSvc;
		this.droolsTaskPersistenceSvc = droolsTaskPersistenceSvc;
		this.txSvc = txSvc;
		
		Configuration config = this.getConfiguration(configSvc);
		this.init(config);
	}
	
	private void init(Configuration config) throws Exception {
		this.auditLogFilePath = config.getString(AUDIT_LOG_FILE_PATH, DEFAUL_AUDIT_LOG_FILE_PATH);
		this.auditLogFlushInterval = config.getInt(AUDIT_LOG_FLUSH_INTERVAL, DEFAULT_AUDIT_LOG_FLUSH_INTERVAL);
		
		if (this.getLogger().isDebugEnabled()) {
			this.getLogger().debug("{} = {}", AUDIT_LOG_FILE_PATH, this.auditLogFilePath);
			this.getLogger().debug("{} = {}", AUDIT_LOG_FLUSH_INTERVAL, this.auditLogFlushInterval);
		}
		
		File auditLogFile = new File(this.configSvc.getApplicationRoot(), this.auditLogFilePath);
		if (!auditLogFile.exists()) {
			File logDir = auditLogFile.getParentFile();
			if (!logDir.exists()) {
				LoggerHelper.info(this.getLogger(), "Audit log file container {} does not exist, make dir first.", logDir);
				logDir.mkdirs();
			} else {
				if (!logDir.isDirectory()) {
					LoggerHelper.warn(this.getLogger(), 
							"Audit log file container {} should be a directory but a file found instead, delete the file then make dir.", logDir);
					logDir.delete();
					logDir.mkdirs();
				}
			}
		}
	}
	
	@Override
	public void start() throws Exception {
		// add knowledge changeset
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("META-INF/drools-changeset.xml"), ResourceType.CHANGE_SET);
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			if (this.getLogger().isErrorEnabled()) {
				for (KnowledgeBuilderError error: errors) {
					this.getLogger().error(error.toString());
				}
			}
			
			throw new IllegalArgumentException("Could not parse knowledge changeset.");
		}

		this.kbase = kbuilder.newKnowledgeBase();
		this.kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		
		// start task server
		EntityManagerFactory emf = this.droolsTaskPersistenceSvc.getSessionFactory();
		this.taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
		this.taskServer = new MinaTaskServer(this.taskService);
		Thread taskServerThread = new Thread(this.taskServer, "DroolsTaskServer");
		taskServerThread.start();
	}
	
	@Override
	public void stop() throws Exception {
		if (this.taskServer != null) {
			this.taskServer.stop();
		}
	}

	@Override
	public KnowledgeBase getKnowledgeBase() {
		return this.kbase;
	}
	
	@Override
	public TaskService getTaskService() {
		return this.taskService;
	}
	
	@Override
	public <T> T execute(StatefulSessionCommand<T> command) throws Exception {
		StatefulKnowledgeSession ksession = null;
		KnowledgeRuntimeLogger rtLogger = null;
		JPAWorkingMemoryDbLogger dbLogger = null;

		try {
			Environment env = KnowledgeBaseFactory.newEnvironment();
			env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, this.defaultPersistenceSvc.getSessionFactory());
			env.set(EnvironmentName.TRANSACTION, this.txSvc.getTransaction());
			
			if (command.getSessionId() > 0) {
				ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(
						command.getSessionId(), this.getKnowledgeBase(), null, env);
			} else {
				ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
			}
			
			ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new WSHumanTaskHandler());
			
			rtLogger = KnowledgeRuntimeLoggerFactory.newThreadedFileLogger(ksession, this.configSvc.mapPath(this.auditLogFilePath), this.auditLogFlushInterval);
			dbLogger = new JPAWorkingMemoryDbLogger(ksession);
			
			return command.execute(ksession);
		} finally {
			if (rtLogger != null) {
				rtLogger.close();
			}
			if (dbLogger != null) {
				dbLogger.dispose();
			}
			if (ksession != null) {
				ksession.dispose();
			}
		}
	}

}
