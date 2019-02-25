package com.thales.IssuePrime.Helper;

import java.util.Iterator;
import java.util.Set;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.label.Label;
import com.atlassian.jira.issue.label.LabelManager;

public class LabelsHelper {

	private LabelsHelper() {
		
		
	}
	
	public static void copyLabels ( final Issue sourceIssue, final Issue targetIssue ) {
		
		
		LabelManager labelManager = ComponentManager.getComponentInstanceOfType(LabelManager.class);
		Set<Label> labelSet = sourceIssue.getLabels();
		Iterator<Label> iter = labelSet.iterator();
		while (iter.hasNext()) {
			Label label = iter.next();
			labelManager.addLabel(targetIssue.getReporterUser(), targetIssue.getId(), label.getLabel(), false);
		}
		//labelManager.setLabels(targetIssue.getReporterUser(), targetIssue.getId() , sourceIssue.getLabels() , false,false);
	}
}
