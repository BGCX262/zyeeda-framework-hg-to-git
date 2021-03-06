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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.entities.Department;
import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.managers.UserManager;
import com.zyeeda.framework.managers.UserPersistException;
import com.zyeeda.framework.managers.internal.DefaultRoleManager;
import com.zyeeda.framework.managers.internal.LdapDepartmentManager;
import com.zyeeda.framework.managers.internal.LdapUserManager;
import com.zyeeda.framework.viewmodels.DepartmentVo;
import com.zyeeda.framework.viewmodels.OrganizationNodeVo;
import com.zyeeda.framework.viewmodels.UserVo;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/depts")
public class DepartmentService extends ResourceService {
	
	private static final Logger logger = LoggerFactory.getLogger(DepartmentService.class);
	
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
			dept.setDeptFullPath("ou=" + dept.getName() + "," + dept.getParent());
			deptMgr.persist(dept);
			
			return deptMgr.findById(dept.getId());
		}
	}
	
	@DELETE
	@Path("/{id}")
	@Produces("application/json")
	public String remove(@PathParam("id") String id,
			 			 @FormParam("cascade") String cascade)
					throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		LdapUserManager userManager = new LdapUserManager(ldapSvc);
		if (cascade != null) {
			deptMgr.remove(id);
			return "{\"success\": \"true\"}";
		} else {
			Integer deptCount = deptMgr.getChildrenCountById(id, "(objectclass=*)");
			Integer userCount = userManager.getChildrenCountById(id, "(objectclass=*)");
			if (userCount > 0 || deptCount > 0) {
				return "{\"success\": \"false\"}";
			} else {
				deptMgr.remove(id);
				return "{\"success\": \"true\"}";
			}
		}
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
	@Path("/search")
	@Produces("application/json")
	public String search(@FormParam("name") String name) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		List<Department> deptList = deptMgr.search(name);
		StringBuffer buffer = new StringBuffer("{");
		buffer.append("\"totalRecords\":").append(deptList.size())
	      	  .append(",").append("\"startIndex\":").append(0)
	      	  .append(",").append("\"pageSize\":").append(13)
	      	  .append(",").append("\"records\":[");
		for (Department department : deptList) {
			buffer.append("{\"name\":").append("\"").append(department.getId()).append("\"").append(",")
			      .append("\"parent\":").append("\"").append(department.getParent() == null ? "" : department.getParent()).append("\"").append(",")
			      .append("\"fullpath\":").append("\"").append(department.getDeptFullPath() == null ? "" : department.getDeptFullPath()).append("\"").append(",")
			      .append("\"description\":").append("\"").append(department.getDescription() == null ? "" : department.getDescription()).append("\"").append("},");
		}
		if (buffer.lastIndexOf(",") != -1 && deptList.size() > 0) {
			buffer.deleteCharAt(buffer.lastIndexOf(","));
		}
		buffer.append("]}");
		
		return buffer.toString();
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
													@PathParam("id") String id)
											   throws UserPersistException {
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
			logger.debug("username = {}", userVo.getCheckName());
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
		if (StringUtils.isBlank(dept.getParent())) {
			deptVo.setDeptFullPath("o=" + dept.getId());
		} else {
			deptVo.setDeptFullPath("ou=" + dept.getId() + "," + dept.getParent());
		}
		if (StringUtils.isBlank(deptVo.getDeptFullPath())) {
			deptVo.setIo("/rest/depts/root/children");
		} else {
			deptVo.setIo("/rest/depts/" + deptVo.getDeptFullPath() + "/children");
		}
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
	
	@GET
	@Path("second_level_dept_role")
	@Produces("application/json")
	public List<DepartmentVo> getSecondLevelDepartmentAndRole() throws UserPersistException {
		List<Department> deptList = null;
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		deptList = deptMgr.getChildrenById("o=广州局");
		List<DepartmentVo> deptVoList = DepartmentService.fillPropertiesToVoAndRoles(deptList);
		return deptVoList;
	}
	
	public static List<DepartmentVo> fillPropertiesToVoAndRoles(List<Department> deptList) {
		List<DepartmentVo> deptVoList = new ArrayList<DepartmentVo>(deptList.size());
		DepartmentVo deptVo = null; 
		for (Department dept : deptList) {
			deptVo = DepartmentService.fillPropertiesToVoAndRole(dept);
			deptVoList.add(deptVo);
		}
		return deptVoList;
	}
	
	
	public static DepartmentVo fillPropertiesToVoAndRole(Department dept) {
		DepartmentVo deptVo = new DepartmentVo();
		deptVo.setType("io");
		deptVo.setLabel(dept.getName());
		deptVo.setId(dept.getDeptFullPath());
		deptVo.setCheckName(dept.getId());
		deptVo.setLeaf(false);
		if (StringUtils.isNotBlank(dept.getDeptFullPath())) {
			deptVo.setIo("/rest/roles/dept_and_role/" + dept.getDeptFullPath());
		}
		deptVo.setKind("dept");
		return deptVo;
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
	@Path("root_and_second_level_dept")
	@Produces("application/json")
	public List<Department> getRootAndSecondLevelDepartment() throws UserPersistException {
		List<Department> deptList = null;
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		deptList = deptMgr.getRootAndSecondLevelDepartment();
//		List<DepartmentVo> deptVoList = DepartmentService.fillPropertiesToVo(deptList);
		return deptList;
	}
	
	@GET
	@Path("root_and_second_level_dept_vo")
	@Produces("application/json")
	public List<DepartmentVo> getRootAndSecondLevelDepartmentVo() throws UserPersistException {
		List<Department> deptList = null;
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		deptList = deptMgr.getRootAndSecondLevelDepartment();
		List<DepartmentVo> deptVoList = DepartmentService.fillPropertiesToVo(deptList);
		for (DepartmentVo deptVo : deptVoList) {
			deptVo.setIo("");
		}
		return deptVoList;
	}
	
	@GET
	@Path("second_level_dept")
	@Produces("application/json")
	public List<Department> getSecondLevelDepartment() throws UserPersistException {
		List<Department> deptList = null;
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		deptList = deptMgr.getChildrenById("o=广州局");
		return deptList;
	}
	
	@GET
	@Path("/children/{id}")
	@Produces("application/json")
	public List<OrganizationNodeVo> getChindren(@PathParam("id") String id) throws UserPersistException {
		List<Department> deptList = null;
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		deptList = deptMgr.getChildrenById(id);
		return this.mergeDepartmentVoAndUserVo(fillPropertiesToVo(deptList, "task"), new ArrayList<UserVo>());
	}
	
//	@GET
//	@Path("/search_by_condition")
//	@Produces("application/json")
	public List<Department> searchByCondition(String condition) 
									     throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		List<Department> deptList = deptMgr.search(condition);
		
		return deptList;
	}
	
	/**
	 * 适用站点
	 * @param condition
	 * @return
	 * @throws UserPersistException
	 */
	@GET
	@Path("/site_dept")
	@Produces("application/json")
	public List<OrganizationNodeVo> getSiteDepartment(@QueryParam("condition") String condition)
																	throws UserPersistException {
		List<Department> deptList = this.searchByCondition("");
		List<String> siteDeptList = new ArrayList<String>();
		siteDeptList.add("广州站");
		siteDeptList.add("宝安站");
		siteDeptList.add("福山站");
		siteDeptList.add("肇庆站");
		siteDeptList.add("花都站");
		siteDeptList.add("穗东站");
		List<DepartmentVo> deptVoList = fillPropertiesToVo(deptList, "task");
		List<DepartmentVo> removeDeptVoList = new ArrayList<DepartmentVo>();
		for (DepartmentVo deptVo : deptVoList) {
			deptVo.setIo("");
			logger.info("department name is {}", deptVo.getId());
			if (!siteDeptList.contains(deptVo.getId())) {
				removeDeptVoList.add(deptVo);
			}
		}
		deptVoList.removeAll(removeDeptVoList);
		return this.mergeDepartmentVoAndUserVo(deptVoList, new ArrayList<UserVo>());
	}
	
	/**
	 * 适用班组
	 * @return
	 * @throws UserPersistException 
	 */
	@GET
	@Path("/site_team/{id}")
	@Produces("application/json")
	public List<OrganizationNodeVo> getSuitTeam(@Context HttpServletRequest request, 
												@PathParam("id") String id) throws UserPersistException {
		List<Department> deptList = null;
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		deptList = deptMgr.getChildrenById(id);
		List<DepartmentVo> deptVoList = fillPropertiesToVo(deptList, "task");
		List<DepartmentVo> removeDeptVoList = new ArrayList<DepartmentVo>();
		for (DepartmentVo departmentVo : deptVoList) {
			departmentVo.setIo("");
			logger.info("department name is {}", departmentVo.getId());
			if ("物资管理人员".equals(departmentVo.getId())) {
				removeDeptVoList.add(departmentVo);
			}
		}
		deptVoList.removeAll(removeDeptVoList);
		return this.mergeDepartmentVoAndUserVo(deptVoList, new ArrayList<UserVo>());
	}
	
	/**
	 * 消缺班组
	 * @param userId
	 * @return
	 * @throws UserPersistException
	 */
	@GET
	@Path("eliminating_team")
	@Produces("application/json")
	public List<Department> getDepartmentListByUserId() throws UserPersistException {
		List<Department> deptList = null;
		LdapService ldapSvc = this.getLdapService();
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		UserManager userManager = new LdapUserManager(this.getLdapService());
		String currentUser = this.getSecurityService().getCurrentUser();
		List<User> userList = userManager.findByName(currentUser);
		User user = null;
		if (userList != null && userList.size() > 0) {
			user = userList.get(0);
			if (user != null && StringUtils.isNotBlank(user.getDepartmentName())) {
				String departmentName = user.getDepartmentName();
				if (departmentName.indexOf(",") != -1) {
					departmentName = StringUtils.substring(departmentName, 
														   departmentName.indexOf(",") + 1,
														   departmentName.length());
				}
				deptList = deptMgr.getDepartmentListByUserId(departmentName);
			}
		}
		
		return deptList;
	}
	
	@GET
	@Path("/get_children/{id}")
	@Produces("application/json")
	public List<Department> getChindrenById(@PathParam("id") String id) throws UserPersistException {
			List<Department> deptList = null;
			LdapService ldapSvc = this.getLdapService();
			LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
			deptList = deptMgr.getChildrenById(id);
			return deptList;
	}
	
	
	@GET
	@Path("/{id}/children/{roleId}")
	@Produces("application/json")
	public List<OrganizationNodeVo> getChildrenNodesByDepartmentIdAndRoleId(@Context HttpServletRequest request, 
																			@PathParam("id") String id,
																			@PathParam("roleId") String roleId)
																	   throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
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
			orgNodeVo.setId(userVo.getCheckName());
			orgNodeVo.setCheckName(userVo.getCheckName());
			orgNodeVo.setIo(userVo.getId());
			for(String id:userId){
				if(id.equals(userVo.getId())){
					orgNodeVo.setChecked(true);
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
		if (StringUtils.isBlank(dept.getParent())) {
			deptVo.setDeptFullPath("o=" + dept.getId());
		} else {
			deptVo.setDeptFullPath("ou=" + dept.getId() + "," + dept.getParent());
		}
		if (StringUtils.isBlank(deptVo.getDeptFullPath())) {
			deptVo.setIo("/rest/depts/root/children");
		} else {
			deptVo.setIo("/rest/depts/" + deptVo.getDeptFullPath() + "/children/"+roleId);
		}
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
	
	@GET
	@Path("/{id}/children/for_user_combobox_dept_not_combobox")
	@Produces("application/json")
	public List<OrganizationNodeVo> getChildrenByIdForUserComboboxDeptNotCombobox(
													@Context HttpServletRequest request,
													@PathParam("id") String id)
											   throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		List<DepartmentVo> deptVoList = null;
		List<UserVo> userVoList = null;
		String type = request.getParameter("type");
		deptVoList = DepartmentService.fillPropertiesToVo(deptMgr.getChildrenById(id));
		for (DepartmentVo deptVo : deptVoList) {
			deptVo.setIo("/rest/depts/" + deptVo.getDeptFullPath() + "/children/for_user_combobox_dept_not_combobox?type=task");
		}
		userVoList = UserService.fillUserListPropertiesToVo(userMgr.findByDepartmentId(id), type);
		List<OrganizationNodeVo> orgList = this.mergeDepartmentVoAndUserVo(deptVoList, userVoList);
		
		return orgList;
	}
	
	@GET
	@Path("/{id}/children/for_dept_combobox_user_not_combobox")
	@Produces("application/json")
	public List<OrganizationNodeVo> getChildrenByIdForDeptComboboxUserNotCombobox(
													@Context HttpServletRequest request,
													@PathParam("id") String id)
											   throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		List<DepartmentVo> deptVoList = null;
		List<UserVo> userVoList = null;
		String type = request.getParameter("type");
		deptVoList = DepartmentService.fillPropertiesToVo(deptMgr.getChildrenById(id), type);
		for (DepartmentVo deptVo : deptVoList) {
			deptVo.setIo("/rest/depts/" +  deptVo.getDeptFullPath() + "/children/for_dept_combobox_user_not_combobox?type=task");
		}
		userVoList = UserService.fillUserListPropertiesToVo(userMgr.findByDepartmentId(id));
		List<OrganizationNodeVo> orgList = this.mergeDepartmentVoAndUserVo(deptVoList, userVoList);
		
		return orgList;
	}
	
	
	
	@GET
	@Path("/{id}/children/for_user_combobox_dept_not_combobox")
	@Produces("application/json")
	public List<OrganizationNodeVo> getChildrenByIdForUserComboboxDeptNotCombobox(
													@PathParam("id") String id)
											   throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		List<DepartmentVo> deptVoList = null;
		List<UserVo> userVoList = null;
		deptVoList = DepartmentService.fillPropertiesToVo(deptMgr.getChildrenById(id));
		userVoList = UserService.fillUserListPropertiesToVo(userMgr.findByDepartmentId(id), "task");
		List<OrganizationNodeVo> orgList = this.mergeDepartmentVoAndUserVo(deptVoList, userVoList);
		
		return orgList;
	}
	
	@GET
	@Path("/{id}/children/for_dept_combobox_user_not_combobox")
	@Produces("application/json")
	public List<OrganizationNodeVo> getChildrenByIdForDeptComboboxUserNotCombobox(
													@PathParam("id") String id)
											   throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		
		LdapDepartmentManager deptMgr = new LdapDepartmentManager(ldapSvc);
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		List<DepartmentVo> deptVoList = null;
		List<UserVo> userVoList = null;
		deptVoList = DepartmentService.fillPropertiesToVo(deptMgr.getChildrenById(id), "task");
		userVoList = UserService.fillUserListPropertiesToVo(userMgr.findByDepartmentId(id));
		List<OrganizationNodeVo> orgList = this.mergeDepartmentVoAndUserVo(deptVoList, userVoList);
		
		return orgList;
	}

}
