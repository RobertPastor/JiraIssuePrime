package com.thales.IssuePrime.Helper;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.thales.IssuePrime.FieldsConfig.FieldsConfiguration;


public class IssueInputParametersHelper {
	
	/**
	 * "customfield_9988": {
					"required": false,
					"schema": {
						"type": "option",
						"custom": "com.atlassian.jira.plugin.system.customfieldtypes:select",
						"customId": 9988
					},
					"name": "Reproducibility",
					"hasDefaultValue": false,
					"operations": ["set"],
					"allowedValues": [{
						"self": "https://rgswebsrv22.airsystems.thales:8443/rest/api/2/customFieldOption/10145",
						"value": "Systematic",
						"id": "10145"
					},
					{
						"self": "https://rgswebsrv22.airsystems.thales:8443/rest/api/2/customFieldOption/10146",
						"value": "Random",
						"id": "10146"
					},
					{
						"self": "https://rgswebsrv22.airsystems.thales:8443/rest/api/2/customFieldOption/10147",
						"value": "Seen Once",
						"id": "10147"
					}]
				},
				
	 * @param customFieldManager
	 * @param customFieldId
	 * @param customFieldName
	 * @param sourceIssue
	 */
	
	private static final Logger log = LoggerFactory.getLogger(IssueInputParametersHelper.class);

	private IssueInputParametersHelper() {
		
	}
	
	public void setReproducibilityOptions(final CustomFieldManager customFieldManager, final String customFieldId, final String customFieldName,
			final Issue sourceIssue) {
		
		
		// 9988 -> Reproducibility
		FieldsConfiguration fieldsConfiguration = new FieldsConfiguration();
		//CustomField customField = customFieldManager.getCustomFieldObject("customfield_9988");
		
		List<CustomField> customFields  = customFieldManager.getCustomFieldObjects();
		Iterator<CustomField> iter = customFields.iterator();
		while (iter.hasNext()) {
			CustomField customField = iter.next();
			
		}
		//customFieldManager.getCustomFieldObjects(sourceIssue)
		//..find {it.name == 'Hand Off'}  
		
		CustomField customField = customFieldManager.getCustomFieldObject(customFieldId);
		CustomField csUserField = customFieldManager.getCustomFieldObjectByName(customFieldName);

		// Retrieve a custom field type by its type key.
		String customFieldKey = "com.atlassian.jira.plugin.system.customfieldtypes:select";
		CustomFieldType customFieldType = customFieldManager.getCustomFieldType(customFieldKey);

		//OptionsManager optionsManager = new OptionsManager();
		FieldConfig fieldConfig = customField.getRelevantConfig(sourceIssue);
		
		Options options = ComponentAccessor.getOptionsManager().getOptions( fieldConfig );
		
		//issue.setCustomFieldValue(customFieldManager.getCustomFieldObjectByName("TECHNOLOGIE"), value);
		//Options fieldVal =(Options) issue.getCustomFieldValue(custom_field)
		
		/**
		 * "allowedValues": [{
						"self": "https://rgswebsrv22.airsystems.thales:8443/rest/api/2/customFieldOption/10145",
						"value": "Systematic",
						"id": "10145"
					},
		 */
		Option newOption = options.getOptionById(Long.valueOf("10145", 10));

		Object value = sourceIssue.getCustomFieldValue(customField);
		//ModifiedValue modifiedValue = new ModifiedValue( , newOption );
		ModifiedValue modifiedValue = new ModifiedValue(null, value);
		FieldLayoutItem fieldLayoutItem = ComponentAccessor.getFieldLayoutManager().getFieldLayout().getFieldLayoutItem(customField);
		
		customField.updateValue(fieldLayoutItem, sourceIssue, modifiedValue, new DefaultIssueChangeHolder());
		
	}

}
