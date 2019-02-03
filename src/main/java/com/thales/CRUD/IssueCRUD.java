package com.thales.CRUD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.IssueService.IssueResult;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.query.Query;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Scanned
public class IssueCRUD extends HttpServlet {
	private static final Logger log = LoggerFactory.getLogger(IssueCRUD.class);

	@JiraImport
	private IssueService issueService;
	@JiraImport
	private ProjectService projectService;
	@JiraImport
	private SearchService searchService;
	@JiraImport
	private TemplateRenderer templateRenderer;
	@JiraImport
	private JiraAuthenticationContext authenticationContext;
	@JiraImport
	private ConstantsManager constantsManager;

	private static final String LIST_ISSUES_TEMPLATE = "/templates/issueCrud/list.vm";
	private static final String NEW_ISSUE_TEMPLATE = "/templates/issueCrud/new.vm";
	private static final String EDIT_ISSUE_TEMPLATE = "/templates/issueCrud/edit.vm";

	public IssueCRUD(IssueService issueService, ProjectService projectService,
			SearchService searchService,
			TemplateRenderer templateRenderer,
			JiraAuthenticationContext authenticationContext,
			ConstantsManager constantsManager) {
		this.issueService = issueService;
		this.projectService = projectService;
		this.searchService = searchService;
		this.templateRenderer = templateRenderer;
		this.authenticationContext = authenticationContext;
		this.constantsManager = constantsManager;
	}



	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String action = Optional.ofNullable(req.getParameter("actionType")).orElse("");
		String issueType = Optional.ofNullable(req.getParameter("issueType")).orElse("");
		String issueKey = Optional.ofNullable(req.getParameter("issueKey")).orElse("");
		
		Map<String, Object> context = new HashMap<>();
		
		ApplicationUser user = authenticationContext.getLoggedInUser();
		IssueResult issueResult = issueService.getIssue(user, issueKey);
		if (issueResult.getErrorCollection().hasAnyErrors()) {
			
			context.put("errors", issueResult.getErrorCollection().getErrors());
			resp.setContentType("text/html;charset=utf-8");
			templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

			
		} else {
			
			MutableIssue issue = issueResult.getIssue();
			if (issue == null) {
				
				context.put("errors", Collections.singletonList("Error - cannot find issue with key= " + issueKey));
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
				return;
				
			} else {
				
				// send parameters to the velocity template
				context.put("action", action);
				context.put("issueType", issueType);
				context.put("issueKey", issueKey);
				
				// pre defined summary and description
				context.put("summary", issue.getSummary());
				context.put("description", issue.getDescription());
				
				resp.setContentType("text/html;charset=utf-8");
				
				switch (action) {
				case "new":

					templateRenderer.render(NEW_ISSUE_TEMPLATE, context, resp.getWriter());
					break;

				default:

					//log.error("Error - Only new action type is implemented!!!");
					//resp.sendError(HttpServletResponse.SC_NOT_FOUND);
					
					context.put("errors", Collections.singletonList("Error - Only new action type is implemented!!!"));
					templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
					return;
				}	
			}
			
		}
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		//String actionType = req.getParameter("actionType"); 
		String actionType = Optional.ofNullable(req.getParameter("actionType")).orElse("");
		String issueType = Optional.ofNullable(req.getParameter("issueType")).orElse("");

		switch (actionType) {

		case "new":
			handleIssueCreation(req, resp);
			break;

		default:
			handleIssueCreation(req, resp);
			break;

			//log.error("Only new action type is implemented!!!");
			//resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			//context.put("errors", Collections.singletonList("Project with key " + "TUTORIAL" + " doesn't exist"));
			//templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
			//return;
		}
	}


	/**
	 * Create an issue
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	private void handleIssueCreation(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		ApplicationUser user = authenticationContext.getLoggedInUser();

		Map<String, Object> context = new HashMap<>();

		Project project = projectService.getProjectByKey(user, "TUTORIAL").getProject();

		if (project == null) {
			context.put("errors", Collections.singletonList("Project with key " + "TUTORIAL" + " doesn't exist"));
			templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
			return;
		}

		// warning the type name is task in english or tâche in French
		IssueType taskIssueType = constantsManager.getAllIssueTypeObjects().stream().filter(
				issueType -> issueType.getName().equalsIgnoreCase("tâche")).findFirst().orElse(null);

		if (taskIssueType == null) {

			taskIssueType = constantsManager.getAllIssueTypeObjects().stream().filter(
					issueType -> issueType.getName().equalsIgnoreCase("task")).findFirst().orElse(null);
		}

		if(taskIssueType == null) {
			context.put("errors", Collections.singletonList("Can't find Task issue type or Tâche issue type -> will try to create only these issue types"));
			templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
			return;
		}

		IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
		issueInputParameters.setSummary(req.getParameter("summary"))
			.setDescription("Coflight-Prime - " + req.getParameter("description"))
			.setAssigneeId(user.getName())
			.setReporterId(user.getName())
			.setProjectId(project.getId())
			.setIssueTypeId(taskIssueType.getId());

		IssueService.CreateValidationResult result = issueService.validateCreate(user, issueInputParameters);

		if (result.getErrorCollection().hasAnyErrors()) {

			context.put("errors", result.getErrorCollection().getErrors());
			resp.setContentType("text/html;charset=utf-8");
			templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

		} else {
			
			IssueResult issueResult = issueService.create(user, result);
			MutableIssue issue = issueResult.getIssue();
			
			String baseURL = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);
			String redirection = "/jira/browse/" + issue.getProjectObject().getKey();
			resp.sendRedirect(redirection);
		}
	}


}