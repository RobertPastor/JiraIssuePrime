package com.thales.IssuePrime.Helper;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;

public class CommentsHelper {

	private static final Logger log = LoggerFactory.getLogger(AttachmentHelper.class);

	private CommentsHelper() {
		
	}
	
	public static void copyComments ( final Issue sourceIssue, final Issue targetIssue) {

		CommentManager commentManager = ComponentAccessor.getCommentManager() ;

		List<Comment> commentList = commentManager.getComments(sourceIssue);
		Iterator<Comment> iter = commentList.iterator();
		while (iter.hasNext()) {

			Comment comment = iter.next();

			String sourceProjectKey = IssueHelper.getProjectKey(sourceIssue);
			//log.debug("Project Key= " + sourceProjectKey);

			try {

				commentManager.create(targetIssue, comment.getAuthorApplicationUser(), comment.getBody(), true);

			}
			catch (Exception se) {
				log.warn("Could not read comment. Not copying. (${se.message})");
			}

		}

	}
}
