package com.thales.IssuePrime.FieldsConfig;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.util.json.JSONObject;


public class FieldsConfigurationTest {
	
	private static final Logger log = LoggerFactory.getLogger(FieldsConfigurationTest.class);


	public FieldsConfigurationTest() {
		
	}
	
	@Test
	public void testSomething() throws IOException {
		
		FieldsConfiguration fieldsConfiguration = new FieldsConfiguration();
		
		log.debug("fields config has been found= " + String.valueOf(fieldsConfiguration.isAvailable()));
		
		assertTrue("Field configuration has been found = ", fieldsConfiguration.isAvailable());
		
		String projectKey = "P4FLIGHT";
		String issueTypeName = "Problem Report";
		
		assertTrue("Project JSON object found - " , fieldsConfiguration.hasProjectKey(projectKey));
		assertTrue("Project JSON object found - ", fieldsConfiguration.getJsonProject(projectKey) != null);
		
		List<Map<String,Boolean>> fields = fieldsConfiguration.getMandatoryFields(projectKey, issueTypeName);
		Iterator<Map<String,Boolean>> iter = fields.iterator();
		
		log.debug("size of fields list= " + fields.size());
		while (iter.hasNext()) {
			Map<String,Boolean> fieldMap = iter.next();
			//log.debug(fieldMap.);
		}
		
		assertTrue ("Size of fields list > 0 - " , (fields.size()>0));
		JSONObject jsonProject = fieldsConfiguration.getJsonProject( projectKey );
		assertTrue("Project with key= P4FLIGHT - found - ", FieldsConfigurationProject.hasIssueTypeName(jsonProject, issueTypeName));
		
		projectKey = "CASE";
		issueTypeName = "Task";
		assertTrue("Project JSON object found - " , fieldsConfiguration.hasProjectKey(projectKey));
		jsonProject = fieldsConfiguration.getJsonProject( projectKey );
		assertTrue("Project JSON object found - ", jsonProject != null);

		assertTrue("Project JSON object found - ", fieldsConfiguration.getJsonProject(projectKey) != null);
		assertTrue("Fields config found for this project and this issue type", FieldsConfigurationProject.hasIssueTypeName(jsonProject, issueTypeName));

		
		projectKey = "NEOPTERYX";
		issueTypeName = "Problem Report";
		
		assertTrue("Project JSON object found - " , fieldsConfiguration.hasProjectKey(projectKey)==false);

		assertTrue("Project JSON object found - ", fieldsConfiguration.getJsonProject(projectKey) == null);
		jsonProject = fieldsConfiguration.getJsonProject( projectKey );
		assertTrue("Project JSON object found - ", jsonProject == null);

		assertTrue("Fields config found for this project and this issue type", FieldsConfigurationProject.hasIssueTypeName(jsonProject, issueTypeName)==false);

		projectKey = "P4FLIGHT";
		issueTypeName = "Problem Report";
		String customFieldId = "customfield_9988";
		assertTrue("custom field exists = ", fieldsConfiguration.hasCustomField(projectKey, issueTypeName, customFieldId));
		
		String customClassName = "com.atlassian.jira.plugin.system.customfieldtypes:select";
		assertTrue("custom fields with schema class = " , fieldsConfiguration.getCustomFieldsFromClass(projectKey, issueTypeName, customClassName).size()>0 );
	}
}
