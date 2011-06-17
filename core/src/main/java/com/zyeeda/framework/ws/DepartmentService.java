package com.zyeeda.framework.ws;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.managers.UserPersistException;
import com.zyeeda.framework.managers.internal.LdapDepartmentManager;
import com.zyeeda.framework.managers.internal.LdapUserManager;
import com.zyeeda.framework.managers.internal.RoleManagerImpl;
import com.zyeeda.framework.viewmodels.DepartmentVo;
import com.zyeeda.framework.viewmodels.OrganizationNodeVo;
import com.zyeeda.framework.viewmodels.UserVo;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/depts")
public class DepartmentService extends ResourceService {
	
	public DepartmentService(@Context ServletContext ctx) {
		super(ctx);
	}
	
	@POST
	@Path("/{parent}")
	@Produces("application/json")
	public Department persist(@FormParam("") Department dept,
							  @PathParam("parent") String parent)
						 throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		if (deptMgr.findByName(dept.getName()) != null && 
				deptMgr.findByName(dept.getName()).size() > 0) {
			throw new RuntimeException("部门名称不能重复");
		} else {
			dept.setParent(parent);
			dept.setId("ou=" + dept.getName() + "," + dept.getParent());
			deptMgr.persist(dept);
			
			return deptMgr.findById(dept.getId());
		}
	}
	
	@DELETE
	@Path("/{id}")
	@Produces("application/json")
	public void remove(@PathParam("id") String id) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		deptMgr.remove(id);
	}
	
	@PUT
	@Path("/{id}")
	@Produces("application/json")
	public Department update(@FormParam("") Department dept,
							 @PathParam("id") String id)
						throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		dept.setId(id);
		deptMgr.update(dept);
		return deptMgr.findById(id);
	}
	
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Department findById(@PathParam("id") String id) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		
		return deptMgr.findById(id);
	}
	
	@GET
	@Path("/search/{name}")
	@Produces("application/json")
	public List<DepartmentVo> findByName(@PathParam("name") String name) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		
		return DepartmentService.fillPropertiesToVo(deptMgr.findByName(name));
	}
	
	@GET
	@Path("/search/{parent}/{name}")
	@Produces("application/json")
	public List<DepartmentVo> findByName(@PathParam("parent") String parent,
										 @PathParam("name") String name)
									throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		
		return DepartmentService.fillPropertiesToVo(deptMgr.findByName(parent, name));
	}
	
	@GET
	@Path("/{id}/children")
	@Produces("application/json")
	public List<OrganizationNodeVo> getChildrenById(@Context HttpServletRequest request, 
			@PathParam("id") String id) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		List<DepartmentVo> deptVoList = null;
		List<UserVo> userVoList = null;
		String type = request.getParameter("type");
		if (StringUtils.isNotBlank(type) && "task".equals(type)) {
			deptVoList = DepartmentService.fillPropertiesToVo(deptMgr.getChildrenById(id), type);
			userVoList = UserService.fillUserListPropertiesToVo(userMgr.findByDepartmentId(id), type);
		} else {
			deptVoList = DepartmentService.fillPropertiesToVo(deptMgr.getChildrenById(id));
			userVoList = UserService.fillUserListPropertiesToVo(userMgr.findByDepartmentId(id));
		}
		List<OrganizationNodeVo> orgList = this.mergeDepartmentVoAndUserVo(deptVoList, userVoList);
		
		return orgList;
	}
	
	private List<OrganizationNodeVo> mergeDepartmentVoAndUserVo(List<DepartmentVo> deptVoList,
																List<UserVo> userVoList) {
		List<OrganizationNodeVo> orgNodeVoList = new ArrayList<OrganizationNodeVo>();
		for (DepartmentVo deptVo: deptVoList) {
			OrganizationNodeVo orgNodeVo = new OrganizationNodeVo();
			orgNodeVo.setId(deptVo.getId());
			orgNodeVo.setCheckName(deptVo.getCheckName());
			orgNodeVo.setIo(deptVo.getIo());
			orgNodeVo.setLabel(deptVo.getLabel());
			orgNodeVo.setType(deptVo.getType());
			orgNodeVo.setFullPath(deptVo.getDeptFullPath());
			orgNodeVo.setKind(deptVo.getKind());
			
			orgNodeVoList.add(orgNodeVo);
		}
		
		for (UserVo userVo: userVoList) {
			OrganizationNodeVo orgNodeVo = new OrganizationNodeVo();
			orgNodeVo.setId(userVo.getDeptFullPath());
			orgNodeVo.setCheckName(userVo.getCheckName());
			orgNodeVo.setIo(userVo.getId());
			orgNodeVo.setLabel(userVo.getLabel());
			orgNodeVo.setType(userVo.getType());
			orgNodeVo.setLeaf(userVo.isLeaf());
			orgNodeVo.setFullPath(userVo.getDeptFullPath());
			orgNodeVo.setKind(userVo.getKind());
			
			orgNodeVoList.add(orgNodeVo);
		}
		return orgNodeVoList;
	}
	
	public static DepartmentVo fillPropertiesToVo(Department dept) {
		DepartmentVo deptVo = new DepartmentVo();
		
		deptVo.setId(dept.getId());
		deptVo.setType("io");
		deptVo.setLabel(dept.getName());
		deptVo.setCheckName(dept.getId());
		deptVo.setLeaf(false);
		deptVo.setDeptFullPath("ou=" + dept.getId() + "," + dept.getParent());
		deptVo.setIo("/rest/depts/" + deptVo.getDeptFullPath() + "/children");
		deptVo.setKind("dept");
		
		return deptVo;
	}
	
	public static List<DepartmentVo> fillPropertiesToVo(List<Department> deptList,
																	  String type) {
		List<DepartmentVo> deptVoList = new ArrayList<DepartmentVo>(deptList.size());
		DepartmentVo deptVo = null;
		for (Department dept : deptList) {
			deptVo = DepartmentService.fillPropertiesToVo(dept);
			deptVo.setId(dept.getId());
			deptVo.setIo(deptVo.getIo() + "?type=task");
			deptVo.setType(type);
			deptVoList.add(deptVo);
		}
		return deptVoList;
	}
	
	public static List<DepartmentVo> fillPropertiesToVo(List<Department> deptList) {
		List<DepartmentVo> deptVoList = new ArrayList<DepartmentVo>(deptList.size());
		DepartmentVo deptVo = null;
		for (Department dept : deptList) {
			deptVo = DepartmentService.fillPropertiesToVo(dept);
			deptVoList.add(deptVo);
		}
		return deptVoList;
	}	
	
	@GET
	@Path("/{id}/children/{roleId}")
	@Produces("application/json")
	public List<OrganizationNodeVo> getChildrenNodesByDepartmentIdAndRoleId(@Context HttpServletRequest request, 
																			@PathParam("id") String id,
																			@PathParam("roleId") String roleId)
																	   throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		RoleManager roleMgr = new RoleManagerImpl(this.getPersistenceService());
		Role role = roleMgr.find(id);
		Set<String> roleByUser = new HashSet<String>();
		if(role != null) {
			roleByUser = role.getSubjects();
		}
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		List<DepartmentVo> deptVoList = null;
		List<UserVo> userVoList = null;
		String type = request.getParameter("type");
		
		if (StringUtils.isNotBlank(type) && "task".equals(type)) {
			deptVoList = DepartmentService.fillDepartmentListPropertiesToVoByRoleId(deptMgr.getChildrenById(id), type, roleId);
		} else {
			deptVoList = DepartmentService.fillPropertiesToVo(deptMgr.getChildrenById(id));
		}
		userVoList = UserService.fillUserListPropertiesToVo(userMgr.findByDepartmentId(id));
		List<OrganizationNodeVo> orgList = this.mergeDepartmentVoAndUserVoCheckUser(deptVoList, userVoList, roleByUser);
		
		return orgList;
	}
	
	private List<OrganizationNodeVo> mergeDepartmentVoAndUserVoCheckUser(List<DepartmentVo> deptVoList, 
																		 List<UserVo> userVoList,
																		 Set<String> userId) {
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
			orgNodeVo.setId(userVo.getId());
			orgNodeVo.setCheckName(userVo.getCheckName());
			orgNodeVo.setIo(userVo.getId());
			for(String id:userId){
				if(id.equals(userVo.getId())){
					orgNodeVo.setCheckedAuth(true);
				}
			}
			orgNodeVo.setLabel(userVo.getLabel());
			orgNodeVo.setType("task");
			orgNodeVo.setLeaf(userVo.isLeaf());
			orgNodeVo.setFullPath("uid=" + userVo.getId() + "," + userVo.getDeptFullPath());
			orgNodeVo.setKind(userVo.getKind());			
			orgNodeVoList.add(orgNodeVo);
		}
		return orgNodeVoList;
	}
	
	public static DepartmentVo fillDepartmentPropertiesToVoByRole(Department dept, String roleId) {
		DepartmentVo deptVo = new DepartmentVo();
		
		deptVo.setId(dept.getId());
		deptVo.setType("io");
		deptVo.setLabel(dept.getName());
		deptVo.setCheckName(dept.getId());
		deptVo.setLeaf(false);
		
		deptVo.setIo("/rest/depts/" + dept.getId() + "/children/" + roleId );
		deptVo.setKind("dept");
		
		return deptVo;
	}
	
	public static List<DepartmentVo> fillDepartmentListPropertiesToVoByRoleId(List<Department> deptList,
																			  String type,
																			  String roleId) {
		List<DepartmentVo> deptVoList = new ArrayList<DepartmentVo>(deptList.size());
		DepartmentVo deptVo = null;
		for (Department dept : deptList) {
			deptVo = DepartmentService.fillDepartmentPropertiesToVoByRole(dept, roleId);
			deptVo.setIo(deptVo.getIo() + "?type=task");
			deptVo.setType(type);
			deptVoList.add(deptVo);
		}
		return deptVoList;
	}
}
