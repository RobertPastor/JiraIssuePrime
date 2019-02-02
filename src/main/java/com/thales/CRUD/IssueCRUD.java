package com.thales.CRUD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.config.ConstantsManager;
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

		Map<String, Object> context = new HashMap<>();
		resp.setContentType("text/html;charset=utf-8");
		switch (action) {
		case "new":

			templateRenderer.render(NEW_ISSUE_TEMPLATE, context, resp.getWriter());
			break;

		default:

			templateRenderer.render(NEW_ISSUE_TEMPLATE, context, resp.getWriter());
			break;
			//log.error("Only new action type is implemented!!!");
			//resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		//String actionType = req.getParameter("actionType"); 
		String actionType = Optional.ofNullable(req.getParameter("actionType")).orElse("");

		switch (actionType) {

		case "new":
			handleIssueCreation(req, resp);
			break;

		default:
			handleIssueCreation(req, resp);
			break;
			
			
			//log.error("Only new action type is implemented!!!");
			//resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}



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
			.setDescription("Jira Prime - " + req.getParameter("description"))
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
			issueService.create(user, result);
			String redirection = "/jira";
			resp.sendRedirect(redirection);
		}
	}

	/*
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        resp.setContentType("text/html");
        resp.getWriter().write("<html><body>Hello World</body></html>");


    }
	 */

}