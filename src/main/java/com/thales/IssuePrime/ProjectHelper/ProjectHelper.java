package com.thales.IssuePrime.ProjectHelper;

import java.util.Collection;
import java.util.Iterator;

import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;

public class ProjectHelper {

	public ProjectHelper() {
		
		
	}
	
	public boolean isIssueTypeInProject(final Project project, final IssueType issueType) {
		
		Collection<IssueType> issueTypesList = project.getIssueTypes();
		Iterator<IssueType> iter = issueTypesList.iterator();
		while (iter.hasNext()) {
			IssueType issueTypeInternal = iter.next();
			if (issueTypeInternal.equals(issueType)) {
				return true;
			}
		}
		return false;
	}
}
