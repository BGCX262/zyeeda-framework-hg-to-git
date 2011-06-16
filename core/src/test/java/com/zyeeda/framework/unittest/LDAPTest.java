package com.zyeeda.framework.unittest;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
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

public class LDAPTest {
	public LDAPTest() {}
	
	public static LdapContext getLdapContext() throws NamingException {
		String root = "dc=ehv,dc=csg,dc=cn";
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://localhost:389/" + root);
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
		ctx.setRequestControls(new Control[] { new PagedResultsControl(
				pageSize, Control.CRITICAL) });
		String sortKey = "uid";
	    ctx.setRequestControls(new Control[] {
	             new SortControl(sortKey, Control.NONCRITICAL) });
	    System.out.println("------------" + ctx.getRequestControls());
		// Perform the search
		NamingEnumeration<SearchResult> results = ctx.search("", "(objectclass=*)",
				new SearchControls());

		// Iterate over a batch of search results sent by the server
		while (results != null && results.hasMore()) {
			// Display an entry
			SearchResult entry = (SearchResult) results.next();
System.out.println(entry.getName());

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
	
	public static void save() throws NamingException {
		LdapContext ctx = getLdapContext();
		String dn = "uid=Test2,o=广州局";
		Attributes attrs = new BasicAttributes();
		
		attrs.put("objectClass", "top");
		attrs.put("objectClass", "person");
		attrs.put("objectClass", "organizationalPerson");
		attrs.put("objectClass", "inetOrgPerson");
		attrs.put("objectClass", "employee");

		attrs.put("cn", "Tes2");
		attrs.put("sn", "Tes2");
		attrs.put("userPassword", DigestUtils.md5Hex("123456"));
		ctx.bind(dn, null, attrs);
	}
	
	public static void main(String[] args) throws NamingException, IOException {
//		String root = "dc=ehv,dc=csg,dc=cn"; // root
//
//		Hashtable<String, String> env = new Hashtable<String, String>();
//		env.put(Context.INITIAL_CONTEXT_FACTORY,
//				"com.sun.jndi.ldap.LdapCtxFactory");
//		env.put(Context.PROVIDER_URL, "ldap://localhost:389/" + root);
//		env.put(Context.SECURITY_AUTHENTICATION, "simple");
//		env.put(Context.SECURITY_PRINCIPAL, "cn=admin");
//		env.put(Context.SECURITY_CREDENTIALS, "admin");
//		LdapContext ctx = null;
//		try {
//			ctx = new InitialLdapContext(env, null);
//			String sortKey = "uid";
//		    ctx.setRequestControls(new Control[] {
//		             new SortControl(sortKey, Control.NONCRITICAL) });
//		    
//		    ctx.setRequestControls(new Control[]{
//			         new PagedResultsControl(5, Control.CRITICAL) });
//System.out.println("------------" + ctx.getRequestControls().length);
//			NamingEnumeration<SearchResult> ne = ctx.search("ou=People",
//															"uid=*",
//															SearchControlsFactory.getSearchControls(SearchControls.SUBTREE_SCOPE));
//			int i = 0;
//			while (ne.hasMore()) {
////				System.out.println(ne.next().getAttributes());
//				i ++;
//			}
//		System.out.println("认证成功");
//		System.out.println(i);
//		} catch (javax.naming.AuthenticationException e) {
//			e.printStackTrace();
//			System.out.println("认证失败");
//		} catch (Exception e) {
//			System.out.println("认证出错：");
//			e.printStackTrace();
//		}
//
//		if (ctx != null) {
//			try {
//				ctx.close();
//			} catch (NamingException e) {
//			}
//		}
//		System.exit(0);
//		save();
		sort();
	}

}