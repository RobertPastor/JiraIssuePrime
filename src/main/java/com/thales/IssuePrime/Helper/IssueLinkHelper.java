package com.thales.IssuePrime.Helper;

import java.util.Iterator;
import java.util.Set;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.issue.link.LinkCollection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IssueLinkHelper {

	private static final Logger log = LoggerFactory.getLogger(IssueLinkHelper.class);

	private IssueLinkHelper() {

	}

	public static void createLink (final Issue sourceIssue, final Issue targetIssue ) {

		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
		IssueLinkTypeManager issueLinkTypeManager = (IssueLinkTypeManager)ComponentManager.getComponentInstanceOfType(IssueLinkTypeManager.class);

		if (issueLinkManager.isLinkingEnabled()) {

			try{
				//issueLinkManager.createIssueLink(sourceIssue.getId(), createResult.getIssue().getId(), 10040L, new Long(0), event.getRemoteUser());

				LinkCollection lc = issueLinkManager.getLinkCollection(sourceIssue, sourceIssue.getReporter());
				Set<IssueLinkType> linkTypes = lc.getLinkTypes();
				Iterator<IssueLinkType> iter = linkTypes.iterator();
				while (iter.hasNext()) {

					IssueLinkType issueLinkType = iter.next();
					log.debug("issue link type id= " + String.valueOf( issueLinkType.getId() ));
					log.debug(issueLinkType.getName());

					//if (issueLinkType.isSystemLinkType()) {

						//IssueLink il = issueLinkManager.getIssueLink(sourceIssue.getId(), targetIssue.getId(), linkType.getId());
						try{
							issueLinkManager.createIssueLink(sourceIssue.getId(), targetIssue.getId(), issueLinkType.getId(), null, targetIssue.getReporter() );
						}catch(Exception e){
							e.printStackTrace();
						}

					//}

				}

			} catch (Exception e) 
			{ 
				log.error(e.getLocalizedMessage());
			}
		} else {
			log.warn("Linking is not enabled");
		}

	}


}
