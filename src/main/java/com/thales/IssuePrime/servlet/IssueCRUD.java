package com.thales.IssuePrime.servlet;

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
import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.thales.IssuePrime.FieldsConfig.FieldsConfiguration;
import com.thales.IssuePrime.Helper.AttachmentHelper;
import com.thales.IssuePrime.Helper.CommentsHelper;
import com.thales.IssuePrime.Helper.IssueHelper;
import com.thales.IssuePrime.Helper.IssueLinkHelper;
import com.thales.IssuePrime.Helper.IssueTypeHelper;
import com.thales.IssuePrime.Helper.LabelsHelper;
import com.thales.IssuePrime.Helper.ProjectHelper;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.atlassian.jira.bc.issue.IssueService.CreateValidationResult;

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

	private static final String ACTION_TYPE_REQUEST_PARAMETER = "actionType";
	private static final String ISSUE_TYPE_REQUEST_PARAMETER = "issueType";
	private static final String ISSUE_KEY_REQUEST_PARAMETER = "issueKey";
	private static final String PROJECT_KEY_REQUEST_PARAMETER = "projectKey";

	private static final String ISSUE_TYPE_TASK = "task";
	private static final String ISSUE_TYPE_TACHE = "tâche";
	private static final String ISSUE_TYPE_PROBLEM_REPORT  = "problem report";

	private static final String ERRORS = "errors";
	private static final String RESULTS = "results";

	private static final String LIST_ISSUES_TEMPLATE = "/templates/issueCrud/list.vm";
	private static final String NEW_ISSUE_TEMPLATE = "/templates/issueCrud/new.vm";
	private static final String EDIT_ISSUE_TEMPLATE = "/templates/issueCrud/edit.vm";

	private static final String NEW_TASK_ISSUE_TEMPLATE = "/templates/issueCrud/newTask.vm";
	private static final String NEW_PROBLEM_REPORT_ISSUE_TEMPLATE = "/templates/issueCrud/newProblemReport.vm";
	private static final String CREATED_ISSUES_TEMPLATE = "/templates/issueCrud/created.vm";


	public IssueCRUD(IssueService issueService, 
			ProjectService projectService,
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


	private List<String> getMandatoryFieldsList(final String projectKey, final String issueType) {

		List<String> fieldsList = new ArrayList<>();
		FieldsConfiguration fieldsConfiguration = new FieldsConfiguration();
		List<Map<String,Boolean>> fieldsMapList = fieldsConfiguration.getMandatoryFields(projectKey, issueType);
		Iterator<Map<String,Boolean>> iterMapOne = fieldsMapList.iterator();
		while(iterMapOne.hasNext()) {
			Map<String,Boolean> fieldMap = iterMapOne.next();
			Map.Entry<String, Boolean> entry = fieldMap.entrySet().iterator().next();
			fieldsList.add(entry.getKey());
		}
		return fieldsList;
	}

	/**
	 * 
	 * @param req
	 * @param resp
	 * @param issue
	 * @param context
	 * @throws RenderingException
	 * @throws IOException
	 */
	private void prepareAndRenderNewIssueTemplate(HttpServletRequest req, HttpServletResponse resp, 
			Issue issue, Map<String, Object> context) 
					throws RenderingException, IOException {

		try {

			String actionType = Optional.ofNullable(req.getParameter(ACTION_TYPE_REQUEST_PARAMETER)).orElse("");
			String issueType = Optional.ofNullable(req.getParameter(ISSUE_TYPE_REQUEST_PARAMETER)).orElse("");
			String issueKey = Optional.ofNullable(req.getParameter(ISSUE_KEY_REQUEST_PARAMETER)).orElse("");

			// send parameters to the velocity template
			context.put(ACTION_TYPE_REQUEST_PARAMETER, actionType);
			context.put(ISSUE_TYPE_REQUEST_PARAMETER, issueType);
			context.put(ISSUE_KEY_REQUEST_PARAMETER, issueKey);
			context.put("issue", issue);

			log.info(issue.getSummary());
			log.info(issue.getDescription());

			//log.info(issue.getAssigneeId());
			log.info(issue.getReporterId());

			String projectKey = IssueHelper.getProjectKey(issue);

			resp.setContentType("text/html;charset=utf-8");

			switch (actionType) {
			case "new":

				if (issueType.equalsIgnoreCase(ISSUE_TYPE_TASK)) {

					List<String> fieldsList = getMandatoryFieldsList( projectKey, issueType);
					context.put("fields", fieldsList);

					log.debug("cloning a task");
					templateRenderer.render(NEW_TASK_ISSUE_TEMPLATE, context, resp.getWriter());

				} else {

					if (issueType.equalsIgnoreCase(ISSUE_TYPE_PROBLEM_REPORT)) {

						log.debug("Cloning a Problem Report");

						context.put("projectKey", projectKey);
						context.put("isRhythmEnabled", ProjectHelper.isProjectRhythmEnabled(issue));

						String issueTypeKey = IssueTypeHelper.getIssueTypeKey(issue.getProjectObject(), "Problem Report");
						log.debug("searching issue type key= " + issueTypeKey);
						context.put("issueTypeKey", issueTypeKey);

						List<String> fieldsList = getMandatoryFieldsList( projectKey, issueType);
						context.put("fields", fieldsList);

						templateRenderer.render(NEW_PROBLEM_REPORT_ISSUE_TEMPLATE, context, resp.getWriter());

					} else {

						context.put(ERRORS, Collections.singletonList("Error - unknow Issue Type - " + issueType + " - not implemented!!!"));
						templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

					}
				}
				break;

			default:

				context.put(ERRORS, Collections.singletonList("Error - Only new action type is implemented!!!"));
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

			}	

		} catch (Exception ex) {
			context.put(ERRORS, Collections.singletonList(ex.getLocalizedMessage()));
			templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
		}

	}


	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		Map<String, Object> context = new HashMap<>();
		try {

			String actionType = Optional.ofNullable(req.getParameter(ACTION_TYPE_REQUEST_PARAMETER)).orElse("");
			String issueType = Optional.ofNullable(req.getParameter(ISSUE_TYPE_REQUEST_PARAMETER)).orElse("");
			String issueKey = Optional.ofNullable(req.getParameter(ISSUE_KEY_REQUEST_PARAMETER)).orElse("");

			log.debug("Action type is= {}" , actionType);
			log.debug("Issue type is= {}" , issueType);
			log.debug("Issue Key is= {}" , issueKey);

			if ((actionType.length()==0) || (issueType.length()==0) || (issueKey.length()==0)) {

				context.put(ERRORS, Collections.singletonList("Action cancelled by the user or insufficient parameters provided by the user"));
				resp.setContentType("text/html;charset=utf-8");
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

			} else {

				ApplicationUser user = authenticationContext.getLoggedInUser();
				log.debug("logged in user= {}" , user.getKey());
				
				IssueResult issueResult = issueService.getIssue(user, issueKey);
				if (!issueResult.isValid() || issueResult.getErrorCollection().hasAnyErrors()) {

					log.debug("issue result is not valid");
					context.put(ERRORS, issueResult.getErrorCollection().getErrors());
					resp.setContentType("text/html;charset=utf-8");
					templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

				} else {

					log.debug("issue result is valid");

					Issue issue = issueResult.getIssue();
					if (issue == null) {

						context.put(ERRORS, Collections.singletonList("Error - cannot find issue with key= " + issueKey));
						templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

					} else {
						// prepare for rendering a velocity template
						prepareAndRenderNewIssueTemplate(req, resp, issue, context);
					}
				}
			}
		} catch (Exception ex) {

			context.put(ERRORS, Collections.singletonList(ex.getLocalizedMessage()));
			try {
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
			} catch (Exception exc) {
				log.error("Error while rendering {} - exception= {}" ,  LIST_ISSUES_TEMPLATE , exc.getMessage());
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
			}

		}

	}

	private List<Issue> getIssues() {   
		ApplicationUser user = authenticationContext.getLoggedInUser();
		JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
		Query query = jqlClauseBuilder.project("TUTORIAL").buildQuery();
		PagerFilter pagerFilter = PagerFilter.getUnlimitedFilter();

		SearchResults searchResults = null;
		try {
			searchResults = searchService.search(user, query, pagerFilter);
		} catch (SearchException e) {
			e.printStackTrace();
		}
		return searchResults != null ? searchResults.getIssues() : null;
	}


	/**
	 * Create an issue - cloning one.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


		Map<String, Object> context = new HashMap<>();
		try {

			String actionType = Optional.ofNullable(req.getParameter(ACTION_TYPE_REQUEST_PARAMETER)).orElse("");
			String targetIssueType = Optional.ofNullable(req.getParameter(ISSUE_TYPE_REQUEST_PARAMETER)).orElse("");
			String issueKey = Optional.ofNullable(req.getParameter(ISSUE_KEY_REQUEST_PARAMETER)).orElse("");

			switch (actionType) {

			case "new":

				if ( targetIssueType.equalsIgnoreCase(ISSUE_TYPE_TACHE) || 
						targetIssueType.equalsIgnoreCase(ISSUE_TYPE_TASK) ) {

					// create the task
					log.debug("handle task creation");
					handleTaskCreation(req, resp, issueKey);

				} else {

					if ( targetIssueType.equalsIgnoreCase(ISSUE_TYPE_PROBLEM_REPORT) ) {
						log.debug("handle Problem Report creation");
						handleProblemReportCreation(req, resp, issueKey);

					} else {

						log.error("Only new issue type Task or Problem Report is implemented!!!");
						context.put(ERRORS, Collections.singletonList("Only new action type TASK or TÂCHE is implemented - you tried= " + targetIssueType));
						templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

					}
				}
				break;

			default:

				log.error("Only new action type TASK or TÂCHE is implemented!!!");
				context.put(ERRORS, Collections.singletonList("Only new action type TASK or TÂCHE is implemented - you tried= " + targetIssueType));
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

			}
		} catch (Exception ex) {

			context.put(ERRORS, Collections.singletonList(ex.getLocalizedMessage()));
			templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
		}
	}


	/**
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	private void handleProblemReportCreation(HttpServletRequest req, HttpServletResponse resp, String issueKey) throws IOException {

		ApplicationUser user = authenticationContext.getLoggedInUser();
		log.debug("issue key= " + issueKey);

		// context used for the velocity templates
		Map<String, Object> context = new HashMap<>();

		try {

			String targetPrimeProjectKey =  Optional.ofNullable(req.getParameter(PROJECT_KEY_REQUEST_PARAMETER)).orElse("");
			log.debug("target project key = " + targetPrimeProjectKey);

			if (targetPrimeProjectKey.length() == 0) {

				context.put(ERRORS, Collections.singletonList("Target Project Key = " + targetPrimeProjectKey + " is not defined"));
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
				return;
			}

			Project project = projectService.getProjectByKey(user, targetPrimeProjectKey).getProject();
			log.debug("target project name= " + project.getName());

			// warning it is about a Problem Report
			IssueType problemReportIssueType = constantsManager.getAllIssueTypeObjects().stream().filter(
					issueType -> issueType.getName().equalsIgnoreCase(ISSUE_TYPE_PROBLEM_REPORT)).findFirst().orElse(null);

			// identify all fields needed for a Problem Report creation
			if (problemReportIssueType == null) {

				context.put(ERRORS, Collections.singletonList("Problem Report Issue type not found "));
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
				return;
			}

			log.debug("Problem Report issue type id= " + problemReportIssueType.getId() + " - key= " + problemReportIssueType.getName());
			Issue sourceIssue = IssueHelper.getIssue(issueKey, authenticationContext, issueService);

			if (sourceIssue == null) {

				context.put(ERRORS, Collections.singletonList("Source Issue not found "));
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
				return;

			}

			log.debug("source issue key = " + sourceIssue.getKey());

			IssueInputParameters issueInputParameters = IssueInputParametersHelper.initProblemReportInputParameters(user, sourceIssue , req, project , problemReportIssueType);

			// Sometimes you may need to set a field that is not present on create or update screens. To do so, you need to use following method:
			issueInputParameters.setSkipScreenCheck(true);

			CreateValidationResult createValidateResult = issueService.validateCreate(user, issueInputParameters);
			if (!createValidateResult.isValid() || createValidateResult.getErrorCollection().hasAnyErrors()) {

				log.debug("there were errors - during validate create - " + createValidateResult.getErrorCollection().getErrors().toString());

				context.put(ERRORS, createValidateResult.getErrorCollection().getErrors());
				resp.setContentType("text/html;charset=utf-8");
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

			} else {

				// create the issue
				IssueResult issueCreateResult = issueService.create(user, createValidateResult);
				if (!issueCreateResult.isValid() || issueCreateResult.getErrorCollection().hasAnyErrors()) {

					log.debug("there were errors - during create - " + issueCreateResult.getErrorCollection().getErrors().toString());

					context.put(ERRORS, issueCreateResult.getErrorCollection().getErrors());
					resp.setContentType("text/html;charset=utf-8");
					templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

				} else {

					MutableIssue newIssue = issueCreateResult.getIssue();

					if (newIssue != null ) {

						try {
							if ( sourceIssue.getDueDate() != null) {
								newIssue.setDueDate(sourceIssue.getDueDate());
							}
						} catch (Exception ex) {
							log.error(ex.getLocalizedMessage());
						}

						try {
							LabelsHelper.copyLabels(sourceIssue, newIssue);
						} catch (Exception ex) {
							log.error(ex.getLocalizedMessage());
						}
						try {
							AttachmentHelper.copyAttachments(sourceIssue, newIssue);
						} catch (Exception ex) {
							log.error(ex.getLocalizedMessage());
						}
						try {
							IssueLinkHelper.createLink(sourceIssue, newIssue);
						} catch (Exception ex) {
							log.error(ex.getLocalizedMessage());
						}
						try {
							CommentsHelper.copyComments(sourceIssue, newIssue);
						} catch (Exception ex) {
							log.error(ex.getLocalizedMessage());
						}

						String baseURL = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);

						context.put(RESULTS, Collections.singletonList("Issue " + newIssue.getKey() + " correctly created"));
						context.put("issueKey", newIssue.getKey() );
						context.put("href", baseURL + "/browse/" + newIssue.getKey() );
						templateRenderer.render(CREATED_ISSUES_TEMPLATE, context, resp.getWriter());

					} else {

						context.put(ERRORS, Collections.singletonList("target issue not created "));
						templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
					}
				}
			}


		} catch ( Exception ex) {

			context.put(ERRORS, Collections.singletonList(ex.getLocalizedMessage()));
			templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
		}
	}

	/**
	 * Create a task in TUTORIAL from a task as the input issue
	 * @param req
	 * @param resp
	 * @param issueKey 
	 * @throws IOException
	 */
	private void handleTaskCreation(HttpServletRequest req, HttpServletResponse resp, String issueKey) throws IOException {

		ApplicationUser user = authenticationContext.getLoggedInUser();
		// hard coded target project key
		final String targetPrimeProjectKey = "TUTORIAL";

		Map<String, Object> context = new HashMap<>();

		try {
			Project project = projectService.getProjectByKey(user, targetPrimeProjectKey).getProject();

			if (project == null) {
				context.put(ERRORS, Collections.singletonList("Try to prime into Project with key " + targetPrimeProjectKey + " but project with this KEY doesn't exist"));
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
				return;

			} 

			// warning the type name is task in English or tâche in French
			IssueType taskIssueTypeFrench = constantsManager.getAllIssueTypeObjects().stream().filter(
					issueType -> issueType.getName().equalsIgnoreCase(ISSUE_TYPE_TACHE)).findFirst().orElse(null);

			IssueType taskIssueTypeEnglish = constantsManager.getAllIssueTypeObjects().stream().filter(
					issueType -> issueType.getName().equalsIgnoreCase(ISSUE_TYPE_TASK)).findFirst().orElse(null);

			if ( (taskIssueTypeFrench == null) && (taskIssueTypeEnglish == null) ) {

				context.put(ERRORS, Collections.singletonList("Can't find Task issue type or Tâche issue type -> will try to create only these issue types"));
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
				return;

			} 

			IssueType taskIssueType = null; 
			if (taskIssueTypeFrench != null) {
				taskIssueType = taskIssueTypeFrench;
			}
			if (taskIssueTypeEnglish != null) {
				taskIssueType = taskIssueTypeEnglish;
			}

			Issue sourceIssue = IssueHelper.getIssue(issueKey, authenticationContext, issueService);

			IssueInputParameters issueInputParameters = IssueInputParametersHelper.initTaskInputParameters(user, sourceIssue , req, project , taskIssueType);

			IssueService.CreateValidationResult result = issueService.validateCreate(user, issueInputParameters);
			if (! result.isValid() || result.getErrorCollection().hasAnyErrors()) {

				context.put(ERRORS, result.getErrorCollection().getErrors());
				resp.setContentType("text/html;charset=utf-8");
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
				return;

			} 

			IssueResult issueResult = issueService.create(user, result);
			if (! issueResult.isValid() || issueResult.getErrorCollection().hasAnyErrors()) {

				log.debug("there were errors - during create - " + issueResult.getErrorCollection().getErrors().toString());

				context.put(ERRORS, issueResult.getErrorCollection().getErrors());
				resp.setContentType("text/html;charset=utf-8");
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
				return;

			} 
			// the newly created issue
			MutableIssue newIssue = issueResult.getIssue();

			try {
				if ( sourceIssue.getDueDate() != null) {
					newIssue.setDueDate(sourceIssue.getDueDate());
				}
			} catch (Exception ex) {
				log.error(ex.getLocalizedMessage());
			}

			try {
				LabelsHelper.copyLabels(sourceIssue, newIssue);
			} catch (Exception ex) {
				log.error(ex.getLocalizedMessage());
			}

			try {
				AttachmentHelper.copyAttachments(sourceIssue, newIssue);
			} catch (Exception ex) {
				log.error(ex.getLocalizedMessage());
			}
			try {
				IssueLinkHelper.createLink(sourceIssue, newIssue);
			} catch (Exception ex) {
				log.error(ex.getLocalizedMessage());
			}
			try {
				CommentsHelper.copyComments(sourceIssue, newIssue);
			} catch (Exception ex) {
				log.error(ex.getLocalizedMessage());
			}

			String baseURL = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);

			context.put(RESULTS, Collections.singletonList("Issue " + newIssue.getKey() + " correctly created"));
			context.put("issueKey", newIssue.getKey() );
			context.put("href", baseURL + "/browse/" + newIssue.getKey() );
			templateRenderer.render(CREATED_ISSUES_TEMPLATE, context, resp.getWriter());



		} catch (Exception ex) {

			context.put(ERRORS, Collections.singletonList(ex.getLocalizedMessage()));
			templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
		}
	}



	private void handleIssueCreation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		ApplicationUser user = authenticationContext.getLoggedInUser();

		Map<String, Object> context = new HashMap<>();

		Project project = projectService.getProjectByKey(user, "TUTORIAL").getProject();

		if (project == null) {
			context.put("errors", Collections.singletonList("Project with key TUTORIAL doesn't exist"));
			templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
			return;
		}

		IssueType taskIssueType = constantsManager.getAllIssueTypeObjects().stream().filter(
				issueType -> issueType.getName().equalsIgnoreCase("task")).findFirst().orElse(null);

		if(taskIssueType == null) {
			context.put("errors", Collections.singletonList("Can't find Task issue type"));
			templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
			return;
		}

		IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
		issueInputParameters.setSummary(req.getParameter("summary"))
		.setDescription(req.getParameter("description"))
		.setAssigneeId(user.getName())
		.setReporterId(user.getName())
		.setProjectId(project.getId())
		.setIssueTypeId(taskIssueType.getId());

		IssueService.CreateValidationResult result = issueService.validateCreate(user, issueInputParameters);

		if (! result.isValid() || result.getErrorCollection().hasAnyErrors()) {
			List<Issue> issues = getIssues();
			context.put("issues", issues);
			context.put("errors", result.getErrorCollection().getErrors());
			resp.setContentType("text/html;charset=utf-8");
			templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

		} else {

			IssueResult issueResult = issueService.create(user, result);
			if (issueResult.isValid()) {
				// the new task is created
				MutableIssue issue = issueResult.getIssue();

				String baseURL = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);

				context.put(RESULTS, Collections.singletonList("Issue " + issue.getKey() + " correctly created"));
				context.put("issueKey", issue.getKey() );
				context.put("href", baseURL + "/browse/" + issue.getKey() );
				templateRenderer.render(CREATED_ISSUES_TEMPLATE, context, resp.getWriter());

			} else {
				log.debug("there were errors - during create - " + issueResult.getErrorCollection().getErrors().toString());

				context.put(ERRORS, issueResult.getErrorCollection().getErrors());
				resp.setContentType("text/html;charset=utf-8");
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

			} 
		}
	}

}