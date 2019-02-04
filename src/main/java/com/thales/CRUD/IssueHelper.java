package com.thales.CRUD;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.fields.screen.FieldScreenRenderLayoutItem;
import com.atlassian.jira.issue.fields.screen.FieldScreenRenderer;
import com.atlassian.jira.issue.fields.screen.FieldScreenRendererFactory;

import com.atlassian.jira.issue.operation.IssueOperations;
import com.atlassian.jira.workflow.*;

public class IssueHelper {
	
	private static final Logger log = LoggerFactory.getLogger(IssueCRUD.class);

	private IssueHelper() {
	}
	
	
	/**
	 * get the fields need for an Issue Creation Operation
	 * @param user
	 * @param issue
	 * @return
	 */
	public static List<Field> getIssueRequiredCreateFields(ApplicationUser user, Issue issue) {
		
		List<Field> requiredCreateFields = new ArrayList<Field>();
		
		//FieldScreenRenderer fsr = FieldScreenRendererFactory.getFieldScreenRenderer( issue );
		
		//for (FieldScreenRenderLayoutItem fsrla : fsr.getRequiredFieldScreenRenderItems()) {
		//	requiredCreateFields.add(fsrla.getFieldScreenLayoutItem().getOrderableField());
		//}
		return requiredCreateFields;
	}
	
}
