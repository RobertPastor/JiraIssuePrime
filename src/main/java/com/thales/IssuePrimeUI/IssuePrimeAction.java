package com.thales.IssuePrimeUI;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueAction;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanelModuleDescriptor;
import com.atlassian.jira.template.VelocityTemplatingEngine;
import com.atlassian.jira.user.ApplicationUser;

import static com.atlassian.jira.template.TemplateSources.file;

public class IssuePrimeAction extends AbstractIssueAction {

    private static final Logger log = LoggerFactory.getLogger(IssuePrimeTabPanel.class);

	private final Issue issue;
	private final ApplicationUser remoteUser;

	private static final String PLUGIN_TEMPLATES = "templates/tabpanels/";

	public IssuePrimeAction(IssueTabPanelModuleDescriptor descriptor, Issue issue, ApplicationUser remoteUser) {
		super(descriptor);

		this.issue = issue;
		this.remoteUser = remoteUser;
	}

	@Override
	public Date getTimePerformed() {
		return issue.getCreated();

	}

	@Override
	protected void populateVelocityParams(Map params) {
		params.put("issue", issue);
		params.put("user", remoteUser);
		
		log.info(issue.getKey());
		log.info(issue.getIssueTypeId());
		log.info(issue.getIssueType().getName());
		log.info(issue.getProjectObject().getName());
		
	}


	public String getHtml() {
		final String templateName = "issue-prime-tab-panel.vm";
		final VelocityTemplatingEngine templatingEngine = ComponentAccessor.getComponent(VelocityTemplatingEngine.class);
		final Map<String, Object> params = new HashMap<String, Object>();
		populateVelocityParams(params);
		return templatingEngine.render(file(PLUGIN_TEMPLATES + templateName)).applying(params).asHtml();
	}



}
