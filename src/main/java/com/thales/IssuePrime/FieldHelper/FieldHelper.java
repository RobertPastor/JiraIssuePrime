package com.thales.IssuePrime.FieldHelper;


import java.util.Iterator;
import java.util.List;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.AttachmentPathManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;


public class FieldHelper {

	
	public FieldHelper() {
		
	}
	
	public boolean isFieldMandatoryForIssueType(final String fieldName, final IssueType issueType) {
		
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
		AttachmentPathManager attachmentPathManager = ComponentAccessor.getAttachmentPathManager();

		CustomField customFieldObject = customFieldManager.getCustomFieldObject(fieldName);
		
		// customFieldObjects = customFieldManager.getCustomFieldObjects(issue);

		//Object customFieldValue = customFieldObject.getValue(issue);//issue.getCustomFieldValue(cFieldObject)
		List<IssueType> issueTypeList  = customFieldObject.getAssociatedIssueTypes();
		 
		Iterator<IssueType> iter = issueTypeList.iterator();
		while (iter.hasNext()) {
			IssueType issueTypeInternal = iter.next();
			if (issueTypeInternal.equals(issueType)) {
				return true;
			}
		}
		return false;
	}
}
