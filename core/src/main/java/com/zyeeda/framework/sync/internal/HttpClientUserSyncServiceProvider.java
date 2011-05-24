package com.zyeeda.framework.sync.internal;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
	private static final String CORE_POOL_SIZE = "corePoolSize";
	private static final String KEEP_ALIVE_TIME = "keepAliveTime";
	private static final String MAXIMUM_POOL_SIZE = "maximumPoolSize";
	private static final String SYNC_ADD_PATH = "/rest/sync/persist";
	private static final String SYNC_UPDATE_PATH = "/rest/sync/update";
	private static final String SYNC_SET_VISIBLE_PATH = "/rest/sync/setVisible";
	
	
	private String syncUrls = null;
	private String[] urls = null;
	private Integer corePoolSize = null;
	private Integer keepAliveTime = null;
	private Integer maximumPoolSize = null;
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
		this.corePoolSize = Integer.parseInt(config.getString(CORE_POOL_SIZE));
		this.keepAliveTime = Integer.parseInt(config.getString(KEEP_ALIVE_TIME));
		this.maximumPoolSize = Integer.parseInt(config.getString(MAXIMUM_POOL_SIZE));
		this.poolExecutor = this.getThreadPool();
	}

	@Override
	public void persist(User user) {
		if (this.urls.length <= 0) {
			return;
		}
		
		HttpPost post = null;
		try {
			HttpEntity entity = this.getUrlEncodedFormEntity(this.buildMap(user));
			for (String url : this.urls) {
				post = new HttpPost(url + SYNC_ADD_PATH);
				post.setEntity(entity);
				this.poolExecutor.execute(new HttpPostTask(post));
			}
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void setVisible(Boolean visible, String... ids) {
		if (this.urls.length <= 0) {
			return;
		}
		
		HttpPut put = null;
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		HttpEntity entity = null;
		
		try {
			for (String id : ids) {
				formParams.add(new BasicNameValuePair("id", id.substring(id.indexOf("=") + 1, id.indexOf(","))));
				formParams.add(new BasicNameValuePair("status", visible.toString()));
				entity = new UrlEncodedFormEntity(formParams, "utf-8");
				for (String url : this.urls) {
					put = new HttpPut(url + SYNC_SET_VISIBLE_PATH);
					put.setEntity(entity);
					this.poolExecutor.execute(new HttpPutTask(put));
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(User user) {
		if (this.urls.length <= 0) {
			return;
		}
		
		HttpPut put = null;
		try {
			HttpEntity entity = this.getUrlEncodedFormEntity(this.buildMap(user));
			for (String url : this.urls) {
				put = new HttpPut(url + SYNC_UPDATE_PATH);
				put.setEntity(entity);
				this.poolExecutor.execute(new HttpPutTask(put));
			}
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> buildMap(Object obj) throws IllegalArgumentException, 
									IllegalAccessException, InvocationTargetException {
		Class clazz = obj.getClass();
		Method methods[] = clazz.getDeclaredMethods();
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		for (int i = 0; i < methods.length; i++) {  
		   if (methods[i].getName().indexOf("get") == 0) {  
			   paramsMap.put(this.getFunctionNameToFieldName(methods[i].getName()), methods[i].invoke(obj, new Object[0]));
		   }  
		}
		return paramsMap;
	}
	
	private String getFunctionNameToFieldName(String functionName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(Character.toLowerCase(functionName.charAt(3))).append(functionName.substring(4, functionName.length()));
		
		return buffer.toString();
	}
	
	private UrlEncodedFormEntity getUrlEncodedFormEntity(Map<String, Object> paramsMap) {
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		for (String key : paramsMap.keySet()) {
			if (paramsMap.get(key) instanceof Boolean) {
				formParams.add(new BasicNameValuePair(key, paramsMap.get(key).toString()));
			} else if (paramsMap.get(key) instanceof String) {
				formParams.add(new BasicNameValuePair(key, (String) paramsMap.get(key)));
			} else if (paramsMap.get(key) instanceof Date) {
				formParams.add(new BasicNameValuePair(key, DatetimeUtils.formatDate((Date) paramsMap.get(key))));
			}
		}
		UrlEncodedFormEntity uefEntity = null;
		try {
			uefEntity = new UrlEncodedFormEntity(formParams, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return uefEntity;
	}
	
	private ThreadPoolExecutor getThreadPool() {
		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
				this.corePoolSize, this.maximumPoolSize, this.keepAliveTime,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2),
				new ThreadPoolExecutor.DiscardOldestPolicy());
		
		return threadPool;
	}

}
