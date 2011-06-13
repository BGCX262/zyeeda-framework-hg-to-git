package com.zyeeda.framework.ldap;

import java.util.ArrayList;
import java.util.List;
import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import org.apache.shiro.realm.ldap.LdapUtils;

import com.zyeeda.framework.ldap.LdapService;
/**
 * Ldap Common operation class
 * @author gary.wang
 * @since 1.5
 * @version 1.6
 * 
 */
public class LdapTemplate{

	@SuppressWarnings("unused")
	private LdapService ldapSvc;
	
	private LdapContext ctx;

	public LdapTemplate(LdapService ldapSvc) {
		this.ldapSvc = ldapSvc;
	}
	
	public LdapTemplate(LdapContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * Create a context
	 * @param dn - the full path of the context, dn not existing
	 * @param attrs - the dn's attributes
	 * @throws NamingException - if a naming exception is encountered
	 */
	public void bind(String dn, Attributes attrs) throws NamingException{
		try {
			this.ctx.bind(dn, null, attrs);
		} catch (NamingException e) {
			throw new RuntimeException(e);
		} finally {
			LdapUtils.closeContext(this.ctx);
		}
		
	}
	
	/**
	 * Binds a dn to an object, overwriting any existing binding
	 * @param dn, the full path of the context
	 * @param attrs the dn's attributes
	 * @throws NamingException - if a naming exception is encountered
	 */ 
	public void rebind(String dn, Attributes attrs) throws NamingException {
		try {
			this.ctx.rebind(dn, null, attrs);
		} catch (NamingException e) {
			throw new RuntimeException(e);
		} finally {
			LdapUtils.closeContext(this.ctx);
		}
	}
	
	/**
	 * Delete a context by dn, not cascade
	 * @param dn - the full path of the context
	 * @throws NamingException - if a naming exception is encountered
	 */
	public void unbind(String dn) throws NamingException {
		this.unbind(dn, false);
	}
	
	/**
	 * Delete a context by dn
	 * @param dn - the full path of the context
	 * @param cascade - if cascade is true it will delete all children
	 * @throws NamingException - if a naming exception is encountered
	 */
	private void unbind(String dn, Boolean cascade) throws NamingException {
		try {
			if (!cascade){
				this.ctx.unbind(dn);
			} else {
				NamingEnumeration<Binding> enumeration = this.ctx.listBindings(dn);
				while (enumeration.hasMore()) {
					Binding binding = (Binding) enumeration.next();
					unbind(binding.getNameInNamespace(),cascade);
				}
				this.ctx.unbind(dn);
			}
		} catch (NamingException e) {
			throw new RuntimeException(e);
		} 
	}
	
	/**
	 * @param dn - the full path of the context
	 * @return - a Attributes
	 * @throws NamingException - if a naming exception is encountered
	 */
	public Attributes findByDn(String dn) throws NamingException{
		try {
			return this.ctx.getAttributes(dn);
		} catch (NamingException e) {
			throw new RuntimeException(e);
		} finally {
			LdapUtils.closeContext(this.ctx);
		}
	}
	
	/**
	 * @param name - the name of the context to search
     * @param matchingAttributes - the attributes to search for 
	 * @return - the list of Attributes
	 * @throws NamingException - if a naming exception is encountered
	 */
	public List<Attributes> getResultList(String name,
			      				   		  Attributes matchingAttributes)
			      		    throws NamingException {
		NamingEnumeration<SearchResult> ne = this.ctx.search(name, matchingAttributes);
		return this.namingEnumerationToList(ne);
	}
	
	/**
	 * @param name - the name of the context or object to search
     * @filter - the filter expression to use for the search; may not be null
     * @param cons - the search controls that control the search. If null, the default search controls are used (equivalent to (new SearchControls())). 
	 * @return - the list of Attributes
	 * @throws NamingException - if a naming exception is encountered
	 */
    public List<Attributes> getResultList(String name,
			      					      String filter,
			                              SearchControls cons)
			                throws NamingException {
		NamingEnumeration<SearchResult> ne = this.ctx.search(name, filter, cons);
		return this.namingEnumerationToList(ne);
	}
	
    /**
	 * @param name - the name of the context or object to search
     * @param matchingAttributes - the attributes to search for
     * @param attributesToReturn - the attributes to return
	 * @return - the list of Attributes
	 * @throws NamingException - if a naming exception is encountered
	 */
	public List<Attributes> getResultList(String name,
							              Attributes matchingAttributes,
							              String[] attributesToReturn)
							throws NamingException {
		NamingEnumeration<SearchResult> ne = this.ctx.search(name,
												 matchingAttributes,
												 attributesToReturn);
		return this.namingEnumerationToList(ne);
	}
	
	/**
	 * @param name - the name of the context or object to search
     * @param filterExpr - the filter expression to use for the search. The expression may contain variables of the form "{i}" where i is a nonnegative integer. May not be null.
     * @param filterArgs - the array of arguments to substitute for the variables in filterExpr. The value of filterArgs[i] will replace each occurrence of "{i}". If null, equivalent to an empty array.
     * @param cons - the search controls that control the search. If null, the default search controls are used (equivalent to (new SearchControls())). 
	 * @return - the list of Attributes
	 * @throws NamingException - if a naming exception is encountered
	 */
	public List<Attributes> getResultList(String name,
										  String filterExpr,
										  Object[] filterArgs,
										  SearchControls cons)
						    throws NamingException {
		NamingEnumeration<SearchResult> ne = this.ctx.search(name,
															 filterExpr,
															 filterArgs,
															 cons);
		return this.namingEnumerationToList(ne);
	}
	
	 /**
	 * @param name - the name of the context or object to search
     * @param matchingAttributes - the attributes to search for
     * @param attributesToReturn - the attributes to return
	 * @return - QueryResult, it contains total record and result list
	 * @throws NamingException - if a naming exception is encountered
	 */
	public QueryResult<Attributes> search(String name,
            							  Attributes matchingAttributes,
            							  String[] attributesToReturn)
								   throws NamingException {
		NamingEnumeration<SearchResult> ne = this.ctx.search(name,
															 matchingAttributes,
															 attributesToReturn);
		return this.namingEnumerationToQueryResult(ne);
	}
	
	/**
	 * @param name - the name of the context to search
     * @param matchingAttributes - the attributes to search for 
	 * @return - QueryResult, it contains total record and result list
	 * @throws NamingException - if a naming exception is encountered
	 */
	public QueryResult<Attributes> search(String name,
            						      Attributes matchingAttributes)
     						       throws NamingException {
		NamingEnumeration<SearchResult> ne = this.ctx.search(name, matchingAttributes);
		return this.namingEnumerationToQueryResult(ne);
	}
	
	/**
	 * @param name - the name of the context or object to search
     * @filter - the filter expression to use for the search; may not be null
     * @param cons - the search controls that control the search. If null, the default search controls are used (equivalent to (new SearchControls())). 
	 * @return - QueryResult, it contains total record and result list
	 * @throws NamingException - if a naming exception is encountered
	 */
	public QueryResult<Attributes> search(String name,
            						      String filter,
            						      SearchControls cons)
     						       throws NamingException {
		NamingEnumeration<SearchResult> ne = this.ctx.search(name, filter, cons);
		return this.namingEnumerationToQueryResult(ne);
	}
	
	/**
	 * @param name - the name of the context or object to search
     * @param filterExpr - the filter expression to use for the search. The expression may contain variables of the form "{i}" where i is a nonnegative integer. May not be null.
     * @param filterArgs - the array of arguments to substitute for the variables in filterExpr. The value of filterArgs[i] will replace each occurrence of "{i}". If null, equivalent to an empty array.
     * @param cons - the search controls that control the search. If null, the default search controls are used (equivalent to (new SearchControls())). 
	 * @return - QueryResult, it contains total record and result list
	 * @throws NamingException - if a naming exception is encountered
	 */
	public QueryResult<Attributes> search(String name,
										  String filterExpr,
										  Object[] filterArgs,
										  SearchControls cons)
								   throws NamingException {
		NamingEnumeration<SearchResult> ne = this.ctx.search(name,
															 filterExpr,
															 filterArgs,
															 cons);
		return this.namingEnumerationToQueryResult(ne);
	}
	
	private List<Attributes> namingEnumerationToList(NamingEnumeration<SearchResult> ne)
								   throws NamingException {
		if (ne == null) {
			return null;
		}
		List<Attributes> attrList = new ArrayList<Attributes>();
		while (ne.hasMore()) {
			Attributes atrrs = ne.next().getAttributes();
			if (atrrs != null) {
				attrList.add(atrrs);
			}
		}
		
		return attrList;
	}
	
	private QueryResult<Attributes> namingEnumerationToQueryResult(
										  NamingEnumeration<SearchResult> ne)
									throws NamingException {
		List<Attributes> attrList = this.namingEnumerationToList(ne);
		QueryResult<Attributes> qr = new QueryResult<Attributes>();
		qr.setResultList(attrList);
		qr.setTotalRecords(attrList.size() + 0l);
		
		return qr;
	}
}
