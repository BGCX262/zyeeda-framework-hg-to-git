package com.zyeeda.framework.managers.internal;


import java.io.IOException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.zyeeda.framework.managers.PermissionManager;
import com.zyeeda.framework.viewmodels.PermissionVo;

public class DefaultPermissionManager implements PermissionManager {

	private final static String ROAM_PERMISSION_FILE = "roamPermission.xml";
	private final static String PERMISSION_FILE = "permission.xml";

	public void getAllPermssion(String authId)
			throws XPathExpressionException, IOException {
		List<PermissionVo> permissions = new ArrayList<PermissionVo>();
		Map<String, PermissionVo> permissionMap = new LinkedHashMap<String, PermissionVo>();
		List<PermissionVo> list = new ArrayList<PermissionVo>();
		List<PermissionVo> authList = this.findSubRoamPermissionById(authId);
		for (PermissionVo permission : authList) {
			//String authId = permission.getId();
//			PermissionVo permissionVo = this.getPermissionByPath(permission
//					.getValue(), ROAM_PERMISSION_FILE);
			permissionMap.put(permission.getId(), permission);
			if (authList.size() == 0) {
				continue;
			}
			permission.getPermissionList().addAll(authList);
			list.add(permission);
			//this.getAllPermssion(authList);
		}
	}

	public List<PermissionVo> getPermissionToTree(String id)
			throws XPathExpressionException, IOException {
		List<PermissionVo> listPermission = new ArrayList<PermissionVo>();
		listPermission = this.findSubRoamPermissionById(id);
		for(PermissionVo permission : listPermission) {
			this.getAllPermssion(permission.getId());
		}
		return listPermission;
	}

	public List<PermissionVo> findSubRoamPermissionById(String id)
			throws XPathExpressionException, IOException {

		List<PermissionVo> authList = new ArrayList<PermissionVo>();
		InputStream is = null;
		InputSource src = null;
		XPathExpression exp = null;
		try {
			XPathFactory fac = XPathFactory.newInstance();
			XPath xpath = fac.newXPath();
			exp = xpath.compile("//p[@id='" + id + "']");
			is = this.getClass().getClassLoader().getResourceAsStream(
					ROAM_PERMISSION_FILE);
			src = new InputSource(is);
			NodeList list = (NodeList) exp
					.evaluate(src, XPathConstants.NODESET);
			authList = new ArrayList<PermissionVo>();
			for (int i = 0; i < list.getLength(); i++) {
				Element element = (Element) list.item(i);
				if (element == null) {
					return null;
				}
				NodeList children = element.getChildNodes();
				for (int j = 0; j < children.getLength(); j++) {
					Node e = children.item(j);
					if (e instanceof Element) {
						Element el = (Element) e;
						PermissionVo permission = new PermissionVo();
						permission.setId(el.getAttribute("id"));
						permission.setName(el.getAttribute("name"));
						permission.setValue(el.getAttribute("value"));
						permission.setOrderBy(el.getAttribute("order"));
						if (el.getAttribute("value").endsWith("*")) {
							permission.setIsHaveIO(false);
						} else {
							permission.setIsHaveIO(true);
						}
						authList.add(permission);
					}
				}
			}
		} finally {
			is.close();
		}
		return authList;
	}


	public List<PermissionVo> findSubPermissionById(String id, String authXml)
			throws XPathExpressionException, IOException {
		List<PermissionVo> authList = null;
		InputStream is = null;
		InputSource src = null;
		XPathExpression exp = null;
		try {
			XPathFactory fac = XPathFactory.newInstance();
			XPath xpath = fac.newXPath();
			exp = xpath.compile("//p[@id='" + id + "']");
			is = this.getClass().getClassLoader().getResourceAsStream(
					authXml);
			src = new InputSource(is);
			NodeList list = (NodeList) exp
					.evaluate(src, XPathConstants.NODESET);
			authList = new ArrayList<PermissionVo>();
			for (int i = 0; i < list.getLength(); i++) {
				Element element = (Element) list.item(i);
				NodeList children = element.getChildNodes();
				for (int j = 0; j < children.getLength(); j++) {
					Node e = children.item(j);
					if (e instanceof Element) {
						Element el = (Element) e;
						PermissionVo permission = new PermissionVo();
						permission.setId(el.getAttribute("id"));
						permission.setName(el.getAttribute("name"));
						permission.setValue(el.getAttribute("value"));
						permission.setOrderBy(el.getAttribute("order"));
						if (el.getAttribute("value").endsWith("*")) {
							permission.setIsHaveIO(false);
						} else {
							permission.setIsHaveIO(true);
						}
						authList.add(permission);
					}
				}
			}
		} finally {
			is.close();
		}
		return authList;
	}


	public PermissionVo getPermissionByPath(String auth, String authXml)
			throws XPathExpressionException, IOException {
		InputStream is = null;
		InputSource src = null;
		XPathExpression exp = null;
		PermissionVo permission = new PermissionVo();
		if (auth != null) {
			try {
				XPathFactory fac = XPathFactory.newInstance();
				XPath xpath = fac.newXPath();
				exp = xpath.compile("//p[@value='" + auth + "']");
				is = this.getClass().getClassLoader().getResourceAsStream(
						authXml);
				src = new InputSource(is);
				Node node = (Node) exp.evaluate(src, XPathConstants.NODE);
				Element elementNode = (Element) node;
				if (elementNode != null) {
					if (elementNode.getAttribute("value") == null) {
						return null;
					}
					permission.setId(elementNode.getAttribute("id"));
					permission.setName(elementNode.getAttribute("name"));
					permission.setValue(elementNode.getAttribute("value"));
					permission.setOrderBy(elementNode.getAttribute("order"));
					if (elementNode.getAttribute("value").endsWith("*")) {
						permission.setIsHaveIO(false);
					} else {
						permission.setIsHaveIO(true);
					}
				} else {
					return null;
				}
			} finally {
				is.close();
			}
		}
		return permission;
	}

	public PermissionVo getParentPermissionByPath(String auth)
			throws XPathExpressionException, IOException {
		InputStream is = null;
		InputSource src = null;
		XPathExpression exp = null;
		PermissionVo permission = new PermissionVo();
		if (auth != null) {
			try {
				XPathFactory fac = XPathFactory.newInstance();
				XPath xpath = fac.newXPath();
				exp = xpath.compile("//p[@value='" + auth + "']");
				is = this.getClass().getClassLoader().getResourceAsStream(
						PERMISSION_FILE);
				src = new InputSource(is);
				if (StringUtils.isNotBlank(auth) && auth != null) {
					Node node = (Node) exp.evaluate(src, XPathConstants.NODE);
					Element elementNode = (Element) node;
					Element elementParent = null;
					if (elementNode != null) {
						elementParent = (Element) elementNode.getParentNode();
						permission.setId(elementParent.getAttribute("id"));
						permission.setName(elementParent.getAttribute("name"));
						permission
								.setValue(elementParent.getAttribute("value"));
						permission.setOrderBy(elementParent
								.getAttribute("order"));
						if (StringUtils.isBlank(elementParent
								.getAttribute("value"))) {
							permission = null;
						}
					}
				} else {
					permission = null;
				}
			} finally {
				is.close();
			}
		}
		return permission;
	}

	
	private void getParentPermissionListAuthByPath(String auth,
			Set<String> allAuth, String authXml) throws XPathExpressionException, IOException {
		List<PermissionVo> permissionList = new ArrayList<PermissionVo>();
		permissionList = findSubPermissionByValue(auth, authXml);
		for (PermissionVo permission : permissionList) {
			System.out.println("*****" + permission.getValue());
			allAuth.add(permission.getValue());
			this.getParentPermissionListAuthByPath(permission.getValue(), allAuth, authXml);
		}
	}

	public String getParentPermissionListAuthByList(List<String> authList, String authXml)
			throws XPathExpressionException, IOException {
		Set<String> allAuth = new HashSet<String>();
		for (String auth : authList) {
			allAuth.add(auth);
			this.getParentPermissionListAuthByPath(auth, allAuth, authXml);
		}
		String utils = StringUtils.join(allAuth, ";");
		return utils;
	}

	public List<PermissionVo> findSubPermissionByValue(String value, String authXml)
			throws XPathExpressionException, IOException {
		List<PermissionVo> authList = null;
		InputStream is = null;
		InputSource src = null;
		XPathExpression exp = null;
		try {
			XPathFactory fac = XPathFactory.newInstance();
			XPath xpath = fac.newXPath();
			exp = xpath.compile("//p[@value='" + value + "']");
			is = this.getClass().getClassLoader().getResourceAsStream(
					authXml);
			src = new InputSource(is);
			NodeList list = (NodeList) exp
					.evaluate(src, XPathConstants.NODESET);
			authList = new ArrayList<PermissionVo>();
			for (int i = 0; i < list.getLength(); i++) {
				Element element = (Element) list.item(i);
				if (element != null) {
					NodeList children = element.getChildNodes();
					for (int j = 0; j < children.getLength(); j++) {
						Node e = children.item(j);
						if (e instanceof Element) {
							Element el = (Element) e;
							PermissionVo permission = new PermissionVo();
							permission.setId(el.getAttribute("id"));
							permission.setName(el.getAttribute("name"));
							permission.setValue(el.getAttribute("value"));
							permission.setOrderBy(el.getAttribute("order"));
							if (el.getAttribute("value").endsWith("*")) {
								permission.setIsHaveIO(false);
							} else {
								permission.setIsHaveIO(true);
							}
							authList.add(permission);
						}
					}
				}
			}
		} finally {
			is.close();
		}

		return authList;
	}



	
}
