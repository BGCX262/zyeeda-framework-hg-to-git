package com.zyeeda.framework.synch.internal;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpClientFactory {
	
	public static void sendPostRequest(HttpPost post) throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(post);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			Header[] headers = response.getAllHeaders();
			   for (int i = 0; i < headers.length; i++) {
System.out.println(headers[i]);
			}
		} else {
			throw new RuntimeException();
		}
	}
	
	public static void sendGetRequest(HttpGet get) throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			Header[] headers = response.getAllHeaders();
			   for (int i = 0; i < headers.length; i++) {
System.out.println(headers[i]);
			}
		} else {
			throw new RuntimeException();
		}
	}
	
	public static void sendPutRequest(HttpPut put) throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(put);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			Header[] headers = response.getAllHeaders();
			   for (int i = 0; i < headers.length; i++) {
System.out.println(headers[i]);
			}
		} else {
			throw new RuntimeException();
		}
	}
	
	public static void sendDeleteRequest(HttpDelete delete) throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(delete);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			Header[] headers = response.getAllHeaders();
			   for (int i = 0; i < headers.length; i++) {
System.out.println(headers[i]);
			}
		} else {
			throw new RuntimeException();
		}
	}
}
