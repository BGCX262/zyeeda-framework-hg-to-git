package com.zyeeda.framework.unittest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.HasControls;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;
import javax.naming.ldap.SortControl;
import javax.naming.ldap.SortResponseControl;

import org.apache.commons.codec.digest.DigestUtils;

import com.zyeeda.framework.utils.LdapEncryptUtils;

public class LDAPTest {
	public LDAPTest() {}
	
	public static LdapContext getLdapContext() throws NamingException {
		String root = "dc=ehv,dc=csg,dc=cn";
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://192.168.1.85:389/" + root);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "cn=admin");
		env.put(Context.SECURITY_CREDENTIALS, "admin");
		LdapContext ctx = new InitialLdapContext(env, null);
		return ctx;
	}
	
	public static void ldapPageView() throws NamingException, IOException {
		LdapContext ctx = getLdapContext();
		int pageSize = 5; // 5 entries per page
		byte[] cookie = null;
		int total = 0;
//		ctx.setRequestControls(new Control[] { new PagedResultsControl(
//				pageSize, Control.CRITICAL) });
		String sortKey = "uid";
//	    ctx.setRequestControls(new Control[] {
//	             new SortControl(sortKey, Control.NONCRITICAL) });
		// Perform the search
		SearchControls sc = new SearchControls();
		sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String condition = "Test";
		NamingEnumeration<SearchResult> results = ctx.search("", "uid=Test5",
				sc);
		// Iterate over a batch of search results sent by the server
		while (results != null && results.hasMore()) {
			// Display an entry
			SearchResult entry = (SearchResult) results.next();
			Attributes attributes = entry.getAttributes();
//			System.out.println(new String((byte[])attributes.get("userpassword").get()));
//			System.out.println(attributes.get("userpassword").get());
			System.out.println(new String((byte[]) attributes.get("userpassword").get()));
			// Handle the entry's response controls (if any)
			if (entry instanceof HasControls) {
				// ((HasControls)entry).getControls();
			}
		}
		// Examine the paged results control response
		Control[] controls = ctx.getResponseControls();
		if (controls != null) {
			for (int i = 0; i < controls.length; i++) {
				if (controls[i] instanceof PagedResultsResponseControl) {
					PagedResultsResponseControl prrc = (PagedResultsResponseControl) controls[i];
					total = prrc.getResultSize();
					cookie = prrc.getCookie();
				} else {
					// Handle other response controls (if any)
				}
			}
		}
		// Re-activate paged results
		ctx.setRequestControls(new Control[] {new PagedResultsControl(pageSize,
																	  cookie,
																	  Control.CRITICAL)});
	}

	public static void sort() throws NamingException, IOException {
		LdapContext ctx = getLdapContext();
		String sortKey = "ou";
		ctx.setRequestControls(new Control[] { new SortControl(sortKey,
				Control.CRITICAL) });
		ctx.setRequestControls(new Control[] { new SortControl("o",
				Control.CRITICAL) });

		// Perform a search
		NamingEnumeration<SearchResult> results = ctx.search("", "(objectclass=*)",
				new SearchControls());

		// Iterate over sorted search results
		while (results != null && results.hasMore()) {
			// Display an entry
			SearchResult entry = (SearchResult) results.next();
			System.out.println(entry.getName());

			// Handle the entry's response controls (if any)
			if (entry instanceof HasControls) {
				// ((HasControls)entry).getControls();
			}
		}
		// Examine the sort control response
		Control[] controls = ctx.getResponseControls();
		if (controls != null) {
			for (int i = 0; i < controls.length; i++) {
				if (controls[i] instanceof SortResponseControl) {
					SortResponseControl src = (SortResponseControl) controls[i];
					if (!src.isSorted()) {
						throw src.getException();
					}
				} else {
					// Handle other response controls (if any)
				}
			}
		}
	}
	
	public static void save() throws NamingException, UnsupportedEncodingException {
		LdapContext ctx = getLdapContext();
		String dn = "uid=Test5,o=广州局";
		Attributes attrs = new BasicAttributes();
		
		attrs.put("objectClass", "top");
		attrs.put("objectClass", "person");
		attrs.put("objectClass", "organizationalPerson");
		attrs.put("objectClass", "inetOrgPerson");
		attrs.put("objectClass", "employee");

		attrs.put("cn", "Tes5");
		attrs.put("sn", "Tes5");
		attrs.put("userPassword", "111111");
		ctx.bind(dn, null, attrs);
	}
	
	public static void saveUserRefObject() throws NamingException {
		LdapContext ctx = getLdapContext();
		String dn = "username=y,uid=Test2,o=广州局";
		Attributes attrs = new BasicAttributes();
		
		attrs.put("objectClass", "top");
		attrs.put("objectClass", "person");
		attrs.put("objectClass", "organizationalPerson");
		attrs.put("objectClass", "inetOrgPerson");
		attrs.put("objectClass", "userReferenceSystem");

		attrs.put("cn", "Tes2");
		attrs.put("sn", "Tes2");
		attrs.put("username", "test");
		attrs.put("password", DigestUtils.sha256("123456"));
		attrs.put("systemName", "test");
		ctx.bind(dn, null, attrs);
	}
	
	public static void updateUserDeptFullPath() throws NamingException {
		LdapContext ctx = getLdapContext();
		SearchControls sc = new SearchControls();
		sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<SearchResult> results = ctx.search("", "(uid=*)",
				sc);
		ModificationItem[] mods = new ModificationItem[3];
		SearchResult rs = null;
		while (results.hasMore()) {
			rs = results.next();
			if (!rs.getNameInNamespace().startsWith("uid=admin")) {
System.out.println(new String((byte[]) rs.getAttributes().get("userpassword").get()));
				mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, 
	   				   new BasicAttribute("deptName", 
	   			rs.getNameInNamespace().replaceAll(",dc=ehv,dc=csg,dc=cn", "").substring(
	   					rs.getNameInNamespace().replaceAll(",dc=ehv,dc=csg,dc=cn", "").indexOf(",") + 1, 
	   					rs.getNameInNamespace().replaceAll(",dc=ehv,dc=csg,dc=cn", "").length())));
				mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, 
		   				   new BasicAttribute("deptFullPath", 
		   			rs.getNameInNamespace().replaceAll(",dc=ehv,dc=csg,dc=cn", "")));
				mods[2] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, 
		   				   new BasicAttribute("userPassword", DigestUtils.md5Hex("111111")));
				ctx.modifyAttributes(rs.getNameInNamespace().replaceAll(",dc=ehv,dc=csg,dc=cn", "")
																	, mods);
			}
		}
	}
	
	public static void main(String[] args) throws NamingException, IOException {
//		System.exit(0);
//		saveUserRefObject();
//		ldapPageView();
//		getAllUser();
		updateUserDeptFullPath();
//		ldapPageView();
//		save();
	}

}