package com.zyeeda.framework.managers.internal;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapContext;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.realm.ldap.LdapUtils;

import com.zyeeda.framework.entities.Department;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.managers.DepartmentManager;

public class DefaultDepartmentManager implements DepartmentManager {

	private LdapService ldapSvc;
	
	public DefaultDepartmentManager(LdapService ldapSvc) {
		this.ldapSvc = ldapSvc;
	}
	
	public void persist(Department dept) throws NamingException {
		LdapContext ctx = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			
			String parent = dept.getParent();
			if (!StringUtils.isBlank(parent)) {
				ctx = (LdapContext) ctx.lookup(parent);
			}
			
			Attributes attrs = unmarshal(dept);
			ctx.createSubcontext("ou=" + dept.getName(), attrs);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}
	
	public Department find(String dn) throws NamingException {
		LdapContext ctx = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			Attributes attrs = ctx.getAttributes(dn);
			return marshal(attrs);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}
	
	private static Attributes unmarshal(Department dept) {
		Attributes attrs = new BasicAttributes();
		
		attrs.put("objectClass", "top");
		attrs.put("objectClass", "organizationalUnit");
		attrs.put("ou", dept.getName());
		attrs.put("description", dept.getDescription());
		attrs.put("fax", dept.getFax());
		attrs.put("telephoneNumber", dept.getTelephoneNumber());
		
		return attrs;
	}
	
	private static Department marshal(Attributes attrs) throws NamingException {
		Department dept = new Department();
		
		dept.setName((String) attrs.get("ou").get());
		dept.setDescription((String) attrs.get("description").get());
		dept.setFax((String) attrs.get("fax").get());
		dept.setTelephoneNumber((String) attrs.get("telephoneNumber").get());
		
		return dept;
	}

}
