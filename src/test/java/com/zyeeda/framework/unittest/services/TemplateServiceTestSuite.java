package com.zyeeda.framework.unittest.services;

import org.apache.tapestry5.ioc.Registry;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import com.zyeeda.framework.template.TemplateService;
import com.zyeeda.framework.unittest.TestSuiteBase;

@Test
public class TemplateServiceTestSuite extends TestSuiteBase {

	@Test
	public void testFreemarkerTemplateServiceProvider() {
		Registry reg = getRegistry();
		TemplateService tplSvc = reg.getService(TemplateService.class);
		assertNotNull(tplSvc);
	}
}
