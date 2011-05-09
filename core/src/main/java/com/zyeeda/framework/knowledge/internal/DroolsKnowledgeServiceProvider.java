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
import org.drools.process.workitem.wsht.WSHumanTaskHandler;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.task.service.TaskServer;
import org.drools.task.service.TaskService;
import org.drools.task.service.mina.MinaTaskServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.config.ConfigurationService;
import com.zyeeda.framework.ioc.annotations.DroolsTaskPersistence;
import com.zyeeda.framework.knowledge.KnowledgeService;
import com.zyeeda.framework.knowledge.StatefulSessionCommand;
import com.zyeeda.framework.persistence.PersistenceService;
import com.zyeeda.framework.service.AbstractService;
import com.zyeeda.framework.transaction.TransactionService;

@ServiceId("drools-knowledge-service")
@Marker(Primary.class)
public class DroolsKnowledgeServiceProvider extends AbstractService implements KnowledgeService {
	
	private static final Logger logger = LoggerFactory.getLogger(DroolsKnowledgeServiceProvider.class);
	
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
			@DroolsTaskPersistence PersistenceService droolsTaskPersistenceSvc,
			@Primary PersistenceService defaultPersistenceSvc,
			@Primary TransactionService txSvc,
			RegistryShutdownHub shutdownHub) throws Exception {
		
		super(shutdownHub);
		
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
		
		logger.debug("{} = {}", AUDIT_LOG_FILE_PATH, this.auditLogFilePath);
		logger.debug("{} = {}", AUDIT_LOG_FLUSH_INTERVAL, this.auditLogFlushInterval);
		
		File auditLogFile = new File(this.configSvc.getApplicationRoot(), this.auditLogFilePath);
		if (!auditLogFile.exists()) {
			File logDir = auditLogFile.getParentFile();
			if (!logDir.exists()) {
				logger.info("Audit log file container {} does not exist, make dir first.", logDir);
				logDir.mkdirs();
			} else {
				if (!logDir.isDirectory()) {
					logger.warn("Audit log file container {} should be a directory but a file found instead, delete the file then make dir.", logDir);
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
		kbuilder.add(ResourceFactory.newClassPathResource("drools-changeset.xml"), ResourceType.CHANGE_SET);
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error: errors) {
				logger.error(error.toString());
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
		this.taskServer.stop();
		// Because MinaTaskServer checks running status every 100ms,
		// so we wait here for 150ms to ensure that the task server thread
		// has been completely stopped.
		Thread.sleep(150);
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

		try {
			Environment env = KnowledgeBaseFactory.newEnvironment();
			env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, this.defaultPersistenceSvc.getSessionFactory());
			env.set(EnvironmentName.APP_SCOPED_ENTITY_MANAGER, this.defaultPersistenceSvc.getCurrentSession());
			env.set(EnvironmentName.TRANSACTION_MANAGER, this.txSvc.getTransactionManager());
			env.set(EnvironmentName.TRANSACTION, this.txSvc.getTransaction());
			env.set(EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY, this.txSvc.getTransactionSynchronizationRegistry());
			
			if (command.getSessionId() > 0) {
				ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(
						command.getSessionId(), this.kbase, null, env);
			} else {
				ksession = JPAKnowledgeService.newStatefulKnowledgeSession(this.kbase, null, env);
			}
			
			ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new WSHumanTaskHandler());
			
			//EmailWorkItemHandler emailHandler = new EmailWorkItemHandler();
			//emailHandler.setConnection("mail.tangrui.net", "21", "webmaster@tangrui.net", "P@$ther0");
			//ksession.getWorkItemManager().registerWorkItemHandler("Email", emailHandler);
			
			rtLogger = KnowledgeRuntimeLoggerFactory.newThreadedFileLogger(ksession, this.configSvc.mapPath(this.auditLogFilePath), this.auditLogFlushInterval);
			//new JPAWorkingMemoryDbLogger(ksession);
			new HistoryLogger(ksession, this.defaultPersistenceSvc);
			
			T result = command.execute(ksession);
			
			return result;
		} catch (Throwable t) {
			logger.error("Execute command failed.", t);
			throw new Exception(t);
		} finally {
			if (rtLogger != null) {
				try {
					rtLogger.close();
				} catch (Throwable t) {
					logger.error("Close knowledge runtime logger failed.", t);
				}
			}
			if (ksession != null) {
				ksession.dispose();
			}
		}
	}

}
