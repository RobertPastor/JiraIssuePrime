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
import com.thales.IssuePrime.FieldsConfig.FieldsConfiguration;
import com.thales.IssuePrime.FieldsConfig.FieldsConfigurationProject;
import com.thales.IssuePrime.Helper.IssueHelper;

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
		
		FieldsConfiguration fieldsConfiguration = new FieldsConfiguration();
		boolean fieldsConfigAvailable = fieldsConfiguration.isAvailable();
		params.put("fieldsConfig", fieldsConfigAvailable);
		
		log.debug(String.valueOf(fieldsConfigAvailable));
		
		String projectKey = IssueHelper.getProjectKey(issue);
		boolean projectFieldsConfigAvailable = fieldsConfiguration.hasProjectKey(projectKey);
		params.put("projectFieldsConfig", projectFieldsConfigAvailable);
		
		log.debug(String.valueOf(projectFieldsConfigAvailable));
		
		String issueTypeName = IssueHelper.getIssueTypeName(issue);
		boolean issueTypeFieldsConfigAvailable = FieldsConfigurationProject.hasIssueTypeName(fieldsConfiguration.getJsonProject(projectKey), issueTypeName);
		params.put("issueTypeFieldsConfig", issueTypeFieldsConfigAvailable);
		
		log.debug(String.valueOf(issueTypeFieldsConfigAvailable));

		log.debug(issue.getKey());
		log.debug(issue.getIssueTypeId());
		log.debug(issue.getIssueType().getName());
		log.debug(issue.getProjectObject().getName());
		log.debug( String.valueOf( fieldsConfigAvailable ) );
	}

	@Override
	public String getHtml() {
		
		final String templateName = "issue-prime-tab-panel.vm";
		final VelocityTemplatingEngine templatingEngine = ComponentAccessor.getComponent(VelocityTemplatingEngine.class);
		final Map<String, Object> params = new HashMap<String, Object>();
		populateVelocityParams(params);
		return templatingEngine.render(file(PLUGIN_TEMPLATES + templateName)).applying(params).asHtml();
		
	}

}
