package com.zyeeda.framework.ws;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;

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
	
//	private static final Logger logger = LoggerFactory.getLogger(LdapDepartmentManager.class);

	public DepartmentService(@Context ServletContext ctx) {
		super(ctx);
	}
	
	@POST
	@Path("/{parent}")
	@Produces("application/json")
	public Department createDepartment(@FormParam("") Department dept, @PathParam("parent") String parent) throws NamingException {
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		if (deptMgr.findByName(dept.getName()) != null && 
				deptMgr.findByName(dept.getName()).size() > 0) {
			throw new RuntimeException("部门名称不能重复");
		} else {
			dept.setParent(parent);
			deptMgr.persist(dept);
			
			return deptMgr.findById(dept.getId());
		}
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
	public Department editDepartment(@FormParam("") Department dept, @PathParam("id") String id) throws NamingException {
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		
		String name = deptMgr.findById(dept.getId()).getName();
		if (dept.getName().equals(name)) {
			deptMgr.update(dept);
			return deptMgr.findById(id);
		} else {
			throw new RuntimeException("不能修改部门名称");
		}
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
		
		return DepartmentService.fillDepartmentListPropertiesToVo(deptMgr.findByName(name));
	}
	
	@GET
	@Path("/{id}/children")
	@Produces("application/json")
	public List<OrganizationNodeVo> getChildrenNodesByDepartmentId(@Context HttpServletRequest request, 
			@PathParam("id") String id) throws NamingException {
		LdapService ldapSvc = this.getLdapService();
		
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		List<DepartmentVo> deptVoList = null;
		List<UserVo> userVoList = null;
		String type = request.getParameter("type");
		if (StringUtils.isNotBlank(type) && "task".equals(type)) {
			deptVoList = DepartmentService.fillDepartmentListPropertiesToVo(deptMgr.getChildrenById(id), type);
			userVoList = UserService.fillUserListPropertiesToVo(userMgr.findByDepartmentId(id), type);
		} else {
			deptVoList = DepartmentService.fillDepartmentListPropertiesToVo(deptMgr.getChildrenById(id));
			userVoList = UserService.fillUserListPropertiesToVo(userMgr.findByDepartmentId(id));
		}
		
		List<OrganizationNodeVo> orgList = this.mergeDepartmentVoAndUserVo(deptVoList, userVoList);
		
		return orgList;
	}
	
	private List<OrganizationNodeVo> mergeDepartmentVoAndUserVo(List<DepartmentVo> deptVoList, List<UserVo> userVoList) {
		List<OrganizationNodeVo> orgNodeVoList = new ArrayList<OrganizationNodeVo>();
		for (DepartmentVo deptVo: deptVoList) {
			OrganizationNodeVo orgNodeVo = new OrganizationNodeVo();
			orgNodeVo.setId(deptVo.getId());
			orgNodeVo.setCheckName(deptVo.getCheckName());
			orgNodeVo.setIo(deptVo.getIo());
			orgNodeVo.setLabel(deptVo.getLabel());
			orgNodeVo.setType(deptVo.getType());
			orgNodeVo.setFullPath(deptVo.getId());
			orgNodeVo.setKind(deptVo.getKind());
			
			orgNodeVoList.add(orgNodeVo);
		}
		
		for (UserVo userVo: userVoList) {
			OrganizationNodeVo orgNodeVo = new OrganizationNodeVo();
			orgNodeVo.setId("uid=" + userVo.getId() + "," + userVo.getDeptFullPath());
			orgNodeVo.setCheckName(userVo.getCheckName());
			orgNodeVo.setIo(userVo.getId());
			orgNodeVo.setLabel(userVo.getLabel());
			orgNodeVo.setType(userVo.getType());
			orgNodeVo.setLeaf(userVo.isLeaf());
			orgNodeVo.setFullPath("uid=" + userVo.getId() + "," + userVo.getDeptFullPath());
			orgNodeVo.setKind(userVo.getKind());
			
			orgNodeVoList.add(orgNodeVo);
		}
		
		return orgNodeVoList;
	}
	
	public static DepartmentVo fillDepartmentPropertiesToVo(Department dept) {
		DepartmentVo deptVo = new DepartmentVo();
		
		deptVo.setId(dept.getId());
		deptVo.setType("io");
		deptVo.setLabel("<a>" + dept.getName() + "<a>");
		deptVo.setCheckName(dept.getId());
		deptVo.setLeaf(false);
		deptVo.setIo("/rest/depts/" + dept.getId() + "/children");
		deptVo.setKind("dept");
		
		return deptVo;
	}
	
	public static List<DepartmentVo> fillDepartmentListPropertiesToVo(List<Department> deptList) {
		List<DepartmentVo> deptVoList = new ArrayList<DepartmentVo>(deptList.size());
		DepartmentVo deptVo = null;
		for (Department dept : deptList) {
			deptVo = DepartmentService.fillDepartmentPropertiesToVo(dept);
			deptVoList.add(deptVo);
		}
		return deptVoList;
	}
	
	public static List<DepartmentVo> fillDepartmentListPropertiesToVo(List<Department> deptList, String type) {
		List<DepartmentVo> deptVoList = new ArrayList<DepartmentVo>(deptList.size());
		DepartmentVo deptVo = null;
		for (Department dept : deptList) {
			deptVo = DepartmentService.fillDepartmentPropertiesToVo(dept);
			deptVo.setIo(deptVo.getIo() + "?type=task");
			deptVo.setType(type);
			deptVoList.add(deptVo);
		}
		return deptVoList;
	}
}
