package com.thales.IssuePrime.Helper;

import java.util.Collection;
import java.util.Iterator;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;

public class ProjectHelper {

	public ProjectHelper() {
		
		
	}
	
	public static boolean isProjectRhythmEnabled(Issue issue) {

		Project project = issue.getProjectObject();
		String projectCat = "";
		if( project.getProjectCategoryObject() != null) {
            projectCat = project.getProjectCategoryObject().getName();
        }
        return projectCat != null && (projectCat.equalsIgnoreCase("Rhythm Enabled") || projectCat.endsWith("(Rhythm)"));
        
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
