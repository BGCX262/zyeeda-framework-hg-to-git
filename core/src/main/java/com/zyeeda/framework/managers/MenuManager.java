package com.zyeeda.framework.managers;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import com.zyeeda.framework.entities.Menu;

public interface MenuManager {
	
	public List<Menu> getMenuListByPermissionAuth(Set<String> authList) throws XPathExpressionException, IOException;

}