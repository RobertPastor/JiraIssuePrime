package ut.com.thales.IssuePrime.servlet;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.DefaultIssueService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.issue.search.DefaultSearchService;

import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.bc.project.DefaultProjectService;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.thales.IssuePrime.servlet.IssueCRUD;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;

//import com.atlassian.jira
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.MockConstantsManager;
import com.atlassian.jira.mock.security.MockAuthenticationContext;

import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.templaterenderer.TemplateRendererFactory;


import com.atlassian.jira.mock.MockIssueManager;


@Scanned
public class IssueCRUDTest {

    HttpServletRequest mockRequest;
    HttpServletResponse mockResponse;
    IssueCRUD issueCRUD;
    
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

    @Before
    public void setup() {
    	
        mockRequest = Mockito.mock(HttpServletRequest.class);
        //mockRequest.setAttribute("actionType", "new");
        //mockRequest.setAttribute("issueType", "Task");
        mockResponse = Mockito.mock(HttpServletResponse.class);
        
        final ApplicationUser fred = new MockApplicationUser("Fred");
        final JiraAuthenticationContext jiraAuthenticationContext = Mockito.mock(JiraAuthenticationContext.class);
        //final ConstantsManager constantsManager = Mockito.mock(ConstantsManager.class);
        
        //Mockito.when(jiraAuthenticationContext.getUser()).thenReturn(fred);
        Mockito.when(jiraAuthenticationContext.getLoggedInUser()).thenReturn(fred);
        
        MockComponentWorker mockComponentWorker = new MockComponentWorker();
        //mockComponentWorker.addMock(ConstantsManager.class, ConstantsManager);
        //mockComponentWorker.addMock(JiraAuthenticationContext.class, jiraAuthenticationContext);
        mockComponentWorker.init();

        issueCRUD = new IssueCRUD( issueService , projectService , searchService, templateRenderer, 
        		jiraAuthenticationContext , constantsManager);
        
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testSomething() throws IOException {
        String expected = "test";
        Mockito.when(mockRequest.getParameter("actionType")).thenReturn("new");
        Mockito.when(mockRequest.getParameter("issueType")).thenReturn("Task");
        Mockito.when(mockRequest.getParameter("issueKey")).thenReturn("CASE-1");
        
        //Assert.assertEquals(expected, mockRequest.getParameter("some string"));
        
        issueCRUD.doGet(mockRequest, mockResponse);

    }
}
