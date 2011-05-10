package com.zyeeda.framework.ws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.managers.internal.LdapDepartmentManager;
import com.zyeeda.framework.managers.internal.LdapUserManager;
import com.zyeeda.framework.viewmodels.UserVo;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/users")
public class UserService extends ResourceService {
	
	private static final Logger logger = LoggerFactory.getLogger(LdapDepartmentManager.class);

	public UserService(@Context ServletContext ctx) {
		super(ctx);
	}
	
	@POST
	@Path("/{parent}")
	@Produces("application/json")
	public UserVo createUser(@FormParam("") User user, @PathParam("parent") String parent) throws NamingException {
		logger.debug("=====================create method==========================");
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		user.setDepartmentName(parent);
		return userMgr.persist(user);
	}
	
	@DELETE
	@Path("/{id}")
	public void removeUser(@PathParam("id") String id) throws NamingException {
		logger.debug("==================== remove the mothod =======================");
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		userMgr.remove(id);
	}
	
	@PUT
	@Path("/{id}")
	@Produces("application/json")
	public UserVo editUser(@FormParam("") User user, @PathParam("id") String id) throws NamingException, ParseException {
		// 传入参数类似uid=XXX,ou=YYY,o=广州局
		logger.debug("==================== edit the mothod =======================");
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		user.setId(id);
		return userMgr.update(user);
	}
	
	@GET
	@Path("/{id}")
	@Produces("application/json")
	public User getUserById(@PathParam("id") String id) throws NamingException, ParseException {
		// 传入参数类似uid=XXX,ou=YYY,o=广州局
		logger.debug("==================== getUserById the mothod =======================");
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		return userMgr.findById(id);
	}
	
	@GET
	@Path("/search/{name}")
	@Produces("application/json")
	public List<UserVo> getUserListByName(@PathParam("name") String name) throws NamingException {
		// 传入参数：名称
		logger.debug("==================== getUserListByName the mothod =======================");
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		return userMgr.getUserListByName(name);
	}
	
	@GET
	@Path("/userList/{deptId}")
	@Produces("application/json")
	public List<UserVo> getUserListByDepartmentId(@PathParam("deptId") String deptId) throws NamingException {
		// 传入参数类似ou=YYY,o=广州局
		logger.debug("==================== getUserListByDepartmentId the mothod =======================");
		LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		return userMgr.getUserListByDepartmentId(deptId);
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
	  
	    byte[] bytes = baos.toByteArray();
	    LdapService ldapSvc = this.getLdapService();
		LdapUserManager userMgr = new LdapUserManager(ldapSvc);
		User user = new User();
		user.setId("china");
//		user.setPhoto(bytes);
		
		userMgr.update(user);
	}
	
	
}
