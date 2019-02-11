package com.thales.IssuePrime.FieldsConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONObject;

public class FieldsConfigurationProject {

	private static final Logger log = LoggerFactory.getLogger(FieldsConfigurationProject.class);

	/**
	 * {
    "expand": "projects",
    "projects": [
        {
            "self": "http://www.example.com/jira/rest/api/2/project/EX",
            "id": "10000",
            "key": "EX",
            "name": "Example Project",
            "avatarUrls": {
                "48x48": "http://www.example.com/jira/secure/projectavatar?pid=10000&avatarId=10011",
                "24x24": "http://www.example.com/jira/secure/projectavatar?size=small&pid=10000&avatarId=10011",
                "16x16": "http://www.example.com/jira/secure/projectavatar?size=xsmall&pid=10000&avatarId=10011",
                "32x32": "http://www.example.com/jira/secure/projectavatar?size=medium&pid=10000&avatarId=10011"
            },

             "issuetypes": [
                {
                    "self": "http://www.example.com/jira/rest/api/2/issueType/1",
                    "id": "1",
                    "description": "An error in the code",
                    "iconUrl": "http://www.example.com/jira/images/icons/issuetypes/bug.png",
                    "name": "Bug",
                    "subtask": false,
                    "fields": {
                        "issuetype": {
                            "required": true,
                            "name": "Issue Type",
                            "hasDefaultValue": false,
                            "operations": [
                                "set"
                            ]
                        }
                    }
                }
            ]
        }
	 */


	private FieldsConfigurationProject() {


	}

	public static boolean hasIssueTypeName(final JSONObject jsonProject, final String issueTypeName) {

		log.debug("FieldsConfigurationProject - hasIssueTypeName - searching for = " + issueTypeName);

		if (jsonProject != null) {

			JSONArray jsonIssueTypesArray = jsonProject.optJSONArray("issuetypes");

			if (jsonIssueTypesArray != null) {

				log.debug("FieldsConfigurationProject - hasIssueTypeName - issuesTypes array found for= " + issueTypeName);

				for(int j=0; j<jsonIssueTypesArray.length(); j++){

					JSONObject jsonIssueType = jsonIssueTypesArray.optJSONObject(j);

					if (jsonIssueType.optString("name", "").equalsIgnoreCase(issueTypeName)) {

						return true;
					}
				}
			} else {
				log.debug("FieldsConfigurationProject - hasIssueTypeName - Issue types field not found in json object");
			}
		}
		return false;
	}

	public static JSONObject getIssueType(final JSONObject jsonProject, final String issueTypeName) {


		if (jsonProject != null) {
			JSONArray jsonIssueTypesArray = jsonProject.optJSONArray("issuetypes");

			if (jsonIssueTypesArray != null) {

				for(int j=0; j<jsonIssueTypesArray.length(); j++){

					JSONObject jsonIssueType = jsonIssueTypesArray.optJSONObject(j);

					if (jsonIssueType.optString("name", "").equalsIgnoreCase(issueTypeName) ) {

						log.debug("FieldsConfigurationProject - getIssueType - issue type name = " + issueTypeName + " - found " );
						return jsonIssueType;
						//FieldsConfigurationIssueType fieldsConfigurationIssuetype = new FieldsConfigurationIssueType(jsonIssueType);

					}
				}
			}
		}
		return null;
	}

}
