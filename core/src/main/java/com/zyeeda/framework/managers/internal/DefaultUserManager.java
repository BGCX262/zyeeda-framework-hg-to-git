package com.zyeeda.framework.managers.internal;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.apache.shiro.realm.ldap.LdapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.managers.UserManager;
import com.zyeeda.framework.viewmodels.UserVo;

public class DefaultUserManager implements UserManager {

	private static final Logger logger = LoggerFactory.getLogger(DefaultUserManager.class);
	
	private LdapService ldapSvc;
	
	public DefaultUserManager(LdapService ldapSvc) {
		this.ldapSvc = ldapSvc;
	}
	
	@Override
	public UserVo persist(User user) throws NamingException {
		LdapContext ctx = null;
		LdapContext parentCtx = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			String department = user.getDepartment();
			String dn = String.format("uid=%s", user.getUid());
			logger.debug("the value of the dn and department is = {}b  {}  ", dn, department);
			
			parentCtx = (LdapContext) ctx.lookup(department);
			Attributes attrs = unmarshal(user);
			parentCtx.createSubcontext(dn, attrs);
			
			UserVo userVo = this.fillUserPropertiesToVo(user);
			
			return userVo;
		} finally {
			LdapUtils.closeContext(parentCtx);
			LdapUtils.closeContext(ctx);
		}
	}
	
	@Override
	public void remove(String id) throws NamingException {
		LdapContext ctx = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			ctx.destroySubcontext(id);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}
	
	@Override
	public UserVo update(User user) throws NamingException {
		LdapContext ctx = null;
		try {
			String uid = this.findById(user.getId()).getLabel().replaceAll("<a>", "");
			logger.debug("the value of the uid is = {} ", uid);
			
			ctx = this.ldapSvc.getLdapContext();
			// 如果名称相同，则可以修改
			if (user.getUid().equals(uid)) {
				Attributes attrs = unmarshal(user);
				String dn = user.getId();
				logger.debug("the value of the dn is =  {}  ", dn);
				
				ctx.modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, attrs);
			} else {
				// 修改名称会出现异常
				logger.debug("******************error perform******************");
			}
			
			UserVo userVo = new UserVo();
			userVo = this.fillUserPropertiesToVo(user);
			
			return userVo;
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}
	
	@Override
	public UserVo findById(String id) throws NamingException {
		LdapContext cxt = null;
		try {
			cxt = this.ldapSvc.getLdapContext();
			Attributes attrs = cxt.getAttributes(id);
			
			User user = marshal(attrs);
			UserVo userVo = new UserVo();
			userVo = this.fillUserPropertiesToVo(user);
			
			return userVo;
		} finally {
			LdapUtils.closeContext(cxt);
		}
	}

	@Override
	public List<UserVo> getUserListByDepartmentId(String id)
			throws NamingException {
		LdapContext ctx = null;
		NamingEnumeration<SearchResult> ne = null;
		List<UserVo> userList = null;
		logger.debug("the value of the id is = {}  ", id);
		
		try {
			ctx = this.ldapSvc.getLdapContext();
			ne = ctx.search(id, "(uid=*)", this.getOneLevelScopeSearchControls());
			
			SearchResult entry = null;
			userList = new ArrayList<UserVo>();
			for (; ne.hasMore(); ) {
				entry = ne.next();
				User user = new User();
				Attributes attr = entry.getAttributes();
				
				String uid = (String) attr.get("uid").get();
				String childId = String.format("uid=%s,%s", uid, id);
				logger.debug("the value of the uid and childId = {}  {}", uid, childId);
				
				user.setCommonName((String) attr.get("cn").get());
				user.setSurname((String) attr.get("sn").get());
				user.setUid((String) attr.get("uid").get());
				user.setId(childId);
				user.setUserPassword(new String((byte[]) attr.get("userpassword").get()));
				
				UserVo userVo = this.fillUserPropertiesToVo(user);
				
				userList.add(userVo);
			}
			logger.debug("**********the userList's size is = {}" + userList.size());
			
			return userList;
		} finally {
			LdapUtils.closeEnumeration(ne);
			LdapUtils.closeContext(ctx);
		}
	}

	@Override
	public List<UserVo> getUserListByName(String name) throws NamingException {
		LdapContext ctx = null;
		NamingEnumeration<SearchResult> ne = null;
		List<UserVo> userList = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			ne = ctx.search("o=广州局", "(uid=*" + name + ")", this.getThreeLevelScopeSearchControls());
			
			SearchResult entry = null; 
			userList = new ArrayList<UserVo>();
			for (; ne.hasMore(); ) {
				entry = ne.next();
				User user = new User();
				String dn = entry.getName();
				logger.debug("**********the value of the childId is = {}  ", dn);
				
				Attributes attr = entry.getAttributes();
				user.setCommonName((String) attr.get("cn").get());
				user.setSurname((String) attr.get("sn").get());
				user.setUid(dn);
				user.setUserPassword(new String((byte[]) attr.get("userpassword").get()));
				
				UserVo userVo = this.fillUserPropertiesToVo(user);
				
				userList.add(userVo);
			}
			if (userList != null) {
				logger.debug("**********the userList's size is = {}  ", userList.size());
			}
			
			return userList;
		} finally {
			LdapUtils.closeEnumeration(ne);
			LdapUtils.closeContext(ctx);
		}
	}

	private static Attributes unmarshal(User user) {
		Attributes attrs = new BasicAttributes();
		
		attrs.put("objectClass", "top");
		attrs.put("objectClass", "person");
		attrs.put("objectClass", "organizationalPerson");
		attrs.put("objectClass", "inetOrgPerson");
		attrs.put("cn", user.getCommonName());
		attrs.put("sn", user.getSurname());
//		attrs.put("uid", user.getUid());
		attrs.put("userPassword", user.getUserPassword());
		
		return attrs;
	}
	
	private static User marshal(Attributes attrs) throws NamingException {
		User user = new User();
		
		user.setCommonName((String) attrs.get("cn").get());
		user.setSurname((String) attrs.get("sn").get());
		user.setUid((String) attrs.get("uid").get());
		user.setUserPassword(new String((byte[]) attrs.get("userpassword").get()));
		
		return user;
	}
	
	private UserVo fillUserPropertiesToVo(User user) {
		UserVo userVo = new UserVo();
		
		userVo.setId(user.getId());
		userVo.setType("task");
		userVo.setLabel("<a>" + user.getUid() + "<a>");
		userVo.setCheckName(user.getId());
		userVo.setLeaf(true);
		userVo.setUid(user.getUid());
		
		return userVo;
	}

	private SearchControls getOneLevelScopeSearchControls() {
		SearchControls sc = this.getSearchControls();
		sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);

		return sc;
	}
	
	private SearchControls getThreeLevelScopeSearchControls() {
		SearchControls sc = this.getSearchControls();
		sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

		return sc;
	}

	private SearchControls getSearchControls() {
		SearchControls sc = new SearchControls();

		return sc;
	}

}
