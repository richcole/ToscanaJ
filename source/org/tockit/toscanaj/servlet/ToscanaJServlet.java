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
    private static final int NODE_SIZE = 10;
    private static final Color NODE_COLOR = Color.blue;
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

        String diagramParameter = req.getParameter("diagram");
        if(diagramParameter == null) {
            resp.setContentType("text/html");
            printDiagramList(out);
        }
        else {
            resp.setContentType("image/svg-xml");
            printHead(out);
            printScript(out);
            int diagramNumber = Integer.parseInt(diagramParameter);
            printDiagram(diagramNumber,out);
            printEnd(out);
        }
    }

    private double addXPos(double oldPos) {
        return (oldPos + 400);
    }

    private double addYPos(double oldPos) {
        return (oldPos + 200);
    }

    private void printDiagram(int diagramNumber, PrintWriter out) {
        Diagram2D diagram = conceptualSchema.getDiagram(diagramNumber);
        int numLines = diagram.getNumberOfLines();

        for(int i = 0; i < numLines; i ++) {
            DiagramLine line = diagram.getLine(i);
            Point2D from = line.getFromPosition();
            Point2D to = line.getToPosition();

            out.println("<path d=\"M " + addXPos(from.getX()) + " " + addYPos(from.getY()) + " L " + addXPos(to.getX()) + " " + addYPos(to.getY()) + "\" stroke=\"black\" stroke-width=\"3\" />");
        }
        int numNodes = diagram.getNumberOfNodes();

        for(int i = 0; i < numNodes; i ++) {
            DiagramNode node = diagram.getNode(i);
            Point2D pos = node.getPosition();
            int radius = NODE_SIZE;
            Color nodeColor = NODE_COLOR;
            Color lineColor = LINE_COLOR;
            LabelInfo attrLabel = node.getAttributeLabelInfo();

		    out.println("<circle cx=\"" + addXPos(pos.getX()) + "\" cy=\"" + addYPos(pos.getY()) +"\" r=\"" + radius + "\" fill=\"blue\" stroke=\"black\" />");

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
//                    out.println("<text onload=\"adjustLabel(evt," + i + "," + addXPos(pos.getX()) + "," + addYPos(pos.getY()) + ");\" id=\"circle" + i + "Label\" x=\"" + addXPos((pos.getX() + offset.getX())) + "\" y=\"" + addYPos((pos.getY() + offset.getY() - 15.0 )) + "\" font-family=\"Verdana\" fill=\"black\">");
                    out.println("<text id=\"circle" + i + "Label\" x=\"" + addXPos((pos.getX() + offset.getX())) + "\" y=\"" + addYPos((pos.getY() + offset.getY() - 15.0 )) + "\" font-family=\"Verdana\" fill=\"black\">");
                    String attributeName = attribute.getName();
                    out.println(escapeEntities(attributeName));
                    out.println("</text>");
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
                    //out.println("<text x=\"" + (pos.getX() + offset.getX()) + "\" y=\"" + (pos.getY() + offset.getY() - 15.0 ) + "\" font-family=\"Verdana\" fill=\"" + textColor.getRGB() + "\">");
                    String object = objIt.next().toString();
                    //out.println(object);
                    //out.println("</text>");
                }
            }
        }
    }

    private String escapeEntities(String string) {
        String retVal = "";
        for(int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            switch(c) {
                case '<':
                    retVal += "&lt;";
                    break;
                case '>':
                    retVal += "&gt;";
                    break;
                case '&':
                    retVal += "&amp;";
                    break;
                case '\'':
                    retVal += "&apos;";
                    break;
                case '"':
                    retVal += "&quot;";
                    break;
                default:
                    retVal += c;
            }
        }
        return retVal;
    }

    private void printDiagramList(PrintWriter out) {
        out.println("<html><head><title>ToscanaJServlet</title></head><body>");
        out.println("<h1>All Diagrams:</h1>");
        int numberOfDiagrams = conceptualSchema.getNumberOfDiagrams();
        out.println("<ol>");
        for(int i = 0; i<numberOfDiagrams; i++) {
            Diagram2D diagram = conceptualSchema.getDiagram(i);
            out.println("<li><a href=\"" + SERVLET_URL + "?diagram=" + i + "\">" + diagram.getTitle() + "</a></li>");
        }
        out.println("</ol>");
        out.println("</body></html>");
    }

    private void parseSchemaFile() {
        String initParameter = getInitParameter("schemaFile");
        if(initParameter == null) {
            throw new RuntimeException("No file given as parameter");
        }
        File schemaFile = new File(initParameter);
        try {
            conceptualSchema = CSXParser.parse(new EventBroker(), schemaFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printScript(PrintWriter out) {
           out.println("<script type=\"text/ecmascript\"><![CDATA[");
           out.println("var svgDocument;");
//           out.println("function adjustLabel(evt, int i, double x, double y) { ");
//           out.println("evt.target.setAttribute(\"x\", x);");
//           out.println("} ");

           out.println("function initScript(evt) {");
           out.println("svgDocument = evt.getTarget().getOwnerDocument();");
           out.println("alert(svgDocument.getElementById(\"circle1Label\"));");
           out.println("}");

           out.println("]]></script>");
    }

    private void printHead(PrintWriter out) {
        out.println("<?xml version=\"1.0\"?>");
        out.println("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" \"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">");
        out.println("<svg onload=\"initScript(evt);\" width=\"30cm\" height=\"30cm\" xmlns=\"http://www.w3.org/2000/svg\">");
    }

    private void printEnd(PrintWriter out) {
        out.println("</svg>");
    }
}
