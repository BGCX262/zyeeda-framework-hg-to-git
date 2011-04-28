package com.zyeeda.framework.managers.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.zyeeda.framework.entities.Permission;
import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.managers.base.DomainEntityManager;
import com.zyeeda.framework.persistence.PersistenceService;

public class RoleManagerImpl extends DomainEntityManager<Role, String> implements RoleManager{

	public RoleManagerImpl(PersistenceService persistenceSvc) {
		super(persistenceSvc);
	}
	
	public List<Permission> getListIdNameById(String id,String path) throws XPathExpressionException, IOException {
		XPathFactory fac = XPathFactory.newInstance();
		XPath xpath = fac.newXPath();
		XPathExpression exp = xpath.compile("/permissions/p[@id='"+id+"']");
		Permission permission=new Permission();
		File file = new File(path);
		InputStream is = new FileInputStream(file);
		InputSource src = new InputSource(is);
		NodeList list = (NodeList) exp.evaluate(src, XPathConstants.NODESET);
		is.close();
		List<Permission> authList=new ArrayList<Permission>();
		for (int i = 0; i < list.getLength(); i++) {
			// org.w3c.dom.Node node= list.item(i);
			Element element = (Element) list.item(i);
			NodeList children = element.getChildNodes();
			for (int j = 0; j < children.getLength(); j++) {
				Node e = children.item(j);
				if (e instanceof Element) {
					Element el = (Element) e;
					permission.setId(el.getAttribute("id"));
					permission.setName(el.getAttribute("name"));
					permission.setValue(el.getAttribute("value"));
					authList.add(permission);
				}
			}
		}
		return authList;
		}

}
