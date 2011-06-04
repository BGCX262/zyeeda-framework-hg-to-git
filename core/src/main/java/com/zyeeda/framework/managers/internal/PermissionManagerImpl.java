package com.zyeeda.framework.managers.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

import com.zyeeda.framework.entities.Permission;
import com.zyeeda.framework.managers.PermissionManager;
import com.zyeeda.framework.persistence.PersistenceService;

public class PermissionManagerImpl  implements PermissionManager {
	
	

	private final static String PERMISSION_FILE = "permission.xml";
	public List<Permission> findSubPermissionById(String id)
			throws XPathExpressionException, IOException {
		List<Permission> authList = null;
		InputStream is = null;
		InputSource src = null;
		XPathExpression exp = null;
		try {
			XPathFactory fac = XPathFactory.newInstance();
			XPath xpath = fac.newXPath();
			exp = xpath.compile("//p[@id='" + id + "']");
			is = this.getClass().getClassLoader().getResourceAsStream(
					PERMISSION_FILE);
			src = new InputSource(is);
			NodeList list = (NodeList) exp
					.evaluate(src, XPathConstants.NODESET);
			authList = new ArrayList<Permission>();
			for (int i = 0; i < list.getLength(); i++) {
				Element element = (Element) list.item(i);
				NodeList children = element.getChildNodes();
				for (int j = 0; j < children.getLength(); j++) {
					Node e = children.item(j);
					if (e instanceof Element) {
						Element el = (Element) e;
						Permission permission = new Permission();
						permission.setId(el.getAttribute("id"));
						permission.setName(el.getAttribute("name"));
						permission.setValue(el.getAttribute("value"));
						if (el.getAttribute("value").endsWith("*")) {
							permission.setIsHaveIO(false);
						}else{
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

	public Permission getPermissionByPath(String auth) throws XPathExpressionException,
			IOException {
		InputStream is = null;
		InputSource src = null;
		XPathExpression exp = null;
		Permission permission =new Permission();
		if (auth != null) {
			try {
				XPathFactory fac = XPathFactory.newInstance();
				XPath xpath = fac.newXPath();
				exp = xpath.compile("//p[@value='" + auth + "']");
				is = this.getClass().getClassLoader().getResourceAsStream(
						PERMISSION_FILE);
				src = new InputSource(is);
				Node node = (Node) exp.evaluate(src, XPathConstants.NODE);
				Element elementNode = (Element) node;
				if(elementNode != null){
					permission.setId(elementNode.getAttribute("id"));
					permission.setName(elementNode.getAttribute("name"));
					permission.setValue(elementNode.getAttribute("value"));
				}
			} finally {
				is.close();
			}
		}
		return permission;
	}
	
	public Permission getParentPermissionByPath(String auth) throws XPathExpressionException,
	IOException {
			InputStream is = null;
			InputSource src = null;
			XPathExpression exp = null;
			Permission permission = new Permission();
			if (auth != null) {
				try {
					XPathFactory fac = XPathFactory.newInstance();
					XPath xpath = fac.newXPath();
					exp = xpath.compile("//p[@value='" + auth + "']");
					is = this.getClass().getClassLoader().getResourceAsStream(PERMISSION_FILE);
					src = new InputSource(is);
					if(StringUtils.isNotBlank(auth) && auth != null){
						Node node = (Node) exp.evaluate(src, XPathConstants.NODE);
						Element elementNode = (Element) node;
					    Element elementParent = (Element)elementNode.getParentNode();
						if (elementNode != null) {
							permission.setId(elementParent.getAttribute("id")); 
							 permission.setName(elementParent.getAttribute("name"));
							 permission.setValue(elementParent.getAttribute("value"));
						}
						if(StringUtils.isBlank(elementParent.getAttribute("value"))){
							permission = null;
						}
					}else{
						permission = null;
					}
				} finally {
					is.close();
				}
			}
			 return permission;
		}
}