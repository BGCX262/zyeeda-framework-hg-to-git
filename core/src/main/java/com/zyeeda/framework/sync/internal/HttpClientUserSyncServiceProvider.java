package com.zyeeda.framework.sync.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;

import com.zyeeda.framework.config.ConfigurationService;
import com.zyeeda.framework.entities.User;
import com.zyeeda.framework.service.AbstractService;
import com.zyeeda.framework.sync.UserSyncService;

@ServiceId("httpclient-user-sync-service")
@Marker(Primary.class)
public class HttpClientUserSyncServiceProvider extends AbstractService implements UserSyncService {

	private static final String SYNC_URLS = "syncUrls";
	private static final String CORE_POOL_SIZE = "corePoolSize";
	private static final String KEEP_ALIVE_TIME = "keepAliveTime";
	private static final String MAXIMUM_POOL_SIZE = "maximumPoolSize";
	
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
		this.poolExecutor = this.getThreadPool();
		this.corePoolSize = Integer.parseInt(config.getString(HttpClientUserSyncServiceProvider.CORE_POOL_SIZE));
		this.keepAliveTime = Integer.parseInt(config.getString(HttpClientUserSyncServiceProvider.KEEP_ALIVE_TIME));
		this.maximumPoolSize = Integer.parseInt(config.getString(HttpClientUserSyncServiceProvider.MAXIMUM_POOL_SIZE));
		
	}

	@Override
	public void persist(User user) {
		if (this.urls.length <= 0) {
			return;
		}
		
		HttpPost post = null;
		try {
			HttpParams params = this.buildHttpParams(this.buildMap(user));
			for (String url : this.urls) {
				post = new HttpPost(url);
				post.setParams(params);
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
		HttpParams params = null;
		for (String id : ids) {
			params = new BasicHttpParams();
			params.setParameter("id", id);
			params.setParameter("visible", visible);
			for (String url : this.urls) {
				put = new HttpPut(url);
				put.setParams(params);
				this.poolExecutor.execute(new HttpPutTask(put));
			}
		}
	}

	@Override
	public void update(User user) {
		if (this.urls.length <= 0) {
			return;
		}
		HttpPut put = null;
		try {
			HttpParams params = this.buildHttpParams(this.buildMap(user));
			for (String url : this.urls) {
				put = new HttpPut(url);
				put.setParams(params);
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
			   paramsMap.put(methods[i].getName(), methods[i].invoke(obj, new Object[0]));  
		   }  
		}
		return paramsMap;
	}
	
	private HttpParams buildHttpParams(Map<String, Object> paramsMap) {
		HttpParams params = new BasicHttpParams();
		for (String key : paramsMap.keySet()) {
			params.setParameter(key, paramsMap.get(key));
		}
		return params;
	}
	
	private ThreadPoolExecutor getThreadPool() {
		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
				this.corePoolSize, this.maximumPoolSize, this.keepAliveTime,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2),
				new ThreadPoolExecutor.DiscardOldestPolicy());
		
		return threadPool;
	}

}
