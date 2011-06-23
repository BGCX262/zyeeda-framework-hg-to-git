package com.zyeeda.framework.ws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
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

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.entities.Account;
import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.helpers.AccountHelper;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.ldap.SearchControlsFactory;
import com.zyeeda.framework.managers.AccountManager;
import com.zyeeda.framework.managers.UserPersistException;
import com.zyeeda.framework.managers.internal.LdapUserManager;
import com.zyeeda.framework.managers.internal.SystemAccountManager;
import com.zyeeda.framework.sync.UserSyncService;
import com.zyeeda.framework.utils.LdapEncryptUtils;
import com.zyeeda.framework.viewmodels.AccountVo;
import com.zyeeda.framework.viewmodels.UserVo;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/users")
public class UserService extends ResourceService {
	
	private Logger logger = LoggerFactory.getLogger(UserService.class);
	
	public UserService(@Context ServletContext ctx) {
		super(ctx);
	}
	
	private static String createUserDn(String parent, String id) {
		return "uid=" + id + "," + parent;
	}
	
	@POST
	@Path("/{parent:.*}")
	@Produces("application/json")
	public User persist(@FormParam("") User user, @PathParam("parent") String parent)
				   											throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		UserSyncService userSyncService = this.getUserSynchService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		List<User> userList = userMgr.findByName(user.getId());
		if (userList != null && userList.size() > 0) {
			throw new RuntimeException("账号不能重复");
		} else {
			user.setDepartmentName(parent);
			user.setDeptFullPath(createUserDn(parent, user.getId()));
			userMgr.persist(user);
			user = userMgr.findById(user.getDeptFullPath());
			userSyncService.persist(user);
			return user;
		}
	}
	
	@DELETE
	@Path("/{id}")
	public String remove(@PathParam("id") String id,
						 @PathParam("cascade") Boolean cascade)
					throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		if (cascade != null) {
			userMgr.remove(id);
			return "{success: 'true'}";
		} else {
			Integer count = userMgr.getChildrenCountById(id, "(objectclass=*)");
			if (count > 0) {
				return "{\"success\": \"false\"}";
			} else {
				userMgr.remove(id);
				return "{\"success\": \"true\"}";
			}
		}
	}
	
	@PUT
	@Path("/{id}")
	@Produces("application/json")
	public User update(@FormParam("") User user, @PathParam("id") String id) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		UserSyncService userSyncService = this.getUserSynchService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		String uid = id.substring(id.indexOf("=") + 1, id.indexOf(","));
		if (!uid.equals(user.getId())) {
			throw new RuntimeException("不能修改账号");
		} else {
			user.setDeptFullPath(id);
			userMgr.update(user);
			user = userMgr.findById(id);
			userSyncService.update(user);
			return user;
		}
	}
	
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public User findById(@PathParam("id") String id) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		return userMgr.findById(id);
	}
	
	@GET
	@Path("/search/{name}")
	@Produces("application/json")
	public List<UserVo> getUserListByName(@PathParam("name") String name) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		return UserService.fillUserListPropertiesToVo(userMgr.findByName(name));
	}
	
	@GET
	@Path("/userList/{deptId}")
	@Produces("application/json")
	public List<UserVo> getUserListByDepartmentId(@PathParam("deptId") String deptId) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		return UserService.fillUserListPropertiesToVo(userMgr.findByDepartmentId(deptId));
	}
	
	@PUT
	@Path("/{id}/update_password")
	@Produces("application/json")
	public User updatePassword(@PathParam("id") String id, @FormParam("oldPassword") String oldPassword,
			@FormParam("newPassword") String newPassword)  throws UserPersistException, UnsupportedEncodingException {
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		User u = userMgr.findById(id);
		System.out.println("-----------oldPwd:" + u.getPassword());
		System.out.println("-----------oldPwd:" + oldPassword);
		System.out.println("-----------oldPwd:" + LdapEncryptUtils.md5Encode(oldPassword));
		if (LdapEncryptUtils.md5Encode(oldPassword).equals(u.getPassword())) {
			if (!LdapEncryptUtils.md5Encode(newPassword).equals(u.getPassword())) {
				userMgr.updatePassword(id, LdapEncryptUtils.md5Encode(newPassword));
			}
		} else {
			throw new RuntimeException("旧密码输入错误");
		}
		return userMgr.findById(id);
	}
	
	@PUT
	@Path("/{id}/enable")
	@Produces("application/json")
	public User enable(@PathParam("id") String id, @FormParam("status") Boolean visible)
			throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		UserSyncService userSyncService = this.getUserSynchService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		userMgr.enable(id);
		userSyncService.enable(id);
		return userMgr.findById(id);
	}
	
	@PUT
	@Path("/{id}/unenable")
	@Produces("application/json")
	public User disable(@PathParam("id") String id, @FormParam("status") Boolean visible)
	 		throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		UserSyncService userSyncService = this.getUserSynchService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		
		userMgr.disable(id);
		userSyncService.disable(id);
		return userMgr.findById(id);
	}
	
	@POST
	@Path("/{id}")
	@Produces("application/json")
	public void uploadPhoto(@Context HttpServletRequest request, @PathParam("id") String id) throws Throwable {
		InputStream in = request.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    byte[] b = new byte[1024];  
	    int len = 0;  
	  
	    while ((len = in.read(b, 0, 1024)) != -1) {  
	        baos.write(b, 0, len);  
	    }  
	    baos.flush();
	  
//	    byte[] bytes = baos.toByteArray();
	    LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		User user = new User();
		user.setId("china");
//		user.setPhoto(bytes);
		
		userMgr.update(user);
	}
	
	public static UserVo fillUserPropertiesToVo(User user) {
		UserVo userVo = new UserVo();

		userVo.setId(user.getId());
		userVo.setType("io");
		userVo.setLabel("<a>" + user.getId() + "<a>");
		userVo.setCheckName(user.getId());
		userVo.setLeaf(true);
		userVo.setUid(user.getId());
		userVo.setDeptFullPath(user.getDeptFullPath());
		userVo.setKind("user");

		return userVo;
	}
	
	public static UserVo fillUserPropertiesToVo(User user, String type) {
		UserVo userVo = new UserVo();

		userVo.setId(user.getId());
		userVo.setType(type);
		userVo.setLabel( user.getId());
		userVo.setCheckName(user.getId());
		userVo.setLeaf(true);
		userVo.setUid(user.getId());
		userVo.setDeptFullPath(user.getDeptFullPath());
		userVo.setKind("user");

		return userVo;
	}

	public static List<UserVo> fillUserListPropertiesToVo(List<User> userList) {
		List<UserVo> userVoList = new ArrayList<UserVo>(userList.size());
		UserVo userVo = null;
		for (User user : userList) {
			userVo = UserService.fillUserPropertiesToVo(user);
			userVoList.add(userVo);
		}
		return userVoList;
	}

	public static List<UserVo> fillUserListPropertiesToVo(List<User> userList, String type) {
		List<UserVo> userVoList = new ArrayList<UserVo>(userList.size());
		UserVo userVo = null;
		for (User user : userList) {
			userVo = UserService.fillUserPropertiesToVo(user, type);
			userVoList.add(userVo);
		}
		return userVoList;
	}
	
//	@POST
//	@Path("/{id}")
//	@Produces("application/json")
//	public Account updateAccount(@FormParam("") Account objAccount, @PathParam("id") String fullPath){
//		if(objAccount == null){
//			throw new RuntimeException("用户名或密码不能为空");
//		}else{
//			objAccount.setUserFullPath(fullPath);
//		}
//		return objAccount;
//	}	
	
	/**
	 * 配置系统信息
	 * 将旧的数据删除，保存新的数据。
	 * Json list
	 * return  userList
	 */
	@POST
	@Path("/accounts/{id}")
	@Produces("application/json")
	public List<Account> updateAccounts(@FormParam("userList") String userListJson, @PathParam("id") String id) throws UserPersistException {
		LdapService ldapSvc = this.getLdapService();
		AccountManager objAccountManager = new SystemAccountManager(ldapSvc);
		ObjectMapper mapper = new ObjectMapper();
		List<Account> userList = null;
		try {
			userList = mapper.readValue(userListJson,
					new TypeReference<List<Account>>() {
					});
			List<Account> tempAccountList = new ArrayList<Account>();
			LdapContext ctx  = ldapSvc.getLdapContext();
			NamingEnumeration<SearchResult> ns = ctx.search(id, "objectclass=*", SearchControlsFactory.getDefaultSearchControls());
			while (ns.hasMore()) {
				Account  ac = AccountHelper.convertAttributesToAccount(ns.next().getAttributes());
				tempAccountList.add(ac);
			}
			for (Account account : tempAccountList) {
				account.setUserFullPath(id);
				objAccountManager.remove("username=" + account.getUserName() + "," + id);
			}
			for (Account account : userList) {
				account.setUserFullPath(id);
//				objAccountManager.remove("username=" + account.getUserName() + "," + id);
				objAccountManager.update(account);
			}
//			logger.debug("UserList size is {}", userList.size());
		} catch (JsonParseException e) {
			logger.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (NamingException e) {
			logger.error(e.getMessage(), e);
		}
		return userList;
	}
//	objAccountManager.remove(account.getSystemName());
//	Account newAccount = objAccountManager.findByUserIdAndSystemName(id, account.getSystemName());
//	newAccount.setUserName(account.getUserName());
//	newAccount.setPassword(account.getPassword());
//	newAccount.setVisible(account.getVisible());
//	objAccountManager.update(newAccount);
//	account.setUserFullPath(id);
//	objAccountManager.remove(account.getSystemName());//.findByUserIdAndSystemName(id, account.getSystemName());
////	newAccount.setUserName(account.getUserName());
////	newAccount.setPassword(account.getPassword());
////	newAccount.setVisible(account.getVisible());
////	newAccount.setUserFullPath(id);

	/**
	 * get account list
	 */
	@GET
	@Path("/accounts/{id}")
	@Produces("application/json")
	public AccountVo getAccounts(@PathParam("id") String id) throws UserPersistException{
		LdapService ldapSvc = this.getLdapService();
		AccountManager objAccountManager = new SystemAccountManager(ldapSvc);
		List<Account> list = objAccountManager.findByUserId(id);
		AccountVo avo = new AccountVo();
		avo.setAccounts(list);
		return avo;
	}
	
//	public void removeSysConfigure(String systemName) throws UserPersistException{
//		LdapService ldapSvc = this.getLdapService();
//		AccountManager objAccountManager = new SystemAccountManager(ldapSvc);
//		objAccountManager.remove(systemName);
//	}
	
	/*
	@POST
	@Path("/accounts/{id}")
	@Produces("application/json")
	public Account updateAccounts(@FormParam("") Account objAccount, @PathParam("id") String id) throws UserPersistException{
		LdapService ldapSvc = this.getLdapService();
		AccountManager objAccountManager = new SystemAccountManager(ldapSvc);

		if(objAccount == null){
			throw new RuntimeException("用户名或密码为空");
		} else {
			objAccount.setUserFullPath("username=" + objAccount.getUserName() + "," + id);
			objAccountManager.update(objAccount);
			return objAccount;
		}
	}
	*/
	
	@GET
	@Path("/accounts/mock")
	@Produces("application/json")
	public Account mockSignIn(@QueryParam("uid") String uid,@QueryParam("systemName") String systemName) throws UserPersistException{
		LdapService ldapSvc = this.getLdapService();
		AccountManager objAccountManager = new SystemAccountManager(ldapSvc);	
		return objAccountManager.findByUserIdAndSystemName(uid, systemName);
	}
}
