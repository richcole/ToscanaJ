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
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.Attribute;
import net.sourceforge.toscanaj.model.diagram.*;
import net.sourceforge.toscanaj.events.EventBroker;

import java.io.*;
import java.util.Iterator;
import java.awt.geom.Point2D;
import java.awt.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class ToscanaJServlet extends HttpServlet {
    private static final String SERVLET_URL = "http://localhost:8080/toscanaj";
    private ConceptualSchema conceptualSchema = null;
    private static final int NODE_SIZE = 30;
    private static final Color NODE_COLOR = new Color(60,60,255);
    private static final Color LINE_COLOR = Color.black;

    public void init() throws ServletException {
        super.init();
        parseSchemaFile();
    }

    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        doGet(httpServletRequest, httpServletResponse);
    }

    public void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        PrintWriter out = resp.getWriter();
        resp.setContentType("text/html");

        printHead(out);
        String diagramParameter = req.getParameter("diagram");
        if(diagramParameter == null) {
            printDiagramList(out);
        }
        else {
            int diagramNumber = Integer.parseInt(diagramParameter);
            printDiagram(diagramNumber);
        }
        printEnd(out);
    }

    private void printDiagram(int diagramNumber) {
        Diagram2D diagram = conceptualSchema.getDiagram(diagramNumber);
        int numLines = diagram.getNumberOfLines();
        for(int i = 0; i < numLines; i ++) {
            DiagramLine line = diagram.getLine(i);
            Point2D from = line.getFromPosition();
            Point2D to = line.getToPosition();
        }
        int numNodes = diagram.getNumberOfNodes();
        for(int i = 0; i < numNodes; i ++) {
            DiagramNode node = diagram.getNode(i);
            Point2D pos = node.getPosition();
            int radius = NODE_SIZE;
            Color nodeColor = NODE_COLOR;
            Color lineColor = LINE_COLOR;
            LabelInfo attrLabel = node.getAttributeLabelInfo();
            if(attrLabel != null) {
                // diagram related info (layout)
                Color backgroundColor = attrLabel.getBackgroundColor();
                Point2D offset = attrLabel.getOffset();
                Color textColor = attrLabel.getTextColor();
                int textAlignment = attrLabel.getTextAlignment(); // do not use yet
                // lattice related info (content)
                Concept concept = node.getConcept();
                Iterator attrIt = concept.getAttributeContingentIterator();
                while (attrIt.hasNext()) {
                    Attribute attribute = (Attribute) attrIt.next();
                    String attributeName = attribute.getName();
                }
            }
            LabelInfo objLabel = node.getObjectLabelInfo();
            if(objLabel != null) {
                Color backgroundColor = objLabel.getBackgroundColor();
                Point2D offset = objLabel.getOffset();
                Color textColor = objLabel.getTextColor();
                int textAlignment = objLabel.getTextAlignment(); // do not use yet
                Concept concept = node.getConcept();
                Iterator objIt = concept.getObjectContingentIterator();
                while (objIt.hasNext()) {
                    String object = objIt.next().toString();
                }
            }
        }
    }

    private void printDiagramList(PrintWriter out) {
        out.println("<h1>All Diagrams:</h1>");
        int numberOfDiagrams = conceptualSchema.getNumberOfDiagrams();
        out.println("<ol>");
        for(int i = 0; i<numberOfDiagrams; i++) {
            Diagram2D diagram = conceptualSchema.getDiagram(i);
            out.println("<li><a href=\"" + SERVLET_URL + "?diagram=" + i + "\">" + diagram.getTitle() + "</a></li>");
        }
        out.println("</ol>");
    }

    private void parseSchemaFile() {
        File schemaFile = new File(getInitParameter("schemaFile"));
        try {
            conceptualSchema = CSXParser.parse(new EventBroker(), schemaFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printHead(PrintWriter out) {
        out.println("<html>");
        out.println("<head><title>ToscanaJ Servlet</title></head>");
        out.println("<body>");
    }

    private void printEnd(PrintWriter out) {
        out.println("</body></html>");
    }
}
