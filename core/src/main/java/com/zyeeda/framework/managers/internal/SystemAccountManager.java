// Copyright 2010, Zyeeda, Inc. All Rights Reserved.
// Confidential and Proprietary Information of Zyeeda, Inc.

package com.zyeeda.framework.managers.internal;

import java.util.List;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import com.zyeeda.framework.entities.Account;
import com.zyeeda.framework.ldap.LdapService;
import com.zyeeda.framework.managers.AccountManager;
import com.zyeeda.framework.managers.UserPersistException;

/**
 * system account manager impl 
 *
 * @author Qi Zhao
 * @date 2011-06-15
 *
 * @LastChanged
 * @LastChangedBy $LastChangedBy:  $
 * @LastChangedDate $LastChangedDate:  $
 * @LastChangedRevision $LastChangedRevision:  $
 */
public class SystemAccountManager implements AccountManager {

    private LdapService ldapSvc;

    public SystemAccountManager(LdapService ldapSvc) {
        this.ldapSvc = ldapSvc;
    }

    public List<Account> findByUserId(String userId) throws UserPersistException {

        return null;
    }

    public Account findByUserIdAndSystemName(String userId, String systemName) throws UserPersistException {

        return null;
    }

    public void update(Account account) throws UserPersistException {
        try {
            LdapContext context = this.getLdapContext();
        } catch (NamingException e) {
            throw new UserPersistException(e); 
        }
    }

    private LdapContext getLdapContext() throws NamingException {
        return this.ldapSvc.getLdapContext();
    }
}
