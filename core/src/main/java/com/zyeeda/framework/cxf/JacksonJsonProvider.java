package com.zyeeda.framework.cxf;

import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class JacksonJsonProvider extends JacksonJaxbJsonProvider {

	public JacksonJsonProvider() {
		ObjectMapper m = this._mapperConfig.getConfiguredMapper();
		if (m == null) {
			m = this._mapperConfig.getDefaultMapper();
		}
		m.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
		m.configure(Feature.FAIL_ON_EMPTY_BEANS, false);
	}
	
}
