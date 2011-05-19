package com.zyeeda.framework.managers.internal;

import java.util.ArrayList;
import java.util.List;

import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.realm.ldap.LdapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.entities.Department;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.ldap.internal.SunLdapServiceProvider;
import com.zyeeda.framework.managers.DepartmentManager;
import com.zyeeda.framework.utils.TreeDeleteControlUtils;

public class LdapDepartmentManager implements DepartmentManager {

	private static final Logger logger = LoggerFactory.getLogger(LdapDepartmentManager.class);
	
	private LdapService ldapSvc;
	
	public LdapDepartmentManager(LdapService ldapSvc) {
		this.ldapSvc = ldapSvc;
	}
	
	public void persist(Department dept) throws NamingException {
		LdapContext ctx = null;
		LdapContext parentCtx = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			String parent = dept.getParent();
			String dn = String.format("ou=%s", dept.getName());
			logger.debug("the value of the dn and parent is = {}   {}  ", dn, parent);
			
			Attributes attrs = LdapDepartmentManager.unmarshal(dept);
			parentCtx = (LdapContext) ctx.lookup(parent);
			parentCtx.createSubcontext(dn, attrs);
		
			String id = String.format("%s,%s", dn, parent);
			dept.setId(id);
		} finally {
			LdapUtils.closeContext(parentCtx);
			LdapUtils.closeContext(ctx);
		}
	}
	
	@Override
	public void remove(String id) throws NamingException {
		LdapContext ctx = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			ctx = (LdapContext) ctx.lookup(id);
			ctx.setRequestControls(new Control[] { new TreeDeleteControlUtils()});
			ctx.unbind("");
			
			logger.debug("ctx " + ctx.getNameInNamespace() + " deleted");
		} catch(OperationNotSupportedException e) {
			 ctx.setRequestControls(new Control[0]);
	         deleteRecursively(ctx);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}
	
	@Override
	public void update(Department dept) throws NamingException {
		LdapContext ctx = null;
		
		try {
			ctx = this.ldapSvc.getLdapContext();
			String oldName = dept.getId();
			Attributes attrs = unmarshal(dept);
			ctx.modifyAttributes(oldName, DirContext.REPLACE_ATTRIBUTE, attrs);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}
	
	public Department findById(String id) throws NamingException {
		LdapContext ctx = null;
		Attributes attrs = null;
		
		try {
			ctx = this.ldapSvc.getLdapContext();
			attrs = ctx.getAttributes(id);
			Department dept = LdapDepartmentManager.marshal(attrs);
			dept.setId(id);
			
			return dept;
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}
	
	@Override
	public List<Department> getDepartmentListById(String id) throws NamingException {
		LdapContext ctx = null;
		NamingEnumeration<SearchResult> ne = null;
		List<Department> deptList = null;
		
		try {
			ctx = this.ldapSvc.getLdapContext();
			SearchResult entry = null;
			ne = ctx.search(id, "(ou=*)", SunLdapServiceProvider.getOneLevelScopeSearchControls());
			
			deptList = new ArrayList<Department>();
			for (; ne.hasMore(); ) {
				entry = ne.next();
				Department dept = new Department();
				Attributes attr = entry.getAttributes();
				String childId = String.format("%s,%s", entry.getName(), id);
				
				dept.setName((String)attr.get("ou").get());
				dept.setDescription((String)attr.get("description").get());
				dept.setId(childId);
				
				deptList.add(dept);
			}
			
			return deptList;
		} finally {
			LdapUtils.closeEnumeration(ne);
			LdapUtils.closeContext(ctx);
		}
	}
	
	@Override
	public List<Department> getDepartmentListByName(String name)
			throws NamingException {
		LdapContext ctx = null;
		NamingEnumeration<SearchResult> ne = null;
		List<Department> deptList = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			ne = ctx.search("o=广州局", "(ou=*" + name + ")", SunLdapServiceProvider.getThreeLevelScopeSearchControls());
			
			SearchResult entry = null;
			deptList = new ArrayList<Department>();
			for (; ne.hasMore(); ) {
				entry = ne.next();
				String childId = String.format("%s,o=%s", entry.getName(), "广州局");
				logger.debug("the value of the dept childId is = {}  ", childId);
				
				Department dept = new Department();
				Attributes attr = entry.getAttributes();
				dept.setName((String)attr.get("ou").get());
				dept.setDescription((String)attr.get("description").get());
				dept.setId(childId);
				deptList.add(dept);
			}
			
			return deptList;
		} finally {
			LdapUtils.closeEnumeration(ne);
			LdapUtils.closeContext(ctx);
		}
	}

	private static Attributes unmarshal(Department dept) {
		Attributes attrs = new BasicAttributes();
		
		attrs.put("objectClass", "top");
		attrs.put("objectClass", "organizationalUnit");
		attrs.put("ou", dept.getName());
		if (StringUtils.isNotBlank(dept.getDescription())) {
			attrs.put("description", dept.getDescription());
		}
		return attrs;
	}
	
	private static Department marshal(Attributes attrs) throws NamingException {
		Department dept = new Department();
		
		if (attrs.get("ou") != null) {
			dept.setName((String) attrs.get("ou").get());
		} else if (attrs.get("o") != null) {
			dept.setName((String) attrs.get("o").get());
		}
		if (attrs.get("description") != null) {
			dept.setDescription((String) attrs.get("description").get());
		}
		return dept;
	}
	
	public static void deleteRecursively(LdapContext ctx) throws NamingException {
		NamingEnumeration<Binding> ne = ctx.listBindings("");
		 while (ne.hasMore()) {
		     Binding b = (Binding) ne.next();
		     if (b.getObject() instanceof LdapContext) {
		         deleteRecursively((LdapContext) b.getObject());
		     }
		 }
		 ctx.unbind("");
		 logger.debug("Entry " + ctx.getNameInNamespace() + " deleted");
	}
//	@Override
//	public List<Department> getDepartmentListById(String id, String type)
//			throws NamingException {
//		logger.debug("******************the method is getDepartmentListById*******************");
//		LdapContext ctx = null;
//		NamingEnumeration<SearchResult> ne = null;
//		List<DepartmentVo> deptList = null;
//		
//		try {
//			ctx = this.ldapSvc.getLdapContext();
//			SearchResult entry = null;
//			ne = ctx.search(id, "(ou=*)", this.getOneLevelScopeSearchControls());
//			
//			deptList = new ArrayList<DepartmentVo>();
//			for (; ne.hasMore(); ) {
//				entry = ne.next();
//				Department dept = new Department();
//				Attributes attr = entry.getAttributes();
//				String childId = String.format("%s,%s", entry.getName(), id);
//				
//				dept.setName((String)attr.get("ou").get());
//				dept.setDescription((String)attr.get("description").get());
//				dept.setId(childId);
//				
//				DepartmentVo deptVo = this.fillDepartmentPropertiesToVo(dept);
//				deptVo.setType(type);
//				deptVo.setIo(deptVo.getIo() + "?type=task");
//				deptList.add(dept);
//			}
//			return deptList;
//		} finally {
//			LdapUtils.closeEnumeration(ne);
//			LdapUtils.closeContext(ctx);
//		}
//	}
	
}
