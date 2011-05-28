package com.zyeeda.drivebox.jobs;

import java.util.Date;

import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.annotations.Scope;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServiceId("test-job")
@Scope(ScopeConstants.PERTHREAD)
public class TestJob implements Job {
	
	private final static Logger logger = LoggerFactory.getLogger(TestJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("Current time = {}", new Date());
	}

}
