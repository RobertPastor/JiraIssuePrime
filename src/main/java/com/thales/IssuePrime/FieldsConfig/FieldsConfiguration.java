package com.thales.IssuePrime.FieldsConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONException;
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
public class FieldsConfiguration {

	private static final Logger log = LoggerFactory.getLogger(FieldsConfiguration.class);

	private  JSONObject jsonObject = null;

	public FieldsConfiguration() {
		
		init();
	}

	public boolean isAvailable() {
		return jsonObject != null;
	}

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
	public boolean hasCustomField(final String projectKey, final String issueTypeName, 
			final String customFieldId) {

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
	 * 
	 * @param projectKey
	 * @return
	 */
	public JSONObject getJsonProject(final String projectKey) {

		if (jsonObject != null) {

			JSONArray jsonProjectsArray = jsonObject.optJSONArray("projects");
			if (jsonProjectsArray != null) {

				for(int i=0; i<jsonProjectsArray.length(); i++){

					JSONObject jsonProject = jsonProjectsArray.optJSONObject(i);

					if ( jsonProject.optString("key","").equalsIgnoreCase(projectKey) ) {
						return jsonProject;
					}
				}
			}
		}
		return null;
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

	/**
	 * loads the JSON file containing the fields configuration
	 * @param projectKey
	 * @return
	 */
	private boolean init() {

		//String jsonFileName = projectKey + "-fields.json";
		String jsonFileName = "config/rgswebsrv22-fields.json";

		InputStream inputStream = null;
		jsonObject = null;
		try {
			inputStream = FieldsConfiguration.class.getClassLoader().getResourceAsStream(jsonFileName);

			if (inputStream != null) {
				BufferedReader streamReader = new BufferedReader(
						new InputStreamReader(inputStream, "UTF-8"));

				StringBuilder responseStrBuilder = new StringBuilder();

				String inputStr;
				while ((inputStr = streamReader.readLine()) != null) {
					responseStrBuilder.append(inputStr);
				}

				jsonObject = new JSONObject(responseStrBuilder.toString());
				return true;

			}
		} catch (UnsupportedEncodingException ex) {

			log.error(ex.getLocalizedMessage());

		} catch (IOException ex) {

			log.error(ex.getLocalizedMessage());

		} catch (Exception ex) {

			log.error(ex.getLocalizedMessage());

		}
		return false;
	}


}
