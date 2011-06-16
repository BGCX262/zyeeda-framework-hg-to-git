// Copyright 2011, Zyeeda, Inc. All Rights Reserved.
// Confidential and Proprietary Information of Zyeeda, Inc.

package com.zyeeda.framework.helpers;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;

import com.zyeeda.framework.entities.Account;

/**
 * help the between the attributes and account convert. 
 * 
 * @author Qi Zhao
 * @date 2011-06-15
 *
 * @LastChanged
 * @LastChangedBy $LastChangedBy:  $
 * @LastChangedDate $LastChangedDate:  $
 * @LastChangedRevision $LastChangedRevision:  $
 */
public class AccountHelper {

    public static Attributes convertAccountToAttributes(Account account) {
        Attributes attributes = new BasicAttributes();
        attributes.put("objectClass", "inetOrgPerson");
        attributes.put("objectClass", "organizationalPerson");
        attributes.put("objectClass", "person");
        attributes.put("objectClass", "top");
        attributes.put("objectClass", "userReferenceSystem");

        attributes.put("cn", account.getSystemName());
        attributes.put("sn", account.getSystemName());

        attributes.put("systemName", account.getSystemName());
        attributes.put("username", account.getUserName());
        attributes.put("password", account.getPassword());

        return attributes;
    }

    public static Account convertAttributesToAccount(Attributes attributes) throws NamingException {
        Account account = new Account();

        account.setSystemName(attributes.get("systemName").get() != null ? (String) attributes.get("systemName").get() : "");
        account.setUserName(attributes.get("username").get() != null ? (String) attributes.get("username").get() : "");
        account.setPassword(attributes.get("password").get() != null ? (String) attributes.get("password").get() : "");

        return account;
    }
    
    public static List<Account> convertNameingEnumeractionToAccountList(NamingEnumeration<SearchResult> nes) throws NamingException {
    	List<Account> accounts = new ArrayList<Account>();
    	
    	while (nes.hasMore()) {
            Attributes attributes = nes.next().getAttributes();
            Account account = convertAttributesToAccount(attributes);
            accounts.add(account);
    	}
    	
    	return accounts;
    }
}
