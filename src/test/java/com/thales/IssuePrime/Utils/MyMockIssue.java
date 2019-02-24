package com.thales.IssuePrime.Utils;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.issue.MockIssue;

public class MyMockIssue extends MockIssue implements Issue {
	
	private String key;
	private String summary;

	public MyMockIssue(String key, String summary) {
		this.key = key;
		this.summary = summary;
	}

	
	
}


