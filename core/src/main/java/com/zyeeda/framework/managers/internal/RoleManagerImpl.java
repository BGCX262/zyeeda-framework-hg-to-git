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
	
	private final static String PERMISSION_FILE = "permission.xml";
	public RoleManagerImpl(PersistenceService persistenceSvc) {
		super(persistenceSvc);
	}
	
	public  List<Permission> getListIdNameById(String id, String levelPath) throws XPathExpressionException, IOException {
		List<Permission> authList = null;
		InputStream is = null;
		InputSource src = null;
		XPathExpression exp = null;
		try {
			XPathFactory fac = XPathFactory.newInstance();
			XPath xpath = fac.newXPath();
			//if( level == 0){
				//	 exp = xpath.compile("/permissions/p[@id='"+id+"']");
				//}else{
					//int levelPath = 0;
					String path = "/p";
					int level = Integer.parseInt(levelPath);
					for(int j = 0; j<level; j++){
						path += "/p";
					}
					 exp = xpath.compile("/permissions"+path+"[@id='"+id+"']");
				//}
			//}
			level++;
			//File file = new File(path);
			is = this.getClass().getClassLoader().getResourceAsStream(PERMISSION_FILE);
			//InputStream is = new FileInputStream(file);
			src= new InputSource(is);
			NodeList list = (NodeList) exp.evaluate(src, XPathConstants.NODESET);
			authList = new ArrayList<Permission>();
			for (int i = 0; i < list.getLength(); i++) {
				// org.w3c.dom.Node node= list.item(i);
				Element element = (Element) list.item(i);
				NodeList children = element.getChildNodes();
				for (int j = 0; j < children.getLength(); j++) {
					Node e = children.item(j);
					if (e instanceof Element) {
						Element el = (Element) e;
						Permission permission=new Permission();
						permission.setId(el.getAttribute("id"));
						permission.setName(el.getAttribute("name"));
						permission.setValue(el.getAttribute("value"));
						permission.setIsHaveIO(el.getAttribute("isIO"));
						permission.setPath(level);
						authList.add(permission);
					}
				}
			}
		} finally {
			is.close();
		}
			return authList;
		}
//	public static final void main(String[] args) throws XPathExpressionException, IOException {
//		List<Permission> list=getListIdNameById("permissions/p[@id='a']","src/aa.xml");
//		for(int i = 0;i<list.size();i++){
//			Permission permission=list.get(i);
//			System.out.println(permission.getId());
//			System.out.println(permission.getName());
//			System.out.println(permission.getValue());
//		}
//	}
}
