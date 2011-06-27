package com.zyeeda.framework.ws;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
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
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.genericdao.search.Search;
import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.managers.PermissionManager;
import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.managers.UserManager;
import com.zyeeda.framework.managers.UserPersistException;
import com.zyeeda.framework.managers.internal.DefaultPermissionManager;
import com.zyeeda.framework.managers.internal.DefaultRoleManager;
import com.zyeeda.framework.managers.internal.LdapUserManager;
import com.zyeeda.framework.viewmodels.PermissionVo;
import com.zyeeda.framework.viewmodels.RoleVo;
import com.zyeeda.framework.viewmodels.RoleWithUserVo;
import com.zyeeda.framework.viewmodels.UserNameVo;
import com.zyeeda.framework.viewmodels.UserVo;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/roles")
public class RoleService extends ResourceService{
	
	private static final Logger logger = LoggerFactory.getLogger(RoleService.class);
	private final static String ROAM_PERMISSION_FILE = "roamPermission.xml";

	private final static String PERMISSION_FILE = "permission.xml";
	public RoleService(@Context ServletContext ctx) {
		super(ctx);
	}
	
	@GET
	@Path("/{id}/get_role")
	@Produces("application/json")
	public Role getOneRolesById(@PathParam("id") String id) {
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		return roleMgr.find(id);
	}
	
	@GET
	@Path("/getAllRoles")
	@Produces("application/json")
	public List<Role> getRoles() {
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		logger.debug("this get all roles is success!");
		return roleMgr.findAll();
	}
	

	
	@DELETE
	@Path("/")
	@Produces("application/json")
	public boolean  getRoles(@QueryParam("ids") String ids ){
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		boolean bool = true;
		try {
			if(roleMgr != null){
				String[] id=ids.split(";");
				for(String sigleId:id){
					roleMgr.removeById(sigleId);
				}
			}
		} catch (Exception e) {
			bool = false;
		}
		return bool;
	}
	
	@GET
	@Path("/{id}/role_and_auth")
	@Produces("application/json")
	public RoleWithUserVo getRolesById(@PathParam("id") String id) throws XPathExpressionException, IOException {
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		Role role = roleMgr.find(id);
		logger.debug("this role's permission is " + role.getPermissions());
		PermissionManager permissionMgr = new DefaultPermissionManager();
		RoleWithUserVo roleWithUserVo = new RoleWithUserVo();
		for(String userName:role.getSubjects()){
			UserNameVo userVo = new UserNameVo();
			userVo.setUserName(userName);
			roleWithUserVo.getUserName().add(userVo);
		}
		for(String auth:role.getPermissionList()){
			PermissionVo permission = new PermissionVo();
		    permission = permissionMgr.getPermissionByPath(auth, PERMISSION_FILE);
			roleWithUserVo.getPermission().add(permission);
		}
		return roleWithUserVo;
	}
	
	@POST
	@Path("/{id}/edite")
	@Produces("application/json")
	public Role editeRole(@PathParam("id") String id, @FormParam("") Role role){
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		Role newRole=roleMgr.find(id);
		newRole.setName(role.getName());
		newRole.setDescription(role.getDescription());
		this.getPersistenceService().getCurrentSession().flush();
		return roleMgr.find(id);
	}
	
	@POST
	@Path("/")
	@Produces("application/json")
	public Role creatRole(@FormParam("") Role role) {
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		String name = role.getName();
		Search search = new Search();
		search.addFilterEqual("name", name);
		List<Role> list = roleMgr.search(search);
		if (list.size() > 0) {
			return null;
		}else{
			roleMgr.persist(role);
			this.getPersistenceService().getCurrentSession().flush();
			return roleMgr.find(role.getId());
		}
	}

	@PUT
	@Path("/")
	@Produces("application/json")
	public Role editRole(@FormParam("") Role role) {
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		Role setRole=roleMgr.find(role.getId());
		Role newRole=roleMgr.save(setRole);
		this.getPersistenceService().getCurrentSession().flush();
		return newRole;
	}

	@POST
	@Path("/{id}/assign_user")
	@Produces("application/json")
	public void assignRoleUser(@PathParam("id") String id,@QueryParam("ids") String ids) {
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		Role role = roleMgr.find(id);
		role.getSubjects().clear();
		if(StringUtils.isNotBlank(ids)){
			String[] usersName = ids.split(",");
			for (int i = 0; i < usersName.length; i++) {
				role.getSubjects().add(usersName[i]);
			}
		}
		this.getPersistenceService().getCurrentSession().flush();
	}
	
	@GET
	@Path("/{id}/sub_user")
	@Produces("application/json")
	public List<String> getUserByRoleId(@PathParam("id") String id){
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		Role role = roleMgr.find(id);
		Set<String> user = role.getSubjects();
		List<String>  list = new ArrayList<String>();
		for(String userId:user) {
			list.add(userId);
		}
		return list;
	}

	@POST
	@Path("/{id}/assign_auth")
	@Produces("application/json")
	public Role assignRoleAuth(@PathParam("id") String id,
			@FormParam("") Role role) throws XPathExpressionException, IOException {
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		PermissionManager permissionMgr = new DefaultPermissionManager();
		Role newRole = roleMgr.find(id);
		String[] str = role.getPermissions().split(";");
		List<String> authList = CollectionUtils.asList(str);
		String authArray = permissionMgr.getParentPermissionListAuthByList(authList, PERMISSION_FILE);
		String auth = newRole.getPermissions();
		if(StringUtils.isBlank(auth)){
			newRole.setPermissions(authArray); 
		} else {
			int flog = auth.indexOf("_");
			if(flog >= 0){
				String menuAuth = auth.substring(flog + 1, auth.length());
				String menuPermission = authArray + "_" + menuAuth;
				newRole.setPermissions(menuPermission); 
			} else {
				newRole.setPermissions(authArray); 
			}
		}
		this.getPersistenceService().getCurrentSession().flush();
		return newRole;
	}
	
	@POST
	@Path("/{id}/assign_raom_auth")
	@Produces("application/json")
	public Role assignroamRoleAuth(@PathParam("id") String id,
			@FormParam("") Role role) throws XPathExpressionException, IOException {
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		PermissionManager permissionMgr = new DefaultPermissionManager();
		Role newRole = roleMgr.find(id);
		logger.debug("this ramoPermissions is : {}", role.getRamoPermissions());
		String[] str = role.getRamoPermissions().split(";");
		List<String> authList = CollectionUtils.asList(str);
		String authArray = permissionMgr.getParentPermissionListAuthByList(authList,ROAM_PERMISSION_FILE);
		String auth = newRole.getPermissions();
		if(StringUtils.isBlank(auth)) {
			String menuPermission =   "_" + authArray;
			newRole.setPermissions(menuPermission);
		} else {
			int flog = auth.indexOf("_");
			if(flog >= 0){
				String menuAuth = auth.substring(0, flog);
				String menuPermission =  menuAuth+ "_" + authArray;
				newRole.setPermissions(menuPermission); 
			} else {
				String  menuPermission = auth + "_" + authArray;
				logger.debug("this ramoPermissions is : {}", menuPermission);
				newRole.setPermissions(menuPermission); 
			} 
		}
		this.getPersistenceService().getCurrentSession().flush();
		return newRole;
	}
	
	@POST
	@Path("/{id}/remove_auth")
	@Produces("application/json")
	public Role removeAuth(@PathParam("id") String id, @QueryParam("permission") String permission) throws XPathExpressionException, IOException{
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		Role role = roleMgr.find(id);
		String[] permissions = permission.split(";");
		List<String> permissionSet = role.getPermissionList();
		for(String havaermissions : permissions){
			if (permissionSet.contains(havaermissions)) {
				permissionSet.remove(havaermissions);
			}
		}
		String utils = StringUtils.join(permissionSet, ";");
		role.setPermissions(utils);
		this.getPersistenceService().getCurrentSession().flush();
		return role;
		
	}

	
	@POST
	@Path("/{id}/remove_user")
	@Produces("application/json")
	public boolean removeUser(@PathParam("id") String id, @QueryParam("subject") String subject) throws XPathExpressionException, IOException{
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		Role role = roleMgr.find(id);
		String[] subjects = subject.split(";");
		boolean result = false;
		for(String subjectsSub:subjects) {
			for(String auth:role.getSubjects()) {
				if(auth.equals(subjectsSub)) {
					result = true;
					logger.debug("this role's subject remove is success" );
					break;
				}
			}
			if(result) {
				role.getSubjects().remove(subjectsSub);
			}
		}
		this.getPersistenceService().getCurrentSession().flush();
//		PermissionManager permissionMgr = new DefaultPermissionManager();
//		RoleWithUserVo roleWithUserVo = new RoleWithUserVo();
//		for(String userName:role.getSubjects()){
//			UserNameVo userVo = new UserNameVo();
//			userVo.setUserName(userName);
//			roleWithUserVo.getUserName().add(userVo);
//		}
//		for(String auth:role.getPermissionList()){
//			PermissionVo permission = new PermissionVo();
//		    permission = permissionMgr.getPermissionByPath(auth);
//			roleWithUserVo.getPermission().add(permission);
//		}
		return result;
	}
	
	@GET 
	@Path("/get_roles_subuser")
	@Produces("application/json")
	public Set<UserVo>  getAllSubUser() throws UserPersistException{
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		String userName = this.getSecurityService().getCurrentUser();
		List<Role> roleList = roleMgr.getRoleBySubject(userName);
		String creator = this.getSecurityService().getCurrentUser();
		UserManager userManager = new LdapUserManager(this.getLdapService());
		List<User> userList = userManager.findByName(creator);
		List<String> siteDeptList = new ArrayList<String>();
		siteDeptList.add("广州站");
		siteDeptList.add("宝安站");
		siteDeptList.add("福山站");
		siteDeptList.add("肇庆站");
		siteDeptList.add("花都站");
		siteDeptList.add("隧东站");
		String subStationName = null;
		if (userList != null && userList.size() > 0) {
			if (StringUtils.isNotBlank(userList.get(0).getDeptFullPath())) {
				String fullPath = userList.get(0).getDeptFullPath();
				String[] spilt = StringUtils.split(fullPath, ",");
				for (int i = 0; i < spilt.length; i++) {
					if (spilt[i].indexOf("=") != -1) {
						spilt[i] = StringUtils.substring(spilt[i], spilt[i]
								.indexOf("=") + 1, spilt[i].length());
						if (siteDeptList.contains(spilt[i])) {
							subStationName = spilt[i];
							break;
						}
					}
				}
			}
		}
		
		//List<String> subjectList = new ArrayList<String>();
		Set<UserVo> userNameVoList = new HashSet<UserVo>();
		for(Role role : roleList){
			if("当班-值长".equals(role.getName()) && role.getDeptepment().equals(subStationName)){
				for(String user : role.getSubjects()){
					UserVo userVo = new UserVo();
					userVo.setCheckName(user);
					userVo.setLabel(user);
					userVo.setType("task");
					userVo.setLeaf(true);
					if(userNameVoList.size() == 0) {
						userNameVoList.add(userVo);
						continue;
					}
					for(UserVo userNameVo : userNameVoList){
						if(!(userNameVo.getCheckName().equals(user))){
							userNameVoList.add(userVo);
							break;
						}
					}
				}
			}
		}
		return userNameVoList;
	}
	
	
	@GET
	@Path("/depts/{id}")
	@Produces("application/json")
	public List<UserVo> getUserVo(@PathParam("id") String id) {
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		Role role = roleMgr.find(id);
		List<UserVo> listUserVo = roleMgr.getUserVoByRole(role);
		return listUserVo;
	}
	

	@GET
	@Path("/get_all_roles_vo")
	@Produces("application/json")
	public List<RoleVo> getRolesVo() {
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		String hql = "select distinct F_DEPTEMENT_ID, F_DEPTEPMENT from sys_role";
		List<Role> listRole = new ArrayList<Role>();
	    listRole = roleMgr.getRoleDistinct(hql);
		logger.debug("this get all roles is success!", listRole.size());
		List<RoleVo> roleVo = roleMgr.deptToVo(listRole);
		return roleVo;
	}
	
	@GET
	@Path("/dept_and_role/{deptId}")
	@Produces("application/json")
	public List<RoleVo>  getDeptById(@PathParam("deptId") String deptId){
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		List<Role> list = new ArrayList<Role>();
		Search search = new Search();
		search.addFilterEqual("deptepmentId", deptId);
		list = roleMgr.search(search);
		List<RoleVo> roleVo = roleMgr.roleToVo(list);
		return roleVo;
	}
	
}
