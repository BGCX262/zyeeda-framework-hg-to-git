package com.zyeeda.framework.synch.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.Configuration;
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
import com.zyeeda.framework.synch.UserSynchService;

@ServiceId("user-synch-service")
@Marker(Primary.class)
public class UserSynchServiceProvider extends AbstractService implements UserSynchService/*, Runnable */{

	private String synchUrls = null;
	private List<String> urls = null;
	
	public UserSynchServiceProvider(ConfigurationService configSvc,
			RegistryShutdownHub shutdownHub) throws Exception {
		super(shutdownHub);
		
		Configuration config = this.getConfiguration(configSvc);
    	this.init(config);
	}
	
	public void init(Configuration config) throws Exception {
		this.synchUrls = config.getString("synchUrls");
		this.urls = this.tokenSynchUrls(synchUrls, ",");
	}

	@Override
	public void persist(User user) {
		HttpPost post = null;
		if (this.urls.size() > 0) {
			try {
				HttpParams params = this.buildHttpParams(this.buildMap(user));
				ThreadPoolExecutor threadPool = this.getThreadPool();
				for (String url : this.urls) {
					post = new HttpPost(url);
					post.setParams(params);
					threadPool.execute(new HttpPostTask(post));
				}
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	public void setVisible(Boolean visible, String... ids) {
		HttpPut put = null;
		if (this.urls.size() > 0) {
			HttpParams params = null;
			ThreadPoolExecutor threadPool = this.getThreadPool();
			for (String id : ids) {
				params = new BasicHttpParams();
				params.setParameter("id", id);
				params.setParameter("visible", visible);
				for (String url : this.urls) {
					put = new HttpPut(url);
					put.setParams(params);
					threadPool.execute(new HttpPutTask(put));
				}
			}
		}
	}

	@Override
	public void update(User user) {
		HttpPut put = null;
		if (this.urls.size() > 0) {
			try {
				HttpParams params = this.buildHttpParams(this.buildMap(user));
				ThreadPoolExecutor threadPool = this.getThreadPool();
				for (String url : this.urls) {
					put = new HttpPut(url);
					put.setParams(params);
					threadPool.execute(new HttpPutTask(put));
				}
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private List<String> tokenSynchUrls(String synchUrls, String delim) {
		StringTokenizer tokenizer = new StringTokenizer(synchUrls, delim);
		List<String> urls = new ArrayList<String>();
		
		while (tokenizer.hasMoreElements()) {
			urls.add(tokenizer.nextToken());
		}
		
		return urls;
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
		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2, 3, 3,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2),
				new ThreadPoolExecutor.DiscardOldestPolicy());
		
		return threadPool;
	}

}
