package com.thales.IssuePrime.servlet;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.context.IssueContext;
import com.atlassian.jira.issue.context.IssueContextImpl;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.user.ApplicationUser;


import com.thales.IssuePrime.FieldsConfig.FieldsConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IssueInputParametersHelper {
	
	private static final Logger log = LoggerFactory.getLogger(IssueInputParametersHelper.class);


	private IssueInputParametersHelper() {
		
		
	}
	
	public static IssueInputParameters initProblemReportInputParameters( final ApplicationUser user, 
			final Issue sourceIssue , final HttpServletRequest req, final Project project, final IssueType problemReportIssueType) {
		
		
		// need to know all the mandatory fields in the ISSUE creation operations for this issue type in the target project
		IssueService issueService = ComponentAccessor.getIssueService();
		IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();

		issueInputParameters.setSummary(req.getParameter("summary"))
		.setDescription("Issue-Prime - primed by [~" + user.getKey() + "] - primed from [~" + sourceIssue.getKey() + "] - " + req.getParameter("description"))
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
		
		if (sourceIssue.getSecurityLevelId() != null) {
			issueInputParameters.setSecurityLevelId(sourceIssue.getSecurityLevelId());
		}

		if (sourceIssue.getPriority() !=  null) {
			issueInputParameters.setPriorityId(sourceIssue.getPriority().getId());
		}

		if (sourceIssue.getReporter() != null ) {
			issueInputParameters.setReporterId(sourceIssue.getReporterId());
		}
		if (sourceIssue.getAssignee() != null) {
			issueInputParameters.setAssigneeId(sourceIssue.getAssigneeId());
		}
		
		// Detection date -  customfield_9991
		issueInputParameters = setDetectionDate( sourceIssue, issueInputParameters);
		// set Reproducibility - customfield_9988
		issueInputParameters = setReproducibility( sourceIssue, issueInputParameters );
		// customfield_9994 - Severity
		issueInputParameters = setSeverity(sourceIssue, issueInputParameters);
		// set test reference
		issueInputParameters = setTestReference( issueInputParameters );
		// set Impacted Requirements
		issueInputParameters = setImpactedRequirements(sourceIssue, issueInputParameters);
		// set Detection Phase
		issueInputParameters = setDetectionPhase(sourceIssue, issueInputParameters);
		
		return issueInputParameters;
		
	}
	
	
	private static IssueInputParameters setDetectionDate( final Issue sourceIssue, IssueInputParameters issueInputParameters ) {

		// Detection date -  customfield_9991
		try {

			log.debug("---------------- setDetectionDate -------------------");

			CustomField customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject( (long) 9991 );
			if (customField != null) {

				log.debug("custom filed id= " + customField.getId());
				log.debug("custom filed name= " + customField.getName() );

			} else {
				customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName("Detection date");

			}
			log.debug( "is custom field null = " + (customField == null));

			CustomFieldType customFieldType = customField.getCustomFieldType();
			log.debug( "custom field type= " + customFieldType.getName() );

			Date date = new Date();
			String format = ComponentAccessor.getApplicationProperties().getDefaultBackedString(APKeys.JIRA_DATE_PICKER_JAVA_FORMAT);
			log.debug( String.valueOf( ComponentAccessor.getApplicationProperties() != null ) );
			log.debug("data format: " + format);

			DateFormat dateFormat = new SimpleDateFormat(format);
			String newValue = dateFormat.format(new Timestamp(date.getTime()));
			log.debug("new date value: " + newValue);

			issueInputParameters = issueInputParameters.addCustomFieldValue(customField.getId(), newValue );

		} catch (Exception e) {
			log.error("Exception while setting Detection date - ex= " + e );
		}
		return issueInputParameters;
	}


	
	public static IssueInputParameters setReproducibility( final Issue sourceIssue, IssueInputParameters issueInputParameters ) {

		// set Reproducibility - customfield_9988
		try {
			log.debug("-------------setReproducibility---------------");

			CustomField customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject( (long) 9988 );

			log.debug( "custom field id= " + customField.getId());
			log.debug( "custom field long id= " + String.valueOf( customField.getIdAsLong() ));
			log.debug( "custom field name= " + customField.getName() );

			CustomFieldType customFieldType = customField.getCustomFieldType();
			log.debug( "custom field type= " + customFieldType.getName() );
			
			IssueContext issueContext = new IssueContextImpl(sourceIssue.getProjectObject().getId() , sourceIssue.getIssueTypeId());
			log.debug( String.valueOf( issueContext != null) );

			FieldConfig fieldConfig = customField.getRelevantConfig(issueContext);
			log.debug( String.valueOf( fieldConfig != null ));

			Options options = ComponentAccessor.getOptionsManager().getOptions(fieldConfig); 
			log.debug( String.valueOf( options != null ));

			if ( ! options.isEmpty() ) {
				log.debug("there are options ");

				Option optionToSelect = options.get(0); 
				log.debug( String.valueOf( optionToSelect.getOptionId()) );

				Object value = sourceIssue.getCustomFieldValue(customField);
				log.debug( String.valueOf( value instanceof Option ));
				
				issueInputParameters = issueInputParameters.addCustomFieldValue( customField.getId(),  String.valueOf( optionToSelect.getOptionId()) );

			}

		} catch (Exception e) {
			log.error("Exception while setting Reproducibility - ex= " + e );
		}
		return issueInputParameters;
	}


	
	public static IssueInputParameters setSeverity(final Issue sourceIssue, IssueInputParameters issueInputParameters) {

		// customfield_9994 - Severity

		try {
			log.debug(" ----------------- setSeverity -------------------");

			CustomField customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject( (long) 9994 );

			log.debug( customField.getId());
			log.debug( customField.getName());

			CustomFieldType customFieldType = customField.getCustomFieldType();
			log.debug( "custom field type= " + customFieldType.getName() );

			IssueContext issueContext = new IssueContextImpl(sourceIssue.getProjectObject().getId() , sourceIssue.getIssueTypeId());
			log.debug( String.valueOf( issueContext != null) );

			FieldConfig fieldConfig = customField.getRelevantConfig(issueContext);
			log.debug( String.valueOf( fieldConfig != null ));

			Options options = ComponentAccessor.getOptionsManager().getOptions(fieldConfig); 
			log.debug( String.valueOf( options != null ));

			if ( ! options.isEmpty() ) {
				log.debug("there are options ");

				Option optionToSelect = options.get(0); 
				log.debug( String.valueOf( optionToSelect.getOptionId()) );

				Object value = sourceIssue.getCustomFieldValue(customField);
				log.debug( String.valueOf( value instanceof Option ));

				issueInputParameters = issueInputParameters.addCustomFieldValue( customField.getId(),  String.valueOf( optionToSelect.getOptionId()) );

			}

		} catch (Exception e) {
			log.error("Exception while setting Severity - ex= " + e );
		}
		return issueInputParameters;
	}
	
	
	public static IssueInputParameters setDetectionPhase (final Issue sourceIssue, IssueInputParameters issueInputParameters) {
		
		try {
			// customfield_9989
			log.debug(" ----------------- set Detection Phase  -------------------");
			
			CustomField customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject( (long) 9989 );

			log.debug( customField.getId());
			log.debug( customField.getName());

			CustomFieldType customFieldType = customField.getCustomFieldType();
			log.debug( "custom field type= " + customFieldType.getName() );

			IssueContext issueContext = new IssueContextImpl(sourceIssue.getProjectObject().getId() , sourceIssue.getIssueTypeId());
			log.debug( String.valueOf( issueContext != null) );

			FieldConfig fieldConfig = customField.getRelevantConfig(issueContext);
			log.debug( String.valueOf( fieldConfig != null ));

			Options options = ComponentAccessor.getOptionsManager().getOptions(fieldConfig); 
			log.debug( String.valueOf( options != null ));

			if ( ! options.isEmpty() ) {
				log.debug("there are options ");

				Option optionToSelect = options.get(0); 
				log.debug( String.valueOf( optionToSelect.getOptionId()) );

				Object value = sourceIssue.getCustomFieldValue(customField);
				log.debug( String.valueOf( value instanceof Option ));

				issueInputParameters = issueInputParameters.addCustomFieldValue( customField.getId(),  String.valueOf( optionToSelect.getOptionId()) );

			}
			
			
		} catch (Exception e) {
			log.error("Exception while setting Severity - ex= " + e );
		}
		return issueInputParameters;
		
	}
	
	
	public static IssueInputParameters setTestReference( IssueInputParameters issueInputParameters) {

		// customfield_11202 - "name": "Test reference",
		try {   
			log.debug("---------- setTestReference -----------------");
			//CustomField customField = customFieldManager.getCustomFieldObject ("customfield_11202");
			CustomField customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject( (long) 11202 );

			log.debug( "custom field id= " + customField.getId());
			log.debug("custom field name= " + customField.getName() );

			CustomFieldType customFieldType = customField.getCustomFieldType();
			log.debug( "custom field type= " + customFieldType.getName() );			

			issueInputParameters = issueInputParameters.addCustomFieldValue(customField.getId(),  "Test Reference"  );

		} catch (Exception e) {
			log.error("Exception while setting test reference= " + e );
		}
		return issueInputParameters;
	}

	
	private static IssueInputParameters setImpactedRequirements(final Issue sourceIssue, IssueInputParameters issueInputParameters) {

		/**
		 * 
		 * "customfield_11203": {
					"required": false,
					"schema": {
						"type": "string",
						"custom": "com.atlassian.jira.plugin.system.customfieldtypes:textfield",
						"customId": 11203
					},
					"name": "Impacted requirements",
					"hasDefaultValue": false,
					"operations": ["set"]
				},

		 */

		try {
			log.debug("-------------setImpactedRequirements-----------------");

			CustomField customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject( (long) 11203 );

			CustomFieldType customFieldType = customField.getCustomFieldType();
			log.debug( "custom field type= " + customFieldType.getName() );

			Object value = customField.getValue(sourceIssue);
			if (value instanceof String) {

				log.debug("Impacted Requirements = " + (String) value);
				issueInputParameters = issueInputParameters.addCustomFieldValue(customField.getId(), (String) value);

			}

		} catch (Exception e) {
			log.error("Exception while setting Impacted Requirements - ex= " + e );
		}
		return issueInputParameters;
	}
	
	

	/**
	 * ste custom field depending upon its type (Date, Long, Select, etc.)
	 * @param customFieldId
	 * @param issueInputParameters
	 * @param sourceIssue
	 */
	private static CustomField setCustomFieldOptionsValues(final String customFieldId ,  final Issue sourceIssue) {

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

		// set the custom field object from the custom field id (customfield_9988 - Reproducibility)
		CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
		CustomField customField = customFieldManager.getCustomFieldObject(customFieldId);

		OptionsManager optionsManager = ComponentAccessor.getOptionsManager();
		Options options = optionsManager.getOptions(customField.getRelevantConfig(sourceIssue));
		//options.getOptionById(arg0)

		Object value = sourceIssue.getCustomFieldValue(customField);
		// here it is the option Id as provided by JIRA
		//Option newOption = options.getOptionById(newOptionId);

		// modified value - old value , new value
		ModifiedValue modifiedValue = new ModifiedValue(null, sourceIssue.getCustomFieldValue(customField) );

		FieldLayoutManager fieldLayoutManager = ComponentAccessor.getFieldLayoutManager();
		FieldLayoutItem fieldLayoutItem = fieldLayoutManager.getFieldLayout().getFieldLayoutItem(customField);
		customField.updateValue(fieldLayoutItem, sourceIssue, modifiedValue, new DefaultIssueChangeHolder());

		return customField;

	}
	

	/*
	private static void setDetectionDateOld(Issue sourceIssue, IssueInputParameters issueInputParameters) {

		DateTimeFormatter dateTimeFormatter = dateTimeFormatterFactory.formatter().forLoggedInUser();

		FieldCollectionsUtils fieldCollectionsUtils = new FieldCollectionsUtils(beanFactory, 
				applicationProperties, dateTimeFormatter, fieldManager, 
				fieldLayoutManager, customFieldManager, fieldVisibilityManager);

		// Detection date -  customfield_9991
		log.debug("looking for custom field with id= customfield_9991 " );

		CustomField customField = customFieldManager.getCustomFieldObject("c1");

		log.debug("custom field long id= " + String.valueOf(customField.getIdAsLong()));
		if ( fieldCollectionsUtils.isIssueHasField(sourceIssue, customField) ) {

			log.debug("source issue has this customfield customfield_9991 " );

			WorkflowUtils workflowUtils = new WorkflowUtils
					(fieldManager, issueManager, projectComponentManager,
							versionManager, issueSecurityLevelManager, applicationProperties, fieldCollectionsUtils, issueLinkManager,
							userManager, optionsManager, projectManager, 
							priorityManager , labelManager, projectRoleManager, watcherManager);

			issueInputParameters.addCustomFieldValue(customField.getIdAsLong(), (String)workflowUtils.getFieldValueFromIssue(sourceIssue, customField));
		} else {
			log.debug("source issue has not this custom field " +  String.valueOf(customField.getIdAsLong()) );
		}

	}
	*/
}
