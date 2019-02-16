package com.thales.IssuePrime.Helper;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.thales.IssuePrime.FieldsConfig.FieldsConfiguration;


public class IssueInputParametersHelper {
	
	private static final Logger log = LoggerFactory.getLogger(IssueInputParametersHelper.class);

	
	private IssueInputParametersHelper() {
		
	}
	
	public void setOptions(final CustomFieldManager customFieldManager, final String customFieldKey,
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
		
	}

}
