package com.zyeeda.framework.ws;

import java.util.List;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.entities.Department;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.managers.internal.LdapDepartmentManager;
import com.zyeeda.framework.managers.internal.LdapUserManager;
import com.zyeeda.framework.viewmodels.DepartmentVo;
import com.zyeeda.framework.viewmodels.OrganizationNodeVo;
import com.zyeeda.framework.viewmodels.UserVo;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/depts")
public class DepartmentService extends ResourceService {
	
	private static final Logger logger = LoggerFactory.getLogger(LdapDepartmentManager.class);

	public DepartmentService(@Context ServletContext ctx) {
		super(ctx);
	}
	
	@POST
	@Path("/{parent}")
	@Produces("application/json")
	public Department createDepartment(@FormParam("") Department dept, @PathParam("parent") String parent) throws NamingException {
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		dept.setParent(parent);
		deptMgr.persist(dept);
		return deptMgr.findById(dept.getId());
		
		
	}
	
	@DELETE
	@Path("/{id}")
	@Produces("application/json")
	public void removeDepartment(@PathParam("id") String id) throws NamingException {
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		deptMgr.remove(id);
	}
	
	@PUT
	@Path("/{id}")
	@Produces("application/json")
	public DepartmentVo editDepartment(@FormParam("") Department dept, @PathParam("id") String id) throws NamingException {
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		return deptMgr.update(dept);
	}
	
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Department getDepartmentById(@PathParam("id") String id) throws NamingException {
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		return deptMgr.findById(id);
	}
	
	@GET
	@Path("/search/{name}")
	@Produces("application/json")
	public List<DepartmentVo> getDepartmentListByName(@PathParam("name") String name) throws NamingException {
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		return deptMgr.getDepartmentListByName(name);
	}
	
	@GET
	@Path("/{id}/children")
	@Produces("application/json")
	public List<OrganizationNodeVo> getChildrenNodesByDepartmentId(@PathParam("id") String id) throws NamingException {
		LdapService ldapSvc = this.getLdapService();
		
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		List<DepartmentVo> deptList = deptMgr.getDepartmentListById(id);
		
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		List<UserVo> userList = userMgr.getUserListByDepartmentId(id);
		
		List<OrganizationNodeVo> orgList = deptMgr.mergeDepartmentVoAndUserVo(deptList, userList);
		logger.debug("the size of the orgList  = {} ", orgList.size());
		
		return orgList;
	}
}
