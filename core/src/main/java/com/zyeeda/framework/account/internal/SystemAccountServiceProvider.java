// Copyright 2011, Zyeeda, Inc. All Rights Reserved.
// Confidential and Proprietary Information of Zyeeda, Inc.

package com.zyeeda.framework.account.internal;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.configuration.Configuration;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;

import com.zyeeda.framework.account.AccountService;
import com.zyeeda.framework.config.ConfigurationService;
import com.zyeeda.framework.service.AbstractService;

/**
 * account support service implement 
 *
 * @author Qi Zhao
 * @date 2011-06-15
 *
 * @LastChanged
 * @LastChangedBy $LastChangedBy:  $
 * @LastChangedDate $LastChangedDate:  $
 * @LastChangedRevision $LastChangedRevision:  $
 */
@ServiceId("system-account-service-provider")
@Marker(Primary.class)
public class SystemAccountServiceProvider extends AbstractService implements AccountService {

    private Configuration config;

    public SystemAccountServiceProvider(ConfigurationService configSvc, RegistryShutdownHub shutdownHub) {
        super(shutdownHub);
        this.config = this.getConfiguration(configSvc);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> getMockSignInConfig(String prefix) {
        Map<String, String> result = new HashMap<String, String>();
        Iterator<String> keys = config.getKeys(prefix);	
        
        while (keys.hasNext()) {
            String key = keys.next();
            result.put(key, config.getString(key));
        }
        return result;
    }
}
