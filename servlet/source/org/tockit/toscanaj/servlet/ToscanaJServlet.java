/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.toscanaj.servlet;

import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.database.*;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.diagram.*;
import org.tockit.events.EventBroker;
import net.sourceforge.toscanaj.controller.fca.*;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.view.diagram.DiagramSchema;

import java.io.*;
import java.util.*;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;

public class ToscanaJServlet extends HttpServlet {
	private static String SERVLET_URL;
	private static double viewBoxX, viewBoxY, viewBoxWidth, viewBoxHeight;
	private static int windowWidth, windowHeight;
	private ConceptualSchema conceptualSchema = null;
	private DatabaseConnectedConceptInterpreter conceptInterpreter = null;
	private static final int NODE_SIZE = 10;
	private static final int IMAGE_HEIGHT = 600;
	private static final int IMAGE_WIDTH = 800;
	private static final int FONT_SIZE = 12;
	private static final String FONT_FAMILY = "Arial";
	private DiagramSchema diagramSchema = new DiagramSchema();
	private static final String PopupOptions =
		"toolbar=no,location=no,directories=no,status=no,menubar=yes,scrollbars=yes,resizable=no,copyhistory=yes,width=400,height=400";

	public void init() throws ServletException {
		super.init();
		SERVLET_URL = getInitParameter("baseURL");
		if (SERVLET_URL == null) {
			throw new ServletException("No baseURL given as init parameter");
		}
		try {
			parseSchemaFile();
		} catch (Exception e) {
			throw new ServletException("Parsing Schema failed", e);
		}
		DatabaseInfo databaseInfo = conceptualSchema.getDatabaseInfo();
		conceptInterpreter =
			new DatabaseConnectedConceptInterpreter(databaseInfo);
		try {
			DatabaseConnection connection =
				new DatabaseConnection(new EventBroker());
			connection.connect(databaseInfo);
			URL location =
				conceptualSchema.getDatabaseInfo().getEmbeddedSQLLocation();
			if (location != null) {
				connection.executeScript(location);
			}
			DatabaseConnection.setConnection(connection);
		} catch (DatabaseException e) {
			e.printStackTrace();
			throw new ServletException(
				"Could not connect to database: " + e.getCause().getMessage(),
				e);
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
		throws ServletException, IOException {
		doGet(httpServletRequest, httpServletResponse);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
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
				conceptualSchema.getDiagram(diagramNumber));
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
				query = (Query) conceptualSchema.getQueries().get(queryNumber);
				session.setAttribute("query", query);
			} else {
				query = (Query) session.getAttribute("query");
				if (query == null) {
					query = (Query) conceptualSchema.getQueries().get(0);
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

		out.println("</head><body>");

		out.println("<table bgcolor=\"#297A01\"><tr><td>");

		out.println(
			"<form name=\"myForm2\" method=\"get\" action=\""
				+ SERVLET_URL
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

		Iterator queryIterator = conceptualSchema.getQueries().iterator();
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
			conceptInterpreter.executeQuery(
				query,
				concept,
				new ConceptInterpretationContext(
					diagramHistory,
					new EventBroker()));
		for (int j = 0; j < contents.length; j++) {
			String object = contents[i].toString();
			out.println("<li>" + object + "</li>");
		}
		out.println("</ul>");
		//        out.println("<h2>Change query:</h2>");
		out.println("</body></html>");
	}

	private Concept getConcept(int diagramNumber, String nodeId) {
		Diagram2D diagram = conceptualSchema.getDiagram(diagramNumber);
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

		int numberOfDiagrams = conceptualSchema.getNumberOfDiagrams();

		out.println("<table bgcolor=\"#0B70A2\" width=\"2000\"><tr><td>");

		out.println(
			"<form name=\"myForm\" method=\"get\" action=\""
				+ SERVLET_URL
				+ "\">");
		out.println("<select name=\"diagram\">");

		for (int i = 0; i < numberOfDiagrams; i++) {
			Diagram2D diagram = conceptualSchema.getDiagram(i);
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

		out
			.println(
				"<br><br><center><table><tr>"
				+ "<td valign=\"center\"><embed type=\"image/svg-xml\" "
				+ "width=\""
				+ 1024 * 0.7
				+ "\" height=\""
				+ 768 * 0.8
				+ "\" pluginspace=\"http://www.adobe.com/svg/viewer/install/\" "
				+ 
		//                "name=\"svg1\" src=\"" + SERVLET_URL + "/ToscanaJDiagrams?&x='+screen.width+'&y='+screen.height+'&diagram=" + diagramNumber + "\"></td></tr></table></center>");
		//                "name=\"svg1\" src=\"" + SERVLET_URL + "/ToscanaJDiagrams?&x=1024&y=768&diagram=" + diagramNumber + "\"></td></tr></table></center>");
		"name=\"svg1\" src=\""
			+ SERVLET_URL
			+ "/ToscanaJDiagrams?diagram="
			+ diagramNumber
			+ "&x="
			+ windowWidth
			+ "&y="
			+ windowHeight
			+ "\"></td></tr></table></center>");
		//        "name=\"svg1\" src=\"" + SERVLET_URL + "/ToscanaJDiagrams?diagram=" + diagramNumber + "&x=1024&y=768\"></td></tr></table></center>");

		//<A HREF="javascript:alert('Your resolution is '+screen.width+'x'+screen.height);">

		//out.println("<table><tr><td onMouseOver=\"javascript:window.showMenu(window.myMenu);\">Show Diagrams</td></table>");
		//        out.println("<h1>All Diagrams:</h1>");
		//        int numberOfDiagrams = conceptualSchema.getNumberOfDiagrams();
		//        out.println("<ol>");
		//        for(int i = 0; i<numberOfDiagrams; i++) {
		//            Diagram2D diagram = conceptualSchema.getDiagram(i);
		////            out.println("<li><a href=\"" + SERVLET_URL + "?diagram=" + i + "\">" + diagram.getTitle() + "</a></li>");
		//            out.println("<li><a class=\"one\" href=\"javascript:window.location('" + SERVLET_URL + "?diagram=" + i + "&x='+screen.width+'&y='+screen.height)\" target=\"diagram\">" + diagram.getTitle() + "</a></li>");
		//            //<A HREF="javascript:alert('Your resolution is '+screen.width+'x'+screen.height);">

		//        }
		//        out.println("</ol>");
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
		int numberOfDiagrams = conceptualSchema.getNumberOfDiagrams();

		out.println(
			"<table bgcolor=\"#0B70A2\" height=\"20\" width=\"2000\"><tr><td>");
		out.println(
			"<form name=\"myForm\" method=\"get\" action=\""
				+ SERVLET_URL
				+ "\">");
		out.println("<select name=\"diagram\">");

		for (int i = 0; i < numberOfDiagrams; i++) {
			Diagram2D diagram = conceptualSchema.getDiagram(i);
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
			"<br><br><center><table><tr><td align=\"center\" width=\"75\" height=\"400\"></td>"
				+ "<td valign=\"center\"><embed type=\"image/svg-xml\" "
				+ "width=\"350\" height=\"200\" pluginspace=\"http://www.adobe.com/svg/viewer/install/\" "
				+ "name=\"svg1\" src=\"./images/ToscanaJLogo.svg\"></td></tr></table></center>");

		//<A HREF="javascript:alert('Your resolution is '+screen.width+'x'+screen.height);">

		//out.println("<table><tr><td onMouseOver=\"javascript:window.showMenu(window.myMenu);\">Show Diagrams</td></table>");
		//        out.println("<h1>All Diagrams:</h1>");
		//        int numberOfDiagrams = conceptualSchema.getNumberOfDiagrams();
		//        out.println("<ol>");
		//        for(int i = 0; i<numberOfDiagrams; i++) {
		//            Diagram2D diagram = conceptualSchema.getDiagram(i);
		////            out.println("<li><a href=\"" + SERVLET_URL + "?diagram=" + i + "\">" + diagram.getTitle() + "</a></li>");
		//            out.println("<li><a class=\"one\" href=\"javascript:window.location('" + SERVLET_URL + "?diagram=" + i + "&x='+screen.width+'&y='+screen.height)\" target=\"diagram\">" + diagram.getTitle() + "</a></li>");
		//            //<A HREF="javascript:alert('Your resolution is '+screen.width+'x'+screen.height);">

		//        }
		//        out.println("</ol>");
		out.println("</body></html>");
	}

	private void parseSchemaFile() throws Exception, IOException {
		String initParameter = getInitParameter("schemaFile");
		//        String initParameter = "/Adrian/examples/sql/pctest/pctest.csx";
		if (initParameter == null) {
			throw new RuntimeException("No file given as parameter");
		}
		File schemaFile = new File(initParameter);
		conceptualSchema = CSXParser.parse(new EventBroker(), schemaFile);
	}
}
