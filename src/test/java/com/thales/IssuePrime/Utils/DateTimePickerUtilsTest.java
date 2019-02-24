package com.thales.IssuePrime.Utils;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.GregorianCalendar;
import java.sql.Timestamp;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.IssueManager;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.issue.util.IssueChangeHolder;


import com.atlassian.jira.mock.MockIssueManager;
import com.atlassian.jira.mock.issue.fields.MockNavigableField;;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.issue.managers.MockCustomFieldManager;

import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.testkit.client.restclient.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;

import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.MockFieldLayoutManager;


import com.atlassian.jira.issue.ModifiedValue;


public class DateTimePickerUtilsTest {


	private MockComponentWorker componentAccessorWorker;
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");

	@Before
	public void setUp()
	{
		final ApplicationUser fred = new MockApplicationUser("Fred");
		final JiraAuthenticationContext jiraAuthenticationContext = Mockito.mock(JiraAuthenticationContext.class);
		//Mockito.when(jiraAuthenticationContext.getUser()).thenReturn(fred);
		//Mockito.when(jiraAuthenticationContext.getUser()).thenReturn(fred.getDirectoryUser());
		//componentAccessorWorker = new MockComponentWorker().init();
		//        .addMock(ConstantsManager.class, new MockConstantsManager())
		//        .addMock(JiraAuthenticationContext.class, jiraAuthenticationContext)
		//        .init();
		
		//36 [main] WARN com.atlassian.jira.mock.component.MockComponentWorker - No mock implementation was provided for component 'com.atlassian.jira.issue.IssueManager'
		//36 [main] WARN com.atlassian.jira.mock.component.MockComponentWorker - No mock implementation was provided for component 'com.atlassian.jira.issue.CustomFieldManager'
		componentAccessorWorker = new MockComponentWorker()
				.addMock(IssueManager.class, new MockIssueManager())
				.addMock(CustomFieldManager.class, new MockCustomFieldManager())
				.addMock(FieldLayoutManager.class, new MockFieldLayoutManager())
				.init();
				
	}



	private Calendar getDate(double roll, String date_fpr) throws java.text.ParseException
	{
		
		Date origdate = df.parse(date_fpr);
		Calendar cal = Calendar.getInstance();
		cal.setTime(origdate);
		//cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);

		for (int x=0 ; x < roll;x++) {
			cal.add(Calendar.DATE, 1);
			while(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				cal.add(Calendar.DATE, 1);
			}
		}
		return cal;
	}


	@Test
	public void testSomething() throws IOException , java.text.ParseException {


		ComponentAccessor.initialiseWorker(componentAccessorWorker);

		IssueManager issueManager = ComponentAccessor.getIssueManager();
		CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
		//CustomFieldLayoutManager customFieldLayoutManager = ComponentAccessor.getCustomFieldLayoutManager();

		String customFieldName = "Detection Date";

		MockIssue mockIssue = new MyMockIssue("AAAA-1","An Issue");
		

		// get the Date value from Date Full Packed Receive custom field
		CustomField customField = customFieldManager.getCustomFieldObjectByName(customFieldName);
		Object value = mockIssue.getCustomFieldValue(customField);

		long Urgent = 5;

		Calendar cal = getDate(Urgent, mockIssue.getCreated().toString());

		// Detection date -  customfield_9991
		String customFieldId = "customfield_9991";

		// get the Date value from SLA Date custom field
		CustomField customFieldDatePicker  = customFieldManager.getCustomFieldObjectByName(customFieldName);

		//FieldLayoutItem fieldLayoutItem = customFieldLayoutManager.getFieldLayout(mockIssue).getFieldLayoutItem(customField);
		//IssueChangeHolder changeHolder = new DefaultIssueChangeHolder();

		//Timestamp dueDate = new Timestamp(cal.getTimeInMillis());

		//customFieldDatePicker.updateValue(fieldLayoutItem, mockIssue, new ModifiedValue(value, dueDate), changeHolder);
		//mutableIssue.setCustomFieldValue(dateSLA , dueDate);
		//dateSLAValue = mockIssue.getCustomFieldValue(dateSLA);

		//return dateSLAValue;

		//log.info("value from field SLA:"+dateSLAValue.getValueFromIssue(mutableIssue));


	}

}
