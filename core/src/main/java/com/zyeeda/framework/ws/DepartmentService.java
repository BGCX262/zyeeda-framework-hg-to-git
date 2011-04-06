package com.zyeeda.framework.ws;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import com.zyeeda.framework.entities.Department;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.managers.internal.DefaultDepartmentManager;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/depts")
public class DepartmentService extends ResourceService {

	public DepartmentService(@Context ServletContext ctx) {
		super(ctx);
	}
	
	@POST
	@Path("/{parent:.*}")
	public void createDepartment(@FormParam("") Department dept, @PathParam("parent") String parent) throws NamingException {
		LdapService ldapSvc = this.getLdapService();
		DefaultDepartmentManager deptMgr = new DefaultDepartmentManager(ldapSvc);
		dept.setParent(parent);
		deptMgr.persist(dept);
	}

}
