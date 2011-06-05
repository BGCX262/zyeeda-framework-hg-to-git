package com.zyeeda.framework.managers.internal;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.realm.ldap.LdapUtils;

import com.ibm.icu.text.SimpleDateFormat;
import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.ldap.SearchControlsFactory;
import com.zyeeda.framework.managers.UserManager;
import com.zyeeda.framework.managers.UserPersistException;
import com.zyeeda.framework.utils.DatetimeUtils;
import com.zyeeda.framework.utils.LdapEncryptUtils;

public class LdapUserManager implements UserManager {

//	private static final Logger logger = LoggerFactory.getLogger(LdapUserManager.class);
	
	private static final String LDAP_DEFAULT_PASSWORD = "123456";

	private LdapService ldapSvc;

	public LdapUserManager(LdapService ldapSvc) {
		this.ldapSvc = ldapSvc;
	}

	@Override
	public void persist(User user) throws UserPersistException {
		LdapContext ctx = null;
		
		try {
			ctx = this.ldapSvc.getLdapContext();
			Attributes attrs = LdapUserManager.unmarshal(user);
			ctx.createSubcontext(user.getDeptFullPath(), attrs);
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} catch (UnsupportedEncodingException e) {
			throw new UserPersistException(e);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}

	@Override
	public void remove(String id) throws UserPersistException {
		LdapContext ctx = null;
		try {
			ctx = this.ldapSvc.getLdapContext();
			ctx.destroySubcontext(id);
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}

	@Override
	public void update(User user) throws UserPersistException {
		LdapContext ctx = null;
		
		try {
			String dn = user.getDeptFullPath();
			ctx = this.ldapSvc.getLdapContext();
			Attributes attrs = LdapUserManager.unmarshal(user);
			ctx.modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, attrs);
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} catch (UnsupportedEncodingException e) {
			throw new UserPersistException(e);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}

	@Override
	public User findById(String id) throws UserPersistException {
		LdapContext ctx = null;
		
		try {
			ctx = this.ldapSvc.getLdapContext();
			Attributes attrs = ctx.getAttributes(id);
			User user = LdapUserManager.marshal(attrs);
			
			return user;
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} catch (ParseException e) {
			throw new UserPersistException(e);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}

	@Override
	public List<User> findByDepartmentId(String id) throws UserPersistException {
		LdapContext ctx = null;
		NamingEnumeration<SearchResult> ne = null;
		List<User> userList = null;

		try {
			ctx = this.ldapSvc.getLdapContext();
			ne = ctx.search(id, "(uid=*)", SearchControlsFactory.getSearchControls(
					SearchControls.ONELEVEL_SCOPE));
			SearchResult entry = null;
			userList = new ArrayList<User>();
			
			for (; ne.hasMore();) {
				entry = ne.next();
				Attributes attrs = entry.getAttributes();
				User user = LdapUserManager.marshal(attrs);

				userList.add(user);
			}
			return userList;
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} catch (ParseException e) {
			throw new UserPersistException(e);
		} finally {
			LdapUtils.closeEnumeration(ne);
			LdapUtils.closeContext(ctx);
		}
	}

	@Override
	public List<User> findByName(String name) throws UserPersistException {
		LdapContext ctx = null;
		NamingEnumeration<SearchResult> ne = null;
		List<User> userList = null;

		try {
			ctx = this.ldapSvc.getLdapContext();
			ne = ctx.search("o=广州局", "(uid=" + name + ")", SearchControlsFactory.
					getSearchControls(SearchControls.SUBTREE_SCOPE));
			SearchResult entry = null;
			userList = new ArrayList<User>();
			
			for (; ne.hasMore();) {
				entry = ne.next();
				User user = new User();
				String dn = entry.getName();

				Attributes attr = entry.getAttributes();
				user.setUsername((String) attr.get("cn").get());
				user.setId(dn);
				user.setPassword(new String((byte[]) attr.get("userpassword").get()));
				
				userList.add(user);
			}
			return userList;
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} finally {
			LdapUtils.closeEnumeration(ne);
			LdapUtils.closeContext(ctx);
		}
	}

	public void updatePassword(String id, String password) throws UserPersistException {
		LdapContext ctx = null;
		
		try {
			ctx = this.ldapSvc.getLdapContext();
			ModificationItem[] mods = new ModificationItem[1];
			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, 
					      new BasicAttribute("userPassword", password));
			
			ctx.modifyAttributes(id, mods);
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}
	
	@Override
	public void enable(String... ids) throws UserPersistException {
		try {
			this.setVisible(true, ids);
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} catch (ParseException e) {
			throw new UserPersistException(e);
		}
	}
	
	@Override
	public void disable(String... ids) throws UserPersistException {
		try {
			this.setVisible(false, ids);
		} catch (NamingException e) {
			throw new UserPersistException(e);
		} catch (ParseException e) {
			throw new UserPersistException(e);
		}
	}
	
	private void setVisible(Boolean visible, String... ids)
			throws NamingException, ParseException {
		LdapContext ctx = null;
		
		try {
			ctx = this.ldapSvc.getLdapContext();
			ModificationItem[] mods = new ModificationItem[1];
			
			for (String dn : ids) {
				mods = new ModificationItem[1];
				mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, 
								new BasicAttribute("status", visible.toString()));
				ctx.modifyAttributes(dn, mods);
			}
		} finally {
			LdapUtils.closeContext(ctx);
		}
	}
	
//	@Override
//	public List<User> getUserListByDepartmentId(String id, String type)
//			throws NamingException {
//		LdapContext ctx = null;
//		NamingEnumeration<SearchResult> ne = null;
//		List<User> userList = null;
//		logger.debug("the value of the id is = {}  ", id);
//
//		try {
//			ctx = this.ldapSvc.getLdapContext();
//			ne = ctx.search(id, "(uid=*)", this.getOneLevelScopeSearchControls());
//
//			SearchResult entry = null;
//			userList = new ArrayList<User>();
//			for (; ne.hasMore();) {
//				entry = ne.next();
//				User user = new User();
//				Attributes attr = entry.getAttributes();
//
//				user.setUsername((String) attr.get("cn").get());
//				user.setId((String) attr.get("uid").get());
//				user.setPassword(new String((byte[]) attr.get("userPassword").get()));
//				user.setDeptFullPath(id);
//
////				UserVo userVo = this.fillUserPropertiesToVo(user);
////				userVo.setType(type);
//				userList.add(user);
//			}
//			return userList;
//		} finally {
//			LdapUtils.closeEnumeration(ne);
//			LdapUtils.closeContext(ctx);
//		}
//	}
	
	//add for upload photo
//	private static byte[] getBytesFromFile(File file) throws IOException {
//		InputStream is = new FileInputStream(file);
//        long length = file.length();
//        if (length > Integer.MAX_VALUE) {
//        	throw new IOException("File is to large "+file.getName());
//        }
//        byte[] bytes = new byte[(int)length];
//        int offset = 0;
//        int numRead = 0;
//
//        while (offset < bytes.length
//        		&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
//            offset += numRead;
//        }
//        if (offset < bytes.length) {
//            throw new IOException("Could not completely read file "+file.getName());
//        }
//        is.close();
//        return bytes;
//	}
	
	private static Attributes unmarshal(User user) throws UnsupportedEncodingException {
		Attributes attrs = new BasicAttributes();

		attrs.put("objectClass", "top");
		attrs.put("objectClass", "person");
		attrs.put("objectClass", "organizationalPerson");
		attrs.put("objectClass", "inetOrgPerson");
		attrs.put("objectClass", "employee");

		attrs.put("cn", user.getUsername());
		attrs.put("sn", user.getUsername());
		attrs.put("uid", user.getId());

		if (StringUtils.isNotBlank(user.getPassword())) {
			attrs.put("userPassword", "{MD5}" + LdapEncryptUtils.md5Encode(
					DigestUtils.md5Hex(user.getPassword())));
		} else {
			attrs.put("userPassword", "{MD5}" + LdapEncryptUtils.md5Encode(
					DigestUtils.md5Hex(LDAP_DEFAULT_PASSWORD)));
		}
		if (StringUtils.isNotBlank(user.getGender())) {
			attrs.put("gender", user.getGender());
		} 
		if (StringUtils.isNotBlank(user.getPosition())) {
			attrs.put("position", user.getPosition());
		}
		if (StringUtils.isNotBlank(user.getDegree())) {
			attrs.put("degree", user.getDegree());
		}
		if (StringUtils.isNotBlank(user.getEmail())) {
			attrs.put("mail", user.getEmail());
		}
		if (StringUtils.isNotBlank(user.getMobile())) {
			attrs.put("mobile", user.getMobile());
		}
		if (user.getBirthday() != null) {
			attrs.put("birthday", DatetimeUtils.formatDate(user.getBirthday()));
		}
		if (user.getDateOfWork() != null) {
			attrs.put("dateOfWork", DatetimeUtils.formatDate(user.getDateOfWork()));
		}
		if (user.getStatus() != null) {
			attrs.put("status", user.getStatus().toString());
		}
		if (user.getPostStatus() != null) {
			attrs.put("postStatus", user.getPostStatus().toString());
		}
		if (user.getDepartmentName() != null) {
			attrs.put("deptName", user.getDepartmentName());
		}
		if (user.getDeptFullPath() != null) {
			attrs.put("deptFullPath", user.getDeptFullPath());
		}
		
		return attrs;
	}
	
	private static User marshal(Attributes attrs) throws NamingException,
			ParseException {
		User user = new User();
		
		user.setUsername((String) attrs.get("sn").get());
		user.setId((String) attrs.get("uid").get());
		user.setPassword(new String((byte[]) attrs.get("userPassword").get()));
		user.setPassword(user.getPassword().substring(5, user.getPassword().length()));
		if (attrs.get("gender") != null) {
			user.setGender((String) attrs.get("gender").get());
		}
		if (attrs.get("degree") != null) {
			user.setDegree((String) attrs.get("degree").get());
		}
		if (attrs.get("position") != null) {
			user.setPosition((String) attrs.get("position").get());
		}
		if (attrs.get("mail") != null) {
			user.setEmail((String) attrs.get("mail").get());
		}
		if (attrs.get("mobile") != null) {
			user.setMobile((String) attrs.get("mobile").get());
		}
		if (attrs.get("birthday") != null) {
			user.setBirthday(new SimpleDateFormat("yy-MM-dd").parse(attrs.get("birthday").get().toString()));
		}
		if (attrs.get("dateOfWork") != null) {
			user.setDateOfWork(new SimpleDateFormat("yy-MM-dd").parse(attrs.get("dateOfWork").get().toString()));
		}
		if (attrs.get("status") != null) {
			user.setStatus(new Boolean(attrs.get("status").get().toString()));
		}
		if (attrs.get("postStatus") != null) {
			user.setPostStatus(new Boolean(attrs.get("postStatus").get().toString()));
		}
		if (attrs.get("deptName") != null) {
			user.setDepartmentName(attrs.get("deptName").get().toString());
		}
		if (attrs.get("deptFullPath") != null) {
			user.setDeptFullPath(attrs.get("deptFullPath").get().toString());
		}
		
		return user;
	}
	
}
