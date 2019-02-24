package com.thales.IssuePrime.Helper;


import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.AttachmentPathManager;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.AttachmentError;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.util.PathUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentHelper {

	private static final Logger log = LoggerFactory.getLogger(AttachmentHelper.class);

	private AttachmentHelper() {

	}

	public static void copyAttachments ( final Issue sourceIssue, final Issue targetIssue) {

		AttachmentManager attachmentManager = ComponentAccessor.getAttachmentManager() ;
		AttachmentPathManager attachmentPathManager = ComponentAccessor.getAttachmentPathManager() ;

		List<Attachment> attachmentList = attachmentManager.getAttachments(sourceIssue);
		Iterator<Attachment> iter = attachmentList.iterator();
		while (iter.hasNext()) {

			Attachment attachment = iter.next();

			String sourceProjectKey = IssueHelper.getProjectKey(sourceIssue);
			//log.debug("Project Key= " + sourceProjectKey);

			try {

				attachmentManager.copyAttachment (attachment , targetIssue.getReporter() , targetIssue.getKey() );

			}
			catch (Exception se) {
				log.warn("Could not read attachment file. Not copying. (${se.message})");
			}

		}

	}
}


