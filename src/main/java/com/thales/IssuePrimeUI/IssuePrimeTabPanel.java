package com.thales.IssuePrimeUI;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.tabpanels.GenericMessageAction;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueTabPanel3;
import com.atlassian.jira.plugin.issuetabpanel.GetActionsRequest;
import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import com.atlassian.jira.plugin.issuetabpanel.ShowPanelRequest;

public class IssuePrimeTabPanel extends AbstractIssueTabPanel3
{
    private static final Logger log = LoggerFactory.getLogger(IssuePrimeTabPanel.class);
	
	@Override
	public boolean showPanel(ShowPanelRequest arg0) {
		log.info(" show Panel ");
		
		return true;
	}


	@Override
    public List<IssueAction> getActions(GetActionsRequest getActionsRequest) {
		
		log.info(" get Actions ");
		List<IssueAction> list = new ArrayList<IssueAction>();
		
		//list.add(new GenericMessageAction("Purpose"));
		//list.add(new GenericMessageAction(this.descriptor.getI18nBean().getText("This feature allows to prime the current issue.")));
		
		list.add(new IssuePrimeAction(descriptor, getActionsRequest.issue(), getActionsRequest.loggedInUser() ));
		
        return list;
    }


	
}
