package com.zyeeda.framework.managers;

import java.io.IOException;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import com.zyeeda.framework.viewmodels.PermissionVo;

public interface PermissionManager {
		public List<PermissionVo> findSubPermissionById(String id, String authXml) throws XPathExpressionException, IOException;
	
	public  PermissionVo getPermissionByPath(String auth, String authXml) throws XPathExpressionException,
	IOException;
	
	public PermissionVo getParentPermissionByPath(String auth) throws XPathExpressionException,
	IOException;
	
	public String getParentPermissionListAuthByList(List<String> authList, String authXml) throws XPathExpressionException, IOException;
	
	
	public List<PermissionVo> getPermissionToTree(String id)
	throws XPathExpressionException, IOException ;

	}
