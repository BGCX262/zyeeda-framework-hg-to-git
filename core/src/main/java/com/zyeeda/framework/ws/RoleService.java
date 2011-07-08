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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.zyeeda.framework.entities.Role;
import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.managers.PermissionManager;
import com.zyeeda.framework.managers.RoleManager;
import com.zyeeda.framework.managers.UserManager;
import com.zyeeda.framework.managers.UserPersistException;
import com.zyeeda.framework.managers.internal.DefaultPermissionManager;
import com.zyeeda.framework.managers.internal.DefaultRoleManager;
import com.zyeeda.framework.managers.internal.DefaultUserManager;
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
	//private final static String ROAM_PERMISSION_FILE = "roamPermission.xml";

	private final static String PERMISSION_FILE = "permission.xml";
	public RoleService(@Context ServletContext ctx) {
		super(ctx);
	}
	
	@GET
	@Path("/get_role_with_out_dept")
	@Produces("application/json")
	public List<Role> getRoleWithOutDept(){
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		Search search = new Search();
		search.addFilterEmpty("deptepmentId");
		search.addFilterNull("deptepmentId");
		List<Role> list = roleMgr.search(search);
		return list;
	}
	
	
	
	@GET
	@Path("/{id}/get_role")
	@Produces("application/json")
	public Role getOneRolesById(@PathParam("id") String id) {
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		return roleMgr.find(id);
	}
	
	@GET
	@Path("/getAllRoles")//TODO;
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
	
	@DELETE
	@Path("/defect_zizhi")
	@Produces("application/json")
	public List<Role>  getRolesReturn(@QueryParam("ids") String ids ){
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
			if(roleMgr != null){
				String[] id=ids.split(";");
				for(String sigleId:id){
					roleMgr.removeById(sigleId);
				}
			}
		return roleMgr.findAll();
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
	@Path("/{id}/zizhi_edite")
	@Produces("application/json")
	public List<Role> editeRoleReturnAll(@PathParam("id") String id, @FormParam("") Role role){
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		Role newRole=roleMgr.find(id);
		newRole.setName(role.getName());
		newRole.setDescription(role.getDescription());
		this.getPersistenceService().getCurrentSession().flush();
		return roleMgr.findAll();
	}
	
	@POST
	@Path("/")
	@Produces("application/json")
	public Role creatRole(@FormParam("") Role role) {
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		roleMgr.persist(role);
		this.getPersistenceService().getCurrentSession().flush();
		return roleMgr.find(role.getId());
		
	}

	
	@POST
	@Path("/return_all")
	@Produces("application/json")
	public List<Role> creatRoleReturnAll(@FormParam("") Role role) {
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		
		String name = role.getName();
		Search search = new Search();
		search.addFilterEqual("name", name);
		search.addFilterEqual("deptepment", role.getDeptepment());
		List<Role> list = roleMgr.search(search);
		if (list.size() > 0) {
			return null;
		}else{
			roleMgr.persist(role);
			this.getPersistenceService().getCurrentSession().flush();
			return roleMgr.findAll();
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
		Role newRole = roleMgr.find(id);
		String authArray =  role.getPermissions();
		String auth = newRole.getPermissions();
		if(StringUtils.isBlank(auth)){
			newRole.setPermissions(authArray); 
		} else {
			int flog = auth.indexOf("&");
			if(flog >= 0){
				String menuAuth = auth.substring(flog + 1, auth.length());
				String menuPermission = authArray + "&" + menuAuth;
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
		Role newRole = roleMgr.find(id);
		String authArray =  role.getRamoPermissions();
		logger.debug("this ramoPermissions is : {}", role.getRamoPermissions());
		String auth = newRole.getPermissions();
		if(StringUtils.isBlank(auth)) {
			String menuPermission =   "&" + authArray;
			newRole.setPermissions(menuPermission);
		} else {
			int flog = auth.indexOf("&");
			if(flog >= 0){
				String menuAuth = auth.substring(0, flog);
				String menuPermission =  menuAuth+ "&" + authArray;
				newRole.setPermissions(menuPermission); 
			} else {
				String  menuPermission = auth + "&" + authArray;
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
		String creator = this.getSecurityService().getCurrentUser();
		UserManager userManager = new LdapUserManager(this.getLdapService());
		String subStationName = userManager.findStationDivisionByCreator(creator);
		Set<UserVo> userNameVoList = new HashSet<UserVo>();
		Search search = new Search();
		search.addFilterEqual("name", "当班值-值长");
		search.addFilterEqual("deptepment", subStationName);
		List<Role> roleList = roleMgr.search(search);
		System.out.println("this role size is :" + roleList.size());
		for(Role role : roleList){
			System.out.println("this role name and depet is :" + role.getDeptepment() + role.getName());
			if(role.getName() != null && role.getDeptepment() != null && subStationName != null) {
				if("当班值-值长".equals(role.getName()) && role.getDeptepment().equals(subStationName)){
					for(String user : role.getSubjects()){
						UserManager userMgr = new DefaultUserManager(this.getPersistenceService());
						User userId = userMgr.findById(user);
						UserVo userVo = new UserVo();
						userVo.setCheckName(userId.getUsername());
						userVo.setLabel(userId.getUsername());
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
		}
		return userNameVoList;
	}
	
	@GET 
	@Path("/get_roles_not_subuser")
	@Produces("application/json")
	public Set<UserVo>  getAllNotSubUser() throws UserPersistException{
		RoleManager roleMgr = new DefaultRoleManager(this.getPersistenceService());
		String creator = this.getSecurityService().getCurrentUser();
		UserManager userManager = new LdapUserManager(this.getLdapService());
		String subStationName = userManager.findStationDivisionByCreator(creator);
		logger.info("this substationName value is : ", subStationName);
		Set<UserVo> userNameVoList = new HashSet<UserVo>();
		Search search = new Search();
		search.addFilterOr(Filter.and(Filter.equal("name", "当班值-值班员"), Filter.equal("deptepment", subStationName)), Filter.and(Filter.equal("deptepment", subStationName), Filter.equal("name", "当班值-值长")));
		List<Role> roleList = roleMgr.search(search);
		System.out.println("this role size is :" + roleList.size());
		
		for(Role role : roleList){
			System.out.println("this role name and depet is :" + role.getDeptepment() + "*******" + role.getName());
			if(role.getName() != null && role.getDeptepment() != null && subStationName != null) {
				if(("当班值-值班员".equals(role.getName()) || "当班值-值长".equals(role.getName())) && subStationName.equals(role.getDeptepment())){
					for(String user : role.getSubjects()) {
						System.out.println("**************1111**********" + user);
						UserManager userMgr = new DefaultUserManager(this.getPersistenceService());
						User userId = userMgr.findById(user);
						UserVo userVo = new UserVo();
						userVo.setCheckName(userId.getUsername());
						userVo.setLabel(userId.getUsername());
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
		}
		return userNameVoList;
	}
	
	@GET
	@Path("/depts/{id}")
	@Produces("application/json")
	public List<UserVo> getUserVo(@PathParam("id") String id) throws UserPersistException {
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
