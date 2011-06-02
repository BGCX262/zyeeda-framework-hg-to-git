package com.zyeeda.framework.ws;

import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.managers.UserPersistException;
import com.zyeeda.framework.managers.internal.DefaultUserManager;
import com.zyeeda.framework.ws.base.ResourceService;

@Path("/sync")
public class DefaultUserService extends ResourceService {

	public DefaultUserService(@Context ServletContext ctx) {
		super(ctx);
	}

	@POST
	@Path("/persist")
	@Produces("application/json")
	public User createUser(@FormParam("") User user) throws UserPersistException {
		DefaultUserManager userMgr = new DefaultUserManager(this.getPersistenceService());
		if (userMgr.findById(user.getId()) != null) {
			throw new RuntimeException("账号不能重复");
		} else {
			userMgr.persist(user);
			return userMgr.findById(user.getId());
		}
	}

	@PUT
	@Path("/update")
	@Produces("application/json")
	public User editUser(@FormParam("") User user) throws UserPersistException {
		DefaultUserManager userMg = new DefaultUserManager(this
				.getPersistenceService());

		User u = userMg.find(user.getId());
		u.setUsername(user.getUsername());
		u.setEmail(user.getEmail());
		u.setBirthday(user.getBirthday());
		u.setDateOfWork(user.getDateOfWork());
		u.setDegree(user.getDegree());
		u.setDepartmentName(user.getDepartmentName());
		u.setDeptFullPath(user.getDeptFullPath());
		u.setGender(user.getGender());
		u.setPassword(user.getPassword());
		u.setMobile(user.getMobile());
		u.setPosition(user.getPosition());
		u.setPostStatus(user.getPostStatus());
		u.setStatus(user.getStatus());
		userMg.update(u);

		return u;
	}

	@PUT
	@Path("/editPassword")
	public User editPassword(@QueryParam("id") String id,
			@QueryParam("formerlyPassword") String formerlyPassword,
			@QueryParam("nowPasswrd") String nowPasswrd)
	 			throws UserPersistException  {
		DefaultUserManager userMg = new DefaultUserManager(this
				.getPersistenceService());
		User user = userMg.findById(id);
		if (user.getPassword().equals(formerlyPassword)) {
			if (!nowPasswrd.equals(user.getPassword())) {
				userMg.updatePassword(id, nowPasswrd);
			}
		} else {
			throw new RuntimeException("旧密码输入错误");
		}
		return null;
	}

	@PUT
	@Path("/enable")
	@Produces("application/json")
	public User enable(@PathParam("id") String id) throws UserPersistException {
		DefaultUserManager userMg = new DefaultUserManager(this.getPersistenceService());
		userMg.enable(id);
		return userMg.findById(id);
	}

	@PUT
	@Path("/{id}/disable")
	@Produces("application/json")
	public User disable(@PathParam("id") String id) throws UserPersistException {
		DefaultUserManager userMg = new DefaultUserManager(this.getPersistenceService());
		userMg.enable(id);
		return userMg.findById(id);
	}

}
