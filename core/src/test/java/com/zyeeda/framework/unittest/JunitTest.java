package com.zyeeda.framework.unittest;


import java.util.Hashtable;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.realm.ldap.LdapUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zyeeda.framework.ldap.SearchControlsFactory;


public class JunitTest {

	private static LdapContext ldapContext = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://192.168.1.14:10389/dc=ehv,dc=csg,dc=cn");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "uid=admin,dc=ehv,dc=csg,dc=cn");
		env.put(Context.SECURITY_CREDENTIALS, "admin");
		ldapContext = new InitialLdapContext(env, null);
	}
	
	@Test
	public void saveDept() throws NamingException {
		String dn = "ou=深圳局,o=广州局";
		Attributes attrs = new BasicAttributes();
		attrs.put("objectClass", "top");
		attrs.put("objectClass", "organizationalUnit");
		attrs.put("ou", "zyeeda");
		attrs.put("description", "zyeeda");
		ldapContext.bind(dn, null, attrs);
		LdapUtils.closeContext(ldapContext);
	}
	
	@Test
	public void saveUser() throws NamingException {
		String dn = "uid=Test1,ou=深圳站,o=广州局";
		Attributes attrs = new BasicAttributes();
		
		attrs.put("objectClass", "top");
		attrs.put("objectClass", "person");
		attrs.put("objectClass", "organizationalPerson");
		attrs.put("objectClass", "inetOrgPerson");
		attrs.put("objectClass", "employee");

		attrs.put("cn", "Tes1");
		attrs.put("sn", "Tes1");
		attrs.put("userPassword", "{MD5}" + DigestUtils.md5Hex("123456"));
		
		ldapContext.bind(dn, null, attrs);
		LdapUtils.closeContext(ldapContext);
	}
	
	@Test
	public void updateDept() throws NamingException {
		String dn = "ou=深圳局,o=广州局";
		Attributes attrs = new BasicAttributes();
		attrs.put("objectClass", "top");
		attrs.put("objectClass", "organizationalUnit");
		attrs.put("ou", "zyeeda");
		attrs.put("description", "zyeeda");
		ldapContext.rebind(dn, null, attrs);
		LdapUtils.closeContext(ldapContext);
	}
	
	@Test
	public void updateUser() throws NamingException {
		String dn = "uid=Test1,ou=深圳站,o=广州局";
		Attributes attrs = new BasicAttributes();
		
		attrs.put("objectClass", "top");
		attrs.put("objectClass", "person");
		attrs.put("objectClass", "organizationalPerson");
		attrs.put("objectClass", "inetOrgPerson");
		attrs.put("objectClass", "employee");

		attrs.put("cn", "Tes1");
		attrs.put("sn", "Tes1");
		attrs.put("userPassword", "{MD5}" + DigestUtils.md5Hex("123456"));
		
		ldapContext.rebind(dn, null, attrs);
		LdapUtils.closeContext(ldapContext);
	}
	
	@Test
	public void deleteDept() throws NamingException {
		String dn = "ou=深圳局,o=广州局";
		delete(dn);
	}
	
	@Test
	public void deleteUser() throws NamingException {
		String dn = "uid=Test1,ou=深圳站,o=广州局";
		delete(dn);
	}
	
	@Test
	public void findByDn() throws NamingException {
		String dn = "uid=Test1,ou=深圳站,o=广州局";
		Attributes attrs = ldapContext.getAttributes(dn);
		System.out.println(attrs);
	}
	
	@Test
	public void getChildren() throws NamingException {
		String dn = "ou=深圳站,o=广州局";
		NamingEnumeration<SearchResult> ne = ldapContext.search(dn, "(uid=*)", SearchControlsFactory.getSearchControls(
				SearchControls.SUBTREE_SCOPE));
		while (ne.hasMore()) {
			System.out.println(ne.next().getAttributes());
		}
	}
	
	public void delete(String dn) throws NamingException {
		NamingEnumeration<Binding> enumeration = ldapContext.listBindings(dn);
		while (enumeration.hasMore()) {
			Binding binding = (Binding) enumeration.next();
			System.out.println(binding.getNameInNamespace());
			delete(binding.getNameInNamespace());
		}
		ldapContext.unbind(dn);
	}

}
