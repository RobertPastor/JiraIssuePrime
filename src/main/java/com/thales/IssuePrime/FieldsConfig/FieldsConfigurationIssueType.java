package com.thales.IssuePrime.FieldsConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

	protected JSONObject jsonObject = null;

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


	protected FieldsConfigurationIssueType() {

		log.debug("---------------init-------------");
		init();
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
				BufferedReader streamReader = new BufferedReader( new InputStreamReader(inputStream, "UTF-8"));

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

	/**
	 * list of mandatory fields
	 * @param issueType
	 * @return
	 */
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

	/**
	 * returns true if custom field with id has been found
	 * @param issueTypeJsonObj
	 * @param customFieldId
	 * @return
	 */
	protected static boolean hasCustomFieldByID(final JSONObject issueTypeJsonObj, final String customFieldId) {


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

	protected static List<String> getCustomFieldsFromClass(final JSONObject issueTypeJsonObj, final String customClassName) {

		log.debug("--------------getCustomFieldsFromClass-----------------------");
		List<String> customFieldsIds = new ArrayList<>();

		JSONObject fieldsObject = issueTypeJsonObj.optJSONObject("fields");
		if (fieldsObject != null) {

			Iterator<String> iter = fieldsObject.keys();
			while (iter.hasNext()) {

				String fieldKey = iter.next();
				//log.debug("looking into field with key= " + fieldKey);

				JSONObject fieldObject = fieldsObject.optJSONObject(fieldKey);
				JSONObject fieldSchemaObject = fieldObject.optJSONObject("schema");
				if (fieldSchemaObject != null) {
					
					String fieldSchemaCustomValue = fieldSchemaObject.optString("custom", "");
					
					if (fieldSchemaCustomValue.equalsIgnoreCase(customClassName)) {
						
						log.debug("--------------getCustomFieldsFromClass---------- " + fieldObject.optString("name", "") + " ----name found---------------------");
						log.debug("--------------getCustomFieldsFromClass--schema found---------------------");
						log.debug("--------------getCustomFieldsFromClass------------schema class name found ++++++++++++---------------------");

						customFieldsIds.add(fieldKey);
					}
					
				}
			}
		}
		return customFieldsIds;

	}


}
