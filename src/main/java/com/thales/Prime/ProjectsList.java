package com.thales.Prime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.*;
import com.atlassian.jira.project.ProjectManager;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.jira.component.ComponentAccessor;

public class ProjectsList extends HttpServlet{

	private static final Logger log = LoggerFactory.getLogger(ProjectsList.class);

	private final TemplateRenderer templateRenderer;

	public ProjectsList(TemplateRenderer tr) 
	{
		log.info("Projects List Constructor");
		this.templateRenderer = tr;
	}

	//@Override
	//protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	//{
	//    resp.setContentType("text/html");
	//    resp.getWriter().write("<html><body>Hello World</body></html>");
	//}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException

	{
		//Map<String, Object> context = Maps.newHashMap(); //NEW
		Map<String, Object> context = new HashMap<String, Object>(); //NEW

		//context.put("projectManager",    ComponentManager.getComponentInstanceOfType(ProjectManager.class));  //NEW
		context.put("projectManager",    ComponentAccessor.getComponentOfType(ProjectManager.class));  //NEW

		templateRenderer.render("templates/projectsList/projectsList.vm", resp.getWriter()); //NEW
	}

}