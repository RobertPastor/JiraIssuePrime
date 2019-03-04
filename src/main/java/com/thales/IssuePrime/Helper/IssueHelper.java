package com.thales.IssuePrime.Helper;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.IssueService.IssueResult;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;

public class IssueHelper {

	private static final Logger log = LoggerFactory.getLogger(IssueHelper.class);

	private IssueHelper() {
	}

	public static MutableIssue getIssue(final String issueKey, 
			final JiraAuthenticationContext authenticationContext, 
			final IssueService issueService) {

		ApplicationUser user = authenticationContext.getLoggedInUser();
		IssueResult issueResult = issueService.getIssue(user, issueKey);

		if (issueResult.getErrorCollection().hasAnyErrors()) {

			return null;

		} else {

			return issueResult.getIssue();
		}
	}

	public static String getProjectKey(Issue issue) {

		return issue.getProjectObject().getKey();
	}

	public static String getProjectName(Issue issue) {

		return issue.getProjectObject().getName();
	}

	public static boolean isIssueTypeTask(Issue issue) {

		return ( issue.getIssueType().getName().equalsIgnoreCase("task") ||  issue.getIssueType().getName().equalsIgnoreCase("tâche") );
	}

	public static String getIssueTypeName(final Issue issue) {
		return issue.getIssueType().getName();
	}

	/**
	 * get the fields need for an Issue Creation Operation
	 * @param user
	 * @param issue
	 * @return
	 */
	public static List<Field> getIssueRequiredCreateFields(ApplicationUser user, Issue issue) {

		log.info("getIssueRequiredCreateFields");

		List<Field> requiredCreateFields = new ArrayList<Field>();

		//FieldScreenRenderer fsr = FieldScreenRendererFactory.getFieldScreenRenderer( issue );

		//for (FieldScreenRenderLayoutItem fsrla : fsr.getRequiredFieldScreenRenderItems()) {
		//	requiredCreateFields.add(fsrla.getFieldScreenLayoutItem().getOrderableField());
		//}
		return requiredCreateFields;
	}

	public static boolean isIssueTypeProblemReport(Issue issue) {

		return ( issue.getIssueType().getName().equalsIgnoreCase("problem report")  );

	}

}
