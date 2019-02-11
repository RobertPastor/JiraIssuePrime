package com.thales.IssuePrime.FieldsConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.util.json.JSONObject;

public class FieldsConfigurationIssueType {

	private static final Logger log = LoggerFactory.getLogger(FieldsConfigurationIssueType.class);


	/**
	 * "issuetypes": [
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
	 */


	public FieldsConfigurationIssueType() {


	}

	public static List<Map<String,Boolean>> getMandatoryFields(final JSONObject issueType ) {

		log.debug("FieldsConfigurationIssueType - getMandatoryFields");

		List<Map<String,Boolean>> fieldsList = new ArrayList<>();

		JSONObject fieldsObject = issueType.optJSONObject("fields");
		if (fieldsObject != null) {

			log.debug("FieldsConfigurationIssueType - getMandatoryFields - key = fields found");

			Iterator<String> iter = fieldsObject.keys();
			while (iter.hasNext()) {

				String fieldName = iter.next();
				JSONObject fieldObject = fieldsObject.optJSONObject(fieldName);

				boolean bool = fieldObject.optBoolean("required", false);
				if (bool) {

					log.debug("FieldsConfigurationIssueType - getMandatoryFields - field name = " + fieldName + " - is required");

					Map<String,Boolean> map = new HashMap<>();
					map.put(fieldName, fieldObject.optBoolean("required", false));
					fieldsList.add(map);
				}
			}

		} else {
			log.debug("FieldsConfigurationIssueType - getMandatoryFields - key = fields NOT found");
		}
		return fieldsList;
	}

	public static boolean hasCustomFieldByID(final JSONObject issueTypeJsonObj, final String customFieldId) {


		JSONObject fieldsObject = issueTypeJsonObj.optJSONObject("fields");
		if (fieldsObject != null) {
			
			Iterator<String> iter = fieldsObject.keys();
			while (iter.hasNext()) {

				String fieldKey = iter.next();

				if (fieldKey.equalsIgnoreCase(customFieldId) ) {
					return true;
				}
			}
		}
		return false;
	}
	

}
