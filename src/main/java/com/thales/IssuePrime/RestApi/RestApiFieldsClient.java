package com.thales.IssuePrime.RestApi;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
//import com.atlassian.jira.rest.client.api.MetadataRestClient;
import com.atlassian.jira.issue.fields.Field;
//import com.atlassian.jira.rest.client.api.domain.Field;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

//import com.atlassian.util.concurrent.Promise; 
import com.atlassian.jira.rest.api.JiraRestClient;

public class RestApiFieldsClient {


	private URI jiraServerUri = null;

	private JiraRestClient restClient;
	private AsynchronousJiraRestClientFactory factory;

	public RestApiFieldsClient(final String projectKey, final String issueTypeKey) {


		String baseURL = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);
		/*
		 * get all fields for a PROJECT and an issue type name
		 * https://jira.atlassian.com/rest/api/latest/issue/createmeta?projectKeys=JRA&issuetypeName=Bug&expand=projects.issuetypes.fields
		 */

		this.jiraServerUri = URI.create(baseURL + "/rest/api/2/issue/createmeta?projectKeys=" + projectKey + "&issuetypeName=" + issueTypeKey + "&expand=projects.issuetypes.fields");

		this.factory = new AsynchronousJiraRestClientFactory();
		//this.factory.create(serverUri, authenticationHandler)
		//this.restClient = this.factory.create(this.jiraServerUri, authenticationHandler)
		this.restClient = this.factory.create(this.jiraServerUri, new AnonymousAuthenticationHandler());
	}

	public List<Field> getFields() {

		List<Field> fieldsList = new ArrayList();
		/*
		 * // Method descriptor #8 ()Lio/atlassian/util/concurrent/Promise;
		 * // Signature: ()Lio/atlassian/util/concurrent/Promise<Ljava/lang/Iterable<Lcom/atlassian/jira/rest/client/api/domain/Field;>;>;
		 * public abstract io.atlassian.util.concurrent.Promise getFields();
		 * 
		 */

		MetadataRestClient metaDataRestClient = restClient.getMetadataClient();

		Promise<Iterable<Field>> fields = metaDataRestClient.getFields();
		Iterable<Field> iterable = fields.claim();
		Iterator<Field> iterator = iterable.iterator();
		while (iterator.hasNext()) {

			Field field = iterator.next();
			fieldsList.add(field);

		}
		return fieldsList;
	}


}
