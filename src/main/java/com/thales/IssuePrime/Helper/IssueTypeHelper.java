package com.thales.IssuePrime.Helper;

import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;

public class IssueTypeHelper {

	private static final Logger log = LoggerFactory.getLogger(IssueHelper.class);

	private IssueTypeHelper() {

	}

	public static String getIssueTypeKey(final Project project, String issueTypeName) {

		Collection<IssueType> issueTypesList = project.getIssueTypes();
		Iterator<IssueType> iter = issueTypesList.iterator();
		while (iter.hasNext()) {
			IssueType issueTypeInternal = iter.next();
			if (issueTypeInternal.getName().equalsIgnoreCase(issueTypeName)) {
				return issueTypeInternal.getId();
			}
		}
		return "";
	}
	
	


}
