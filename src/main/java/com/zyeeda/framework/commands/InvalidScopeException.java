// Copyright 2010, Zyeeda, Inc. All Rights Reserved.
// Confidential and Proprietary Information of Zyeeda, Inc.

package com.zyeeda.framework.commands;

/**
 * invalid scope exception
 *
 * @author Qi Zhao
 * @date 2010-4-26
 *
 * @LastChanged
 * @LastChangedBy $LastChangedBy$
 * @LastChangedDate $LastChangedDate$
 * @LastChangedRevision $LastChangedRevision$
 */
public class InvalidScopeException extends Exception {

    private static final long serialVersionUID = 1560879767876807057L;

    private String scope;

    public InvalidScopeException(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return this.scope;
    }
    
    @Override
    public String getMessage() {
    	return "The scope " + this.scope.toString() + " of the command is invalid.";
    }
}
