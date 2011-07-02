package com.zyeeda.framework.sync.internal;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.BasicNameValuePair;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;

import com.zyeeda.framework.config.ConfigurationService;
import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.service.AbstractService;
import com.zyeeda.framework.sync.UserSyncService;
import com.zyeeda.framework.utils.DatetimeUtils;

@ServiceId("httpclient-user-sync-service")
@Marker(Primary.class)
public class HttpClientUserSyncServiceProvider extends AbstractService implements UserSyncService {

	private static final String SYNC_URLS = "syncUrls";
	private static final String NTHREADS = "nThreads";
	private static final String SYNC_PERSIST_PATH = "/rest/sync/persist";
	private static final String SYNC_UPDATE_PATH = "/rest/sync/update";
	private static final String SYNC_ENABLE_PATH = "/rest/sync/enable";
	private static final String SYNC_DISABLE_PATH = "/rest/sync/disable";
	
	private String syncUrls = null;
	private String[] urls = null;
	private Integer nThreads = null;
	private ThreadPoolExecutor poolExecutor = null;
	
	public HttpClientUserSyncServiceProvider(
			@Primary ConfigurationService configSvc,
			RegistryShutdownHub shutdownHub) throws Exception {
		super(shutdownHub);
		
		Configuration config = this.getConfiguration(configSvc);
    	this.init(config);
	}
	
	public void init(Configuration config) throws Exception {
		this.syncUrls = config.getString(HttpClientUserSyncServiceProvider.SYNC_URLS);
		this.urls = StringUtils.split(this.syncUrls, ",");
		this.nThreads = Integer.parseInt(config.getString(NTHREADS));
		this.poolExecutor = this.getThreadPool();
	}

	@Override
	public void persist(User user) {
		if (this.urls.length <= 0) {
			return;
		}
		
		HttpPost post = null;
		HttpEntity entity = null;
		try {
			entity = this.getUrlEncodedFormEntity(this.buildMap(user));
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		for (String url : this.urls) {
			post = new HttpPost(url + SYNC_PERSIST_PATH);
			post.setEntity(entity);
			this.poolExecutor.execute(new HttpPostTask(post));
		}
	}
	
	@Override
	public void enable(String... ids) {
		setVisible(true, ids);
	}
	
	@Override
	public void disable(String... ids) {
		setVisible(false, ids);
	}
	
	@Override
	public void update(User user) {
		if (this.urls.length <= 0) {
			return;
		}
		
		HttpPut put = null;
		HttpEntity entity = null;
		try {
			entity = this.getUrlEncodedFormEntity(this.buildMap(user));
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		
		for (String url : this.urls) {
			put = new HttpPut(url + SYNC_UPDATE_PATH);
			put.setEntity(entity);
			this.poolExecutor.execute(new HttpPutTask(put));
		}
	}
	
	private void setVisible(Boolean visible, String... ids) {
		if (this.urls.length <= 0) {
			return;
		}
		
		HttpPut put = null;
		HttpEntity entity = null;
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		
		StringBuffer buffer = null;
		for (String id : ids) {
			buffer = new StringBuffer();
			buffer.append(StringUtils.substring(id, StringUtils.indexOf(id, "="), StringUtils.indexOf(id, ","))).append(",");
		}
		if (buffer.length() > 0) {
			buffer.deleteCharAt(buffer.lastIndexOf(","));
		}
		formParams.add(new BasicNameValuePair("id", buffer.toString()));
		formParams.add(new BasicNameValuePair("status", visible.toString()));
		try {
			entity = new UrlEncodedFormEntity(formParams, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		String setVisibleUrl = null;
		if (visible) {
			setVisibleUrl = SYNC_ENABLE_PATH;
		} else {
			setVisibleUrl = SYNC_DISABLE_PATH;
		}
		for (String url : this.urls) {
			put = new HttpPut(url + setVisibleUrl);
			put.setEntity(entity);
			this.poolExecutor.execute(new HttpPutTask(put));
		}
	}

	
	private Map<String, Object> buildMap(Object obj) throws IllegalArgumentException, 
									IllegalAccessException, InvocationTargetException {
		Class<?> clazz = obj.getClass();
		Method methods[] = clazz.getDeclaredMethods();
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		for (int i = 0; i < methods.length; i ++) {
		   if (methods[i].getName().indexOf("get") == 0) {  
			   paramsMap.put(this.getFunctionNameToFieldName(methods[i].getName()), methods[i].invoke(obj, new Object[0]));
		   }
		}
		return paramsMap;
	}
	
	private String getFunctionNameToFieldName(String functionName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(Character.toLowerCase(functionName.charAt("get".length())))
			  .append(functionName.substring("get".length() + 1, functionName.length()));
		
		return buffer.toString();
	}
	
	private UrlEncodedFormEntity getUrlEncodedFormEntity(Map<String, Object> paramsMap) {
		List<NameValuePair> formParams = new ArrayList<NameValuePair>(paramsMap.size());
		for (String key : paramsMap.keySet()) {
			if (paramsMap.get(key) instanceof Boolean) {
				formParams.add(new BasicNameValuePair(key, paramsMap.get(key).toString()));
			} else if (paramsMap.get(key) instanceof String) {
				formParams.add(new BasicNameValuePair(key, (String) paramsMap.get(key)));
			} else if (paramsMap.get(key) instanceof Date) {
				formParams.add(new BasicNameValuePair(key, DatetimeUtils.formatDate((Date) paramsMap.get(key))));
			}
		}
		try {
			return new UrlEncodedFormEntity(formParams, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	private ThreadPoolExecutor getThreadPool() {
		return (ThreadPoolExecutor) Executors.newFixedThreadPool(this.nThreads);
	}

}
