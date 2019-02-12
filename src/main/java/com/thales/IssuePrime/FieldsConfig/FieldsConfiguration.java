package com.thales.IssuePrime.FieldsConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONObject;


/**
 * The purpose of this class is to read the fields configuration
 * Fields configuration depends upon the Issue Type and the Project, and the JIRA server instance configuration
 * @author t0007330
 * 
 * /**
 * {
    "expand": "projects",
    "projects": [
        {
            "self": "http://www.example.com/jira/rest/api/2/project/EX",
            "id": "10000",
            "key": "EX",
            "name": "Example Project",
 *
 */
public class FieldsConfiguration extends FieldsConfigurationProject {

	private static final Logger log = LoggerFactory.getLogger(FieldsConfiguration.class);


	public FieldsConfiguration() {

		super();
	}

	public boolean isAvailable() {
		return jsonObject != null;
	}



	/**
	 * retrieves the list of mandatory fields
	 * @param projectKey
	 * @param issueTypeName
	 * @return
	 */
	public  List<Map<String,Boolean>> getMandatoryFields(final String projectKey, final String issueTypeName) {


		if (hasProjectKey(projectKey)) {

			log.debug("FieldsConfiguration - getMandatoryFields - project with key= " + projectKey + " - found");

			if (FieldsConfigurationProject.hasIssueTypeName(getJsonProject(projectKey) ,issueTypeName)) {

				log.debug("FieldsConfiguration - getMandatoryFields - issue Type found with name= " + issueTypeName);

				JSONObject issueTypeJsonObj = FieldsConfigurationProject.getIssueType(getJsonProject(projectKey), issueTypeName);

				return FieldsConfigurationIssueType.getMandatoryFields(issueTypeJsonObj);

			} else {
				log.debug("FieldsConfiguration - getMandatoryFields - Issue type with name= " + issueTypeName + " - not found");
			}
		} else {
			log.debug("FieldsConfiguration - getMandatoryFields - project with key= " + projectKey + " - not found in the config file");
		}
		return new ArrayList<>();
	}


	/**
	 * check that customfield such as customfield_9988 is available for a project key and an issue type (Problem Report)
	 * @param projectKey
	 * @param issueTypeName
	 * @param customFieldId
	 * @return
	 */
	public boolean hasCustomField(final String projectKey, final String issueTypeName, final String customFieldId) {

		if (hasProjectKey(projectKey)) {

			log.debug("FieldsConfiguration - getMandatoryFields - project with key= " + projectKey + " - found");

			if (FieldsConfigurationProject.hasIssueTypeName(getJsonProject(projectKey) , issueTypeName)) {

				JSONObject issueTypeJsonObj = FieldsConfigurationProject.getIssueType(getJsonProject(projectKey), issueTypeName);
				return FieldsConfigurationIssueType.hasCustomFieldByID(issueTypeJsonObj, customFieldId);

			}
		}
		return false;
	}
	
	
	/**
	 * 						"custom": "com.atlassian.jira.plugin.system.customfieldtypes:select",

	 * @param projectKey
	 * @param issueTypeName
	 * @return
	 */
	public List<String> getCustomFieldsFromClass(final String projectKey, final String issueTypeName, final String customClassName) {
		
		List<String> customFieldsIds = new ArrayList<>();
		
		JSONObject jsonProjectObject = getJsonProject( projectKey );
		if (jsonProjectObject != null) {
			
			JSONObject jsonIssueTypeObject = getIssueType(jsonProjectObject, issueTypeName);
			if ( jsonIssueTypeObject != null) {
				
				return getCustomFieldsFromClass(jsonIssueTypeObject, customClassName);
			}
		}
		return customFieldsIds;
	}
	
	
	/**
	 * 
	 * check that the fields configuration contains this project key
	 * @param projectKey
	 * @return
	 */
	public  boolean hasProjectKey(final String projectKey) {

		log.debug("FieldsConfiguration - trying to find project with key= " + projectKey);

		if (jsonObject != null) {

			log.debug("FieldsConfiguration - hasProjectKey - json object is not null");

			JSONArray jsonProjectsArray = jsonObject.optJSONArray("projects");
			if (jsonProjectsArray != null) {

				for(int i=0; i<jsonProjectsArray.length(); i++){

					JSONObject jsonProject = jsonProjectsArray.optJSONObject(i);
					log.debug("FieldsConfiguration - project name found= " + jsonProject.optString("name", "default to empty string"));
					log.debug("FieldsConfiguration - project key found= " + jsonProject.optString("key", "default to empty string"));

					if ( jsonProject.optString("key","").equalsIgnoreCase(projectKey) ) {
						log.debug("FieldsConfiguration - project found");
						return true;
					} else {
						log.debug("FieldsConfiguration - it is not this one = " + jsonProject.optString("key"));
					}
				}
			} else {
				log.debug("FieldsConfiguration - roperty 'projects' not found");
			}
		}
		return false;
	}




}
