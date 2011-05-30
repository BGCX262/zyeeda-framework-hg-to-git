package com.zyeeda.drivebox.jobs;

import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.annotations.Scope;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.openid.consumer.shiro.PasswordFreeAuthenticationToken;
import com.zyeeda.framework.security.SecurityService;
import com.zyeeda.framework.security.annotations.Virtual;

@ServiceId("test-job")
@Scope(ScopeConstants.PERTHREAD)
public class TestJob implements Job {
	
	private final static Logger logger = LoggerFactory.getLogger(TestJob.class);
	
	private SecurityService<SecurityManager> securitySvc;
	
	public TestJob(@Virtual SecurityService<SecurityManager> securitySvc) {
		this.securitySvc = securitySvc;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("current time = {}", new Date());
		
		final Subject subject = new Subject.Builder(this.securitySvc.getSecurityManager()).buildSubject();
		subject.execute(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				subject.login(new PasswordFreeAuthenticationToken("system"));
				logger.info("current user = {}", securitySvc.getCurrentUser());
				return null;
			}
			
		});
	}

}
