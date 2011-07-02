package com.zyeeda.framework.unittest.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.ioc.Registry;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import com.zyeeda.framework.template.TemplateService;
import com.zyeeda.framework.unittest.TestSuiteBase;

@Test
public class TemplateServiceTest extends TestSuiteBase {

	@Test(enabled = false)
	public void testGetTemplateService() {
		TemplateService tplSvc = this.getService();
		assertNotNull(tplSvc);
	}
	
	@Test(enabled = false)
	public void testRenderString() throws IOException {
		TemplateService tplSvc = this.getService();
		String tpl = "hello ${name}";
		Map<String, Object> args = new HashMap<String, Object>(1);
		args.put("name", "world");
		String result = tplSvc.render(tpl, args);
		assertEquals(result, "hello world");
	}
	
	private TemplateService getService() {
		Registry reg = getRegistry();
		TemplateService tplSvc = reg.getService(TemplateService.class);
		return tplSvc;
	}
}
