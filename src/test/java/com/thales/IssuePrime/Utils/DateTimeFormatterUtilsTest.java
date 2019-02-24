package com.thales.IssuePrime.Utils;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.datetime.DateTimeFormatterFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.datetime.DateTimeFormatUtils;
import com.atlassian.jira.mock.MockConstantsManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.thales.IssuePrime.FieldsConfig.FieldsConfigurationTest;

public class DateTimeFormatterUtilsTest {
	
	private Log log = LogFactory.getLog(DateTimeFormatterUtilsTest.class);
	
	private MockComponentWorker componentAccessorWorker;
	
	
	@Before
    public void setUp()
    {
        final ApplicationUser fred = new MockApplicationUser("Fred");
        final JiraAuthenticationContext jiraAuthenticationContext = Mockito.mock(JiraAuthenticationContext.class);
        
        Mockito.when(jiraAuthenticationContext.getUser()).thenReturn(fred);
        //Mockito.when(jiraAuthenticationContext.getUser()).thenReturn(fred.getDirectoryUser());
        componentAccessorWorker = new MockComponentWorker().init();
        //        .addMock(ConstantsManager.class, new MockConstantsManager())
        //        .addMock(JiraAuthenticationContext.class, jiraAuthenticationContext)
        //        .init();
    }
	
	
	@Test
	public void testSomething() throws IOException {
		
		
		//componentAccessorWorker.addMock("com.atlassian.jira.datetime.DateTimeFormatterFactory" , "DateTimeFormatterFactory");
		ComponentAccessor.initialiseWorker(componentAccessorWorker);

		//DateTimeFormatter dateTimeFormatter = new DateTimeFormatter();
		
		//DateTimeFormatterFactory.
		
		String dateFormat = DateTimeFormatUtils.getDateFormat();
		log.debug(dateFormat);

		// Detection date -  customfield_9991
		TimeZone timeZone = TimeZone.getTimeZone("France/Paris");
		log.debug(timeZone.getDisplayName());
		
		//String formattedDate = dateTimeFormatter.withZone(timeZone).format( new Date() )  ;
		//System.out.println(formattedDate);
		
	}

}
