package com.zyeeda.framework.tomcat;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlSessionTrackingValve extends ValveBase {
	
	private final static Logger logger = LoggerFactory.getLogger(UrlSessionTrackingValve.class);
	
	//private final static String JSESSIONID_KEY = ";jsessionid=";
	
	private final static String JSESSIONID_PATTERN = "^(.*?)(?:\\;jsessionid=([^\\?#]*))?(\\?[^#]*)?(#.*)?$";

	@Override
	public void invoke(Request request, Response response) throws IOException,
			ServletException {
		/*String uri = request.getRequestURI();
		int start = uri.lastIndexOf(JSESSIONID_KEY);
		if (start > 0) {
			String jsessionid = uri.substring(start + JSESSIONID_KEY.length());
			logger.debug("Find jsessionid {} from url {}, set request session id.", jsessionid, uri);
			request.setRequestedSessionId(jsessionid);
			request.setRequestedSessionURL(true);
		}*/
		
		Pattern pattern = Pattern.compile(JSESSIONID_PATTERN, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(request.getRequestURI());
		if (matcher.matches()) {
			String jsessionid = matcher.group(2);
			if (jsessionid != null && !"".equals(jsessionid)) {
				logger.debug("Find jsessionid {} from url {}, set request session id.", jsessionid, request.getRequestURI());
				
				request.setRequestedSessionId(jsessionid);
				request.setRequestedSessionURL(true);
			}
		}
		
		this.getNext().invoke(request, response);
	}

}
