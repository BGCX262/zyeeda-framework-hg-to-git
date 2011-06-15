package com.zyeeda.framework.account;

import java.util.Map;

import com.zyeeda.framework.service.Service;

public interface AccountService extends Service {

    public Map<String, String> getMockSignInConfig(String prefix);
}
