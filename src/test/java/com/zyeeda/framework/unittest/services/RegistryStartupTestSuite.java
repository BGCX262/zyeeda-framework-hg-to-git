package com.zyeeda.framework.unittest.services;

import org.apache.tapestry5.ioc.Registry;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import com.zyeeda.framework.unittest.TestSuiteBase;

@Test
public class RegistryStartupTestSuite extends TestSuiteBase {

	@Test
	public void testGetRegistry() {
		Registry reg = getRegistry();
		assertNotNull(reg);
	}
	
}
