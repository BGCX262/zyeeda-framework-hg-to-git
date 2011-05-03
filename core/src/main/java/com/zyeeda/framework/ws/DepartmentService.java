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
import com.zyeeda.framework.managers.internal.DefaultDepartmentManager;
import com.zyeeda.framework.managers.internal.DefaultUserManager;
import com.zyeeda.framework.viewmodels.DepartmentVo;
import com.zyeeda.framework.viewmodels.OrganizationNodeVo;
import com.zyeeda.framework.viewmodels.UserVo;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/depts")
public class DepartmentService extends ResourceService {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultDepartmentManager.class);

	public DepartmentService(@Context ServletContext ctx) {
		super(ctx);
	}
	
	@POST
	@Path("/{parent}")
	@Produces("application/json")
	public DepartmentVo createDepartment(@FormParam("") Department dept, @PathParam("parent") String parent) throws NamingException {
		// 传入类似参数ou=dada,o=广州局,在ou=datda,o=广州局下创建部门
		logger.debug("=====================create method==========================" + parent);
		LdapService ldapSvc = this.getLdapService();
		DefaultDepartmentManager deptMgr = new DefaultDepartmentManager(ldapSvc);
		dept.setParent(parent);
		return deptMgr.persist(dept);
	}
	
	@DELETE
	@Path("/{id}")
	@Produces("application/json")
	public void removeDepartment(@PathParam("id") String id) throws NamingException {
		// 传入类似参数ou=dada,o=广州局
		logger.debug("=====================remove method==========================" + id);
		LdapService ldapSvc = this.getLdapService();
		DefaultDepartmentManager deptMgr = new DefaultDepartmentManager(ldapSvc);
		deptMgr.remove(id);
	}
	
	@PUT
	@Path("/{id}")
	@Produces("application/json")
	public DepartmentVo editDepartment(@FormParam("") Department dept, @PathParam("id") String id) throws NamingException {
		// 传入类似参数ou=dada,o=广州局
		logger.debug("=====================edit method==========================" + id);
		LdapService ldapSvc = this.getLdapService();
		DefaultDepartmentManager deptMgr = new DefaultDepartmentManager(ldapSvc);
		dept.setId(id);
		return deptMgr.update(dept);
	}
	
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Department getDepartmentById(@PathParam("id") String id) throws NamingException {
		// 传入类似参数ou=dada,o=广州局
		logger.debug("=====================getDeptMent by id method==========================" + id);
		LdapService ldapSvc = this.getLdapService();
		DefaultDepartmentManager deptMgr = new DefaultDepartmentManager(ldapSvc);
		return deptMgr.findById(id);
	}
	
	@GET
	@Path("/search/{name}")
	@Produces("application/json")
	public List<DepartmentVo> getDepartmentListByName(@PathParam("name") String name) throws NamingException {
		// 传入类似参数dada(单纯名称就可以)
		logger.debug("=====================getDepartmentListByName method==========================" + name);
		LdapService ldapSvc = this.getLdapService();
		DefaultDepartmentManager deptMgr = new DefaultDepartmentManager(ldapSvc);
		return deptMgr.getDepartmentListByName(name);
	}
	
	@GET
	@Path("/{id}/children")
	@Produces("application/json")
	public List<OrganizationNodeVo> getChildrenNodesByDepartmentId(@PathParam("id") String id) throws NamingException {
		logger.debug("=====================getDepartmentList method==========================id = {} ", id);
		LdapService ldapSvc = this.getLdapService();
		
		DefaultDepartmentManager deptMgr = new DefaultDepartmentManager(ldapSvc);
		List<DepartmentVo> deptList = deptMgr.getDepartmentListById(id);
		
		DefaultUserManager userMgr = new DefaultUserManager(ldapSvc);
		List<UserVo> userList = userMgr.getUserListByDepartmentId(id);
		
		List<OrganizationNodeVo> orgList = deptMgr.mergeDepartmentVoAndUserVo(deptList, userList);
		logger.debug("the size of the orgList  = {} ", orgList.size());
		
		return orgList;
	}
}
