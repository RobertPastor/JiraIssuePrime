package com.thales.IssuePrime.IssuePrimeTabPanel;


import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueTabPanel3;
import com.atlassian.jira.plugin.issuetabpanel.GetActionsRequest;
import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import com.atlassian.jira.plugin.issuetabpanel.ShowPanelRequest;

public class IssuePrimeTabPanel extends AbstractIssueTabPanel3 {
	
	@Override
	public boolean showPanel(ShowPanelRequest showPanelRequest) {
		return true;
	}

	@Override
	public List<IssueAction> getActions(GetActionsRequest getActionsRequest) {

		List<IssueAction> list = new ArrayList<IssueAction>();

		list.add(new IssuePrimeAction(descriptor, getActionsRequest.issue(), getActionsRequest.loggedInUser() ));

		return list;

		
	}
}