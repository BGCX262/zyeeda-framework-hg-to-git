package com.zyeeda.framework.managers;

import java.io.IOException;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import com.zyeeda.framework.entities.Permission;

public interface PermissionManager {
	
	public List<Permission> findSubPermissionById(String id) throws XPathExpressionException, IOException;
	
	public  Permission getPermissionByPath(String auth) throws XPathExpressionException,
	IOException;
	
	public Permission getParentPermissionByPath(String auth) throws XPathExpressionException,
	IOException;
	
}
