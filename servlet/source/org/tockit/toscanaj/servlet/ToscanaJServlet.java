/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.toscanaj.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.lattice.Concept;

import org.tockit.events.EventBroker;

public class ToscanaJServlet extends HttpServlet {

	private static int 		windowWidth, windowHeight;

	public void init() throws ServletException {
		
		super.init();
		if(!GlobalVariables.isInitialized()) {
			// get the schema filename from the servlet init parameter
			String inputFile = getInitParameter("schemaFile");        

			// if no filename defined        
			if(inputFile == null) {
				throw new RuntimeException("No file given as parameter");
			}
        
			String servletUrl = getInitParameter("baseURL");
			if (servletUrl == null) {
				throw new ServletException("No baseURL given as init parameter");
			}
			File schemaFile = new File(inputFile);
			
			try {
				GlobalVariables.initialize(schemaFile, servletUrl);
			} catch (Exception e) {
				throw new ServletException("Can not initialize servlet", e);
			}
		}
	}

	public void destroy() {
		super.destroy();
		try {
			DatabaseConnection.getConnection().disconnect();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	protected void doPost(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse)
		throws IOException {
		doGet(httpServletRequest, httpServletResponse);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws IOException {
		PrintWriter out = resp.getWriter();

		HttpSession session = req.getSession();

		String conceptParameter = req.getParameter("concept");
		String filterConceptParameter = req.getParameter("filterConcept");
		String diagramParameter = req.getParameter("diagram");
		String xParameter = req.getParameter("x");
		String yParameter = req.getParameter("y");
		DiagramHistory diagramHistory =
			(DiagramHistory) session.getAttribute("diagramHistory");
		if ((xParameter != null) && (yParameter != null)) {
			windowWidth = Integer.parseInt(xParameter);
			windowHeight = Integer.parseInt(yParameter);
		}
		if (diagramHistory == null) {
			diagramHistory = new DiagramHistory();
//			session.setAttribute("diagramHistory", diagramHistory);
		}
		if (filterConceptParameter != null) {
			int diagramNumber = Integer.parseInt(diagramParameter);
			diagramHistory.addDiagram(
				GlobalVariables.getConceptualSchema().getDiagram(diagramNumber));
			Concept concept = getConcept(diagramNumber, filterConceptParameter);
			diagramHistory.next(concept);
			resp.setContentType("text/html");
			//            printDiagramList(out);
			printDiagramAndList(out, diagramNumber, req);
		} else if (conceptParameter != null) {
			resp.setContentType("text/html");
			int diagramNumber = Integer.parseInt(diagramParameter);
			String queryParameter = req.getParameter("query");
			Query query;
			if (queryParameter != null) {
				int queryNumber = Integer.parseInt(queryParameter);
				query = (Query) GlobalVariables.getConceptualSchema().getQueries().get(queryNumber);
				session.setAttribute("query", query);
			} else {
				query = (Query) session.getAttribute("query");
				if (query == null) {
					query = (Query) GlobalVariables.getConceptualSchema().getQueries().get(0);
				}
			}
			printConceptList(
				diagramNumber,
				conceptParameter,
				query,
				diagramHistory,
				out);
		}
		else if (diagramParameter != null) {
			int diagramNumber = Integer.parseInt(diagramParameter);
			resp.setContentType("text/html");
			printDiagramAndList(out, diagramNumber, req);
		} else {
			resp.setContentType("text/html");
			printDiagramList(out);
		}
	}

	private void printConceptList(
		int diagramNumber,
		String nodeId,
		Query query,
		DiagramHistory diagramHistory,
		PrintWriter out) {
		out.println(
			"<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\"><title>ToscanaJServlet</title>");
		out.println("<meta http-equiv=\"Expires\" content=\"0\">");
		out.println("<meta http-equiv=\"Pragma\" content=\"no-cache;\">");
		out.println("<script language=\"javascript\">");
		out.println("function activate(y) { window.status = y; return true; }");
		out.println("function deactivate() { window.status = ''; return true; }");
		out.println("</script>");
		out.println("</head><body>");

		out.println("<table bgcolor=\"#297A01\"><tr><td>");

		out.println(
			"<form name=\"myForm2\" method=\"get\" action=\""
				+ GlobalVariables.getServletUrl()
				+ "\">");
		out.println(
			"<input type=\"hidden\" name=\"diagram\" value=\""
				+ diagramNumber
				+ "\">");
		out.println(
			"<input type=\"hidden\" name=\"concept\" value=\""
				+ nodeId
				+ "\">");
		out.println("<select name=\"query\">");

		Iterator queryIterator = GlobalVariables.getConceptualSchema().getQueries().iterator();
		int i = 0;

		while (queryIterator.hasNext()) {
			Query curQuery = (Query) queryIterator.next();
			if (query.getName().equals(curQuery.getName())) {
				out.println(
					"<option value=\""
						+ i
						+ "\" selected>"
						+ curQuery.getName()
						+ "</option>");
			} else {
				out.println(
					"<option value=\""
						+ i
						+ "\">"
						+ curQuery.getName()
						+ "</option>");
			}
			i++;
		}

		out.println("</select>");
		out.println("<input type=\"submit\" value=\"Change Query\">");
		out.println("</form>");
		out.println("</td></tr></table>");

		out.println("<h2>Result of query '" + query.getName() + "':</h1>");
		Concept concept = getConcept(diagramNumber, nodeId);
		out.println("<ul>");
		Object[] contents =
			GlobalVariables.getConceptInterpreter().executeQuery(
				query,
				concept,
				new ConceptInterpretationContext(
					diagramHistory,
					new EventBroker()));
					
		String searchQuery;
		
		for (int j = 0; j < contents.length; j++) {
			String object = contents[j].toString();
			if (query.getName().equals("List")) {
				searchQuery = constructQueryString(object);			
				out.println("<li><a onMouseOut=\"deactivate();\" onMouseOver=\"activate('Check info for " + escapeEntities(object) + " !!');return true;\" class=\"one\" target=\"_blank\" href=\"http://us.imdb.com/M/title-substring?" + searchQuery + "\">" + object + "</a></li>");
			}
			else {
				out.println("<li>" + object + "</li>");
			}
		}
		out.println("</ul>");
		//        out.println("<h2>Change query:</h2>");
		out.println("</body></html>");
	}

	private Concept getConcept(int diagramNumber, String nodeId) {
		Diagram2D diagram = GlobalVariables.getConceptualSchema().getDiagram(diagramNumber);
		DiagramNode node = diagram.getNode(nodeId);
		Concept concept = node.getConcept();
		return concept;
	}

	private void printDiagramAndList(
		PrintWriter out,
		int diagramNumber,
		HttpServletRequest req) {
		//        out.println("<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\"><title>ToscanaJServlet</title>");
		out.println(
			"<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">");

		out.println("</head><body>");

		int numberOfDiagrams = GlobalVariables.getConceptualSchema().getNumberOfDiagrams();

		out.println("<table bgcolor=\"#0B70A2\" width=\"2000\"><tr><td>");

		out.println(
			"<form name=\"myForm\" method=\"get\" action=\""
				+ GlobalVariables.getServletUrl()
				+ "\">");
		out.println("<select name=\"diagram\">");

		for (int i = 0; i < numberOfDiagrams; i++) {
			Diagram2D diagram = GlobalVariables.getConceptualSchema().getDiagram(i);
			if (diagramNumber == i) {
				out.println(
					"<option value=\""
						+ i
						+ "\" selected>"
						+ diagram.getTitle()
						+ "</option>");
			} else {
				out.println(
					"<option value=\""
						+ i
						+ "\">"
						+ diagram.getTitle()
						+ "</option>");
			}
			//            out.println("menu.addSubItem(\"listofdiagrams\", \"" + diagram.getTitle() + "\", \"" + diagram.getTitle() + "\", \"" + SERVLET_URL + "?diagram=" + i + "\"" + "+url2, \"_self\");");
			//            out.println("menu.addSubItem(\"listofdiagrams\", \"" + diagram.getTitle() + "\", \"" + diagram.getTitle() + "\", \"" + SERVLET_URL + "?diagram=" + i + "\"" + "+url2, \"_self\");");
		}

		out.println("</select>");
		out.println("<input type=\"submit\" value=\"Show Diagram\">");
		out.println("</form>");
		out.println("</td></tr></table>");

		out.println(
				"<br><br><center><table><tr>"
				+ "<td valign=\"center\"><embed " 
				+ "type=\"image/svg-xml\" "
				+ "width=\""
				+ 1024 * 0.67
				+ "\" height=\""
				+ 768 * 0.7
				+ "\" pluginspace=\"http://www.adobe.com/svg/viewer/install/\" "
				+ 
		"name=\"svg1\" src=\""
			+ GlobalVariables.getServletUrl()
			+ "/ToscanaJDiagrams?diagram="
			+ diagramNumber
			+ "&x="
			+ windowWidth
			+ "&y="
			+ windowHeight
			+ "\"></td></tr></table></center>");
		out.println("</body></html>");
	}

	private void printDiagramList(PrintWriter out) {
		//        out.println("<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\"><title>ToscanaJServlet</title>");
		out.println(
			"<html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">");
		out.println("<script language=\"javascript\">");
		out.println("function fillForm() {");
		out.println("   myForm.x.value = screen.width;");
		out.println("   myForm.y.value = screen.height;");
		out.println("}");
		out.println("</script>");
		out.println("</head><body>");
		int numberOfDiagrams = GlobalVariables.getConceptualSchema().getNumberOfDiagrams();

		out.println(
			"<table bgcolor=\"#0B70A2\" height=\"20\" width=\"2000\"><tr><td>");
		out.println(
			"<form name=\"myForm\" method=\"get\" action=\""
				+ GlobalVariables.getServletUrl()
				+ "\">");
		out.println("<select name=\"diagram\">");

		for (int i = 0; i < numberOfDiagrams; i++) {
			Diagram2D diagram = GlobalVariables.getConceptualSchema().getDiagram(i);
			out.println(
				"<option value=\""
					+ i
					+ "\">"
					+ diagram.getTitle()
					+ "</option>");
			//            out.println("menu.addSubItem(\"listofdiagrams\", \"" + diagram.getTitle() + "\", \"" + diagram.getTitle() + "\", \"" + SERVLET_URL + "?diagram=" + i + "\"" + "+url2, \"_self\");");
			//            out.println("menu.addSubItem(\"listofdiagrams\", \"" + diagram.getTitle() + "\", \"" + diagram.getTitle() + "\", \"" + SERVLET_URL + "?diagram=" + i + "\"" + "+url2, \"_self\");");
		}

		out.println("</select>");
		out.println("<input type=\"hidden\" name=\"x\">");
		out.println("<input type=\"hidden\" name=\"y\">");
		out.println("<input type=\"submit\" value=\"Show Diagram\">");
		out.println("</form>");
		out.println("<script language=\"javascript\">fillForm();</script>");
		out.println("</td></tr></table>");
		out.println(
			"<br><br><center><table><tr>"
				+ "<td valign=\"center\"><embed type=\"image/svg-xml\" "
				+ "pluginspace=\"http://www.adobe.com/svg/viewer/install/\" "
				+ "name=\"svg1\" width=\"" + 1024 * 0.67 + "\" height=\"" + 768 * 0.7 
				+ "\" src=\"./images/ToscanaJLogo.svg\"></td></tr></table></center>");

		//<A HREF="javascript:alert('Your resolution is '+screen.width+'x'+screen.height);">

		//out.println("<table><tr><td onMouseOver=\"javascript:window.showMenu(window.myMenu);\">Show Diagrams</td></table>");
		//        out.println("<h1>All Diagrams:</h1>");
		//        int numberOfDiagrams = GlobalVariables.getConceptualSchema().getNumberOfDiagrams();
		//        out.println("<ol>");
		//        for(int i = 0; i<numberOfDiagrams; i++) {
		//            Diagram2D diagram = GlobalVariables.getConceptualSchema().getDiagram(i);
		////            out.println("<li><a href=\"" + SERVLET_URL + "?diagram=" + i + "\">" + diagram.getTitle() + "</a></li>");
		//            out.println("<li><a class=\"one\" href=\"javascript:window.location('" + SERVLET_URL + "?diagram=" + i + "&x='+screen.width+'&y='+screen.height)\" target=\"diagram\">" + diagram.getTitle() + "</a></li>");
		//            //<A HREF="javascript:alert('Your resolution is '+screen.width+'x'+screen.height);">

		//        }
		//        out.println("</ol>");
		out.println("</body></html>");
	}
	
	public String constructQueryString(String object){

		StringTokenizer st = new StringTokenizer(object);
		String result = "";
		
		while (st.hasMoreTokens()) {
			result += escapeEntities(st.nextToken()) + "+";						
		}
		
		return result;
	}
	
	private String escapeEntities(String string) 
	{
    	
		System.out.println("ToscanaJDiagrams: escapeEntities");
		String retVal = "";
		for(int i = 0; i < string.length(); i++) 
		{
			char c = string.charAt(i);
			switch(c) {
				case '-':
					retVal += "";
					break;
				case '\'':
					retVal += "";
					break;
				case ',':
					retVal += "";
					break;
				case '?':
					retVal += "";
					break;
				case '=':
					retVal += "";
					break;
				case '"':
					retVal += "";
					break;
				default:
					retVal += c;
			}
		}
		return retVal;
        
	}

}
