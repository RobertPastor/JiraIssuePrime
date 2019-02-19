package com.thales.CRUD;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.IssueService.IssueResult;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.PriorityManager;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.customfields.DefaultCustomFieldValueProvider;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.label.LabelManager;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.issue.transport.FieldValuesHolder;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.issue.watchers.WatcherManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.I18nHelper.BeanFactory;
import com.atlassian.jira.web.FieldVisibilityManager;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.googlecode.jsu.util.FieldCollectionsUtils;
import com.googlecode.jsu.util.WorkflowUtils;
import com.thales.IssuePrime.FieldsConfig.FieldsConfiguration;
import com.thales.IssuePrime.Helper.IssueHelper;
import com.thales.IssuePrime.Helper.IssueTypeHelper;
import com.thales.IssuePrime.Helper.ProjectHelper;

@Scanned
public class IssueCRUD extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4439147766945679620L;

	private static final Logger log = LoggerFactory.getLogger(FieldsConfiguration.class);


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
	@JiraImport
	private CustomFieldManager customFieldManager;
	@JiraImport
	private OptionsManager optionsManager;
	@JiraImport
	private FieldLayoutManager fieldLayoutManager;
	@JiraImport
	private DateTimeFormatterFactory dateTimeFormatterFactory; 
	@JiraImport
	private FieldManager fieldManager;
	@JiraImport
	private IssueManager issueManager;
	@JiraImport
	private ProjectComponentManager projectComponentManager;
	@JiraImport
	private VersionManager versionManager;
	@JiraImport
    private IssueSecurityLevelManager issueSecurityLevelManager;
	@JiraImport
	private ApplicationProperties applicationProperties;
	
	@JiraImport
	private IssueLinkManager issueLinkManager;
	@JiraImport
	private UserManager userManager;
    //CrowdService crowdService, 
	@JiraImport
	private ProjectManager projectManager;
	@JiraImport
	private PriorityManager priorityManager;
	@JiraImport
	private LabelManager labelManager;
	@JiraImport
	private ProjectRoleManager projectRoleManager;
	@JiraImport
	private WatcherManager watcherManager;
	@JiraImport
	private FieldVisibilityManager fieldVisibilityManager;


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
	private static final String NEW_TASK_ISSUE_TEMPLATE = "/templates/issueCrud/newTask.vm";
	private static final String NEW_PROBLEM_REPORT_ISSUE_TEMPLATE = "/templates/issueCrud/newProblemReport.vm";
	private static final String CREATED_ISSUES_TEMPLATE = "/templates/issueCrud/created.vm";

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
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		Map<String, Object> context = new HashMap<>();
		try {

			String actionType = Optional.ofNullable(req.getParameter(ACTION_TYPE_REQUEST_PARAMETER)).orElse("");
			String issueType = Optional.ofNullable(req.getParameter(ISSUE_TYPE_REQUEST_PARAMETER)).orElse("");
			String issueKey = Optional.ofNullable(req.getParameter(ISSUE_KEY_REQUEST_PARAMETER)).orElse("");

			log.debug("Issue type is= " + issueType);

			if ((actionType.length()==0) || (issueType.length()==0) || (issueKey.length()==0)) {

				context.put(ERRORS, Collections.singletonList("Action cancelled by the user or insufficient parameters provided by the user"));
				resp.setContentType("text/html;charset=utf-8");
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

			} else {

				ApplicationUser user = authenticationContext.getLoggedInUser();
				IssueResult issueResult = issueService.getIssue(user, issueKey);

				if (issueResult.getErrorCollection().hasAnyErrors()) {

					context.put(ERRORS, issueResult.getErrorCollection().getErrors());
					resp.setContentType("text/html;charset=utf-8");
					templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

				} else {

					Issue issue = issueResult.getIssue();
					if (issue == null) {

						context.put(ERRORS, Collections.singletonList("Error - cannot find issue with key= " + issueKey));
						templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

					} else {

						prepareAndRenderNewIssueTemplate(req, resp, issue, context);
					}
				}
			}
		} catch (Exception ex) {
			context.put(ERRORS, Collections.singletonList(ex.getLocalizedMessage()));
			templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
		}
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
	 * ste custom field depending upon its type (Date, Long, Select, etc.)
	 * @param customFieldId
	 * @param issueInputParameters
	 * @param sourceIssue
	 */
	private CustomField setCustomFieldOptionsValues(final String customFieldId ,  final Issue sourceIssue) {

		/**
		 * Detection date is required - customfield_9991

		 * Affects Version/s is required - versions
		 * Reproducibility is required - customfield_9988
		 * Detection phase is required - customfield_9989
		 * Priority is required.
		 * Test reference is required - customfield_11202
		 * Severity is required - customfield_9994
		 * Impacted requirements is required - customfield_11203
		 */

		//issueInputParameters = issueInputParameters.		

		List<String> customFieldNamelist = Arrays.asList("customfield_9991", "versions", 
				"customfield_9988", "customfield_9989", "customfield_11202", "customfield_9994", "customfield_11203");

		// et the custom field object from the custom field id (customfield_9988 - Reproducibility)
		CustomField customField = customFieldManager.getCustomFieldObject(customFieldId);

		Options options = optionsManager.getOptions(customField.getRelevantConfig(sourceIssue));
		//options.getOptionById(arg0)

		Object value = sourceIssue.getCustomFieldValue(customField);
		// here it is the option Id as provided by JIRA
		//Option newOption = options.getOptionById(newOptionId);

		// modified value - old value , new value
		ModifiedValue modifiedValue = new ModifiedValue(null, sourceIssue.getCustomFieldValue(customField) );

		FieldLayoutItem fieldLayoutItem = fieldLayoutManager.getFieldLayout().getFieldLayoutItem(customField);
		customField.updateValue(fieldLayoutItem, sourceIssue, modifiedValue, new DefaultIssueChangeHolder());

		return customField;

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
		Map<String, Object> context = new HashMap<>();

		try {

			String targetPrimeProjectKey =  Optional.ofNullable(req.getParameter(PROJECT_KEY_REQUEST_PARAMETER)).orElse("");
			log.debug("target project key = " + targetPrimeProjectKey);

			if (targetPrimeProjectKey.length()>0) {

				Project project = projectService.getProjectByKey(user, targetPrimeProjectKey).getProject();

				log.debug("target project name= " + project.getName());

				// warning it is about a Problem Report
				IssueType problemReportIssueType = constantsManager.getAllIssueTypeObjects().stream().filter(
						issueType -> issueType.getName().equalsIgnoreCase(ISSUE_TYPE_PROBLEM_REPORT)).findFirst().orElse(null);

				// identify all fields needed for a Problem Report creation
				if (problemReportIssueType != null) {

					log.debug("Problem Report issue type id= " + problemReportIssueType.getId() + " - key= " + problemReportIssueType.getName());
					Issue sourceIssue = IssueHelper.getIssue(issueKey, authenticationContext, issueService);

					if (sourceIssue != null) {

						log.debug("source issue = " + sourceIssue.getKey());

						// need to know all the mandatory fields in the ISSUE creation operations for this issue type in the target project
						IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
						issueInputParameters.setSummary(req.getParameter("summary"))
						.setDescription("Issue-Prime - primed by [~" + user.getKey() + "] - " + req.getParameter("description"))
						.setProjectId(project.getId())
						.setIssueTypeId(problemReportIssueType.getId());

						// set affected versions
						if ( ! sourceIssue.getAffectedVersions().isEmpty() ) {
							Collection<Version> affectedVersions = sourceIssue.getAffectedVersions();
							Iterator<Version> iter = affectedVersions.iterator();
							while (iter.hasNext()) {
								Version version = iter.next();
								log.debug("affected versions = " + version.getName());
								issueInputParameters.setAffectedVersionIds(version.getId());
							}
						}

						// do not perform screen check
						//issueInputParameters.setSkipScreenCheck(true);
						//issueInputParameters.setApplyDefaultValuesWhenParameterNotProvided(true);

						//DateTimeFormatter dateTimeFormatter = dateTimeFormatterFactory.formatter().forLoggedInUser();

						// Detection date -  customfield_9991
						//issueInputParameters.addCustomFieldValue("customfield_9991", dateTimeFormatter.withZone(TimeZone.getTimeZone("France/Paris")).format( new Date() ) ) ;

						// set Reproducibility - customfield_9988
						//CustomField customField = setCustomFieldOptionsValues("customfield_9988", sourceIssue);						
						//issueInputParameters.addCustomFieldValue(customField.getIdAsLong(), (String)customField.getValue(sourceIssue));


						if (sourceIssue.getSecurityLevelId() != null) {
							issueInputParameters.setSecurityLevelId(sourceIssue.getSecurityLevelId());
						}

						if (sourceIssue.getPriority() !=  null) {
							issueInputParameters.setPriorityId(sourceIssue.getPriority().getId());
						}

						// set all custom fields values
						//setCustomFieldsValues(sourceIssue, issueInputParameters);

						// set default values
						//issueInputParameters.setApplyDefaultValuesWhenParameterNotProvided(true);

						if (sourceIssue.getReporter() != null ) {
							issueInputParameters.setReporterId(sourceIssue.getReporterId());
						}
						if (sourceIssue.getAssignee() != null) {
							issueInputParameters.setAssigneeId(sourceIssue.getAssigneeId());
						}

						IssueService.CreateValidationResult result = issueService.validateCreate(user, issueInputParameters);
						if (!result.isValid() || result.getErrorCollection().hasAnyErrors()) {

							log.debug("there were errors - during validate create - " + result.getErrorCollection().getErrors().toString());

							context.put(ERRORS, result.getErrorCollection().getErrors());
							resp.setContentType("text/html;charset=utf-8");
							templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

						} else {

							IssueResult issueResult = issueService.create(user, result);
							if (!issueResult.isValid() || issueResult.getErrorCollection().hasAnyErrors()) {

								log.debug("there were errors - during create - " + issueResult.getErrorCollection().getErrors().toString());

								context.put(ERRORS, issueResult.getErrorCollection().getErrors());
								resp.setContentType("text/html;charset=utf-8");
								templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

							} else {

								MutableIssue issue = issueResult.getIssue();

								if (issue != null ) {

									String baseURL = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);

									context.put(RESULTS, Collections.singletonList("Issue " + issue.getKey() + " correctly created"));
									context.put("issueKey", issue.getKey() );
									context.put("href", baseURL + "/browse/" + issue.getKey() );
									templateRenderer.render(CREATED_ISSUES_TEMPLATE, context, resp.getWriter());

								} else {

									context.put(ERRORS, Collections.singletonList("target issue not created "));
									templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
								}
							}

						}
					} else {
						context.put(ERRORS, Collections.singletonList("Source Issue not found "));
						templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

					}
				} else {

					context.put(ERRORS, Collections.singletonList("Problem Report Issue type not found "));
					templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
				}
			} else {

				context.put(ERRORS, Collections.singletonList("Target Project Key = " + targetPrimeProjectKey + " is not defined"));
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
			}

		} catch ( Exception ex) {

			context.put(ERRORS, Collections.singletonList(ex.getLocalizedMessage()));
			templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
		}
	}

	/**
	 * Create a task in TUTORIAL from a task in the input issue
	 * @param req
	 * @param resp
	 * @param issueKey 
	 * @throws IOException
	 */
	private void handleTaskCreation(HttpServletRequest req, HttpServletResponse resp, String issueKey) throws IOException {

		ApplicationUser user = authenticationContext.getLoggedInUser();
		final String targetPrimeProjectKey = "TUTORIAL";

		Map<String, Object> context = new HashMap<>();

		try {
			Project project = projectService.getProjectByKey(user, targetPrimeProjectKey).getProject();

			if (project == null) {
				context.put(ERRORS, Collections.singletonList("Try to prime into Project with key " + targetPrimeProjectKey + " but project with this KEY doesn't exist"));
				templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

			} else {

				// warning the type name is task in English or tâche in French
				IssueType taskIssueTypeFrench = constantsManager.getAllIssueTypeObjects().stream().filter(
						issueType -> issueType.getName().equalsIgnoreCase(ISSUE_TYPE_TACHE)).findFirst().orElse(null);

				IssueType taskIssueTypeEnglish = constantsManager.getAllIssueTypeObjects().stream().filter(
						issueType -> issueType.getName().equalsIgnoreCase(ISSUE_TYPE_TASK)).findFirst().orElse(null);

				if (taskIssueTypeFrench == null && taskIssueTypeEnglish == null) {

					context.put(ERRORS, Collections.singletonList("Can't find Task issue type or Tâche issue type -> expected to create only these issue types"));
					templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

				} else {

					Issue sourceIssue = IssueHelper.getIssue(issueKey, authenticationContext, issueService);

					// need to know all the mandatory fields in the ISSUE creation operations for this issue type in the target project
					IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
					issueInputParameters.setSummary(req.getParameter("summary"))
					.setDescription("Issue-Prime - primed by [~" + user.getKey() + "] - " + req.getParameter("description"))
					.setProjectId(project.getId())
					.setIssueTypeId(sourceIssue.getIssueTypeId());

					if (sourceIssue != null) {
						issueInputParameters.setReporterId(sourceIssue.getReporterId());
						issueInputParameters.setAssigneeId(sourceIssue.getAssigneeId());
					} else {
						issueInputParameters.setReporterId(user.getName());
						issueInputParameters.setAssigneeId(user.getName());
					}
					
					BeanFactory beanFactory = ComponentAccessor.getI18nHelperFactory();
					DateTimeFormatter dateTimeFormatter = dateTimeFormatterFactory.formatter().forLoggedInUser();

					FieldCollectionsUtils fieldCollectionsUtils = new FieldCollectionsUtils(beanFactory, applicationProperties, 
							dateTimeFormatter, fieldManager, fieldLayoutManager, customFieldManager, fieldVisibilityManager);
					
					WorkflowUtils workflowUtils = new WorkflowUtils(fieldManager, issueManager, projectComponentManager,
							versionManager, issueSecurityLevelManager, applicationProperties, fieldCollectionsUtils, issueLinkManager, userManager, 
							optionsManager, projectManager, priorityManager, labelManager, projectRoleManager, watcherManager);
					
					CustomField customField = customFieldManager.getCustomFieldObject("com.example.plugins.tutorial.customfields.jira-custom-field-example:admintextfield");
					if (fieldCollectionsUtils.isIssueHasField(sourceIssue, customField) ) {
						
						log.info("custom field found");
					}
					
					//==================

					IssueService.CreateValidationResult result = issueService.validateCreate(user, issueInputParameters);

					if (result.getErrorCollection().hasAnyErrors()) {

						context.put(ERRORS, result.getErrorCollection().getErrors());
						resp.setContentType("text/html;charset=utf-8");
						templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());


					} else {

						IssueResult issueResult = issueService.create(user, result);
						if (issueResult.getErrorCollection().hasAnyErrors()) {

							log.debug("there were errors - during create - " + issueResult.getErrorCollection().getErrors().toString());

							context.put(ERRORS, issueResult.getErrorCollection().getErrors());
							resp.setContentType("text/html;charset=utf-8");
							templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());

						} else {

							MutableIssue issue = issueResult.getIssue();

							String baseURL = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);

							context.put(RESULTS, Collections.singletonList("Issue " + issue.getKey() + " correctly created"));
							context.put("issueKey", issue.getKey() );
							context.put("href", baseURL + "/browse/" + issue.getKey() );
							templateRenderer.render(CREATED_ISSUES_TEMPLATE, context, resp.getWriter());
						}
					}
				}

			}

		} catch (Exception ex) {

			context.put(ERRORS, Collections.singletonList(ex.getLocalizedMessage()));
			templateRenderer.render(LIST_ISSUES_TEMPLATE, context, resp.getWriter());
		}
	}

}