package com.thales.IssuePrime.RestApi;

import java.util.ArrayList;
import java.util.List;



public class RestApiFieldsClient {
	
	
	public RestApiFieldsClient(final String projectKey, final String issueTypeKey) {
		
		
	}
	
	public List<String> getFields() {
		List<String> fields = new ArrayList<String>();
		fields.add("Dummy One");
		fields.add("Dummy Two");
		
		return fields;
	}
	
}


