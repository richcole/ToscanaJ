/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.toscanaj.servlet;

import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.database.*;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.Attribute;
import net.sourceforge.toscanaj.model.diagram.*;
import org.tockit.events.EventBroker;
import net.sourceforge.toscanaj.controller.fca.*;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.view.diagram.DiagramSchema;

import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.geom.Point2D;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * @todo
 */
public class ToscanaJServlet extends HttpServlet {
    private static String SERVLET_URL;
    private ConceptualSchema conceptualSchema = null;
    private DatabaseConnectedConceptInterpreter conceptInterpreter = null;
    private static final int NODE_SIZE = 10;
    private static final int IMAGE_HEIGHT = 600;
    private static final int IMAGE_WIDTH = 800;
    private static final int FONT_SIZE = 12;
    private static final String FONT_FAMILY = "Arial";
    private DiagramSchema diagramSchema = new DiagramSchema();
    private static final String PopupOptions = "toolbar=no,location=no,directories=no,status=no,menubar=yes,scrollbars=yes,resizable=no,copyhistory=yes,width=400,height=400";

    public void init() throws ServletException {
        super.init();
        SERVLET_URL = getInitParameter("baseURL");
        if(SERVLET_URL == null) {
            throw new ServletException("No baseURL given as init parameter");
        }
        try {
            parseSchemaFile();
        } catch (Exception e) {
            throw new ServletException("Parsing Schema failed", e);
        }
        DatabaseInfo databaseInfo = conceptualSchema.getDatabaseInfo();
        conceptInterpreter = new DatabaseConnectedConceptInterpreter(databaseInfo);
        try {
            DatabaseConnection connection = new DatabaseConnection(new EventBroker());
            connection.connect(databaseInfo);
            URL location = conceptualSchema.getDatabaseInfo().getEmbeddedSQLLocation();
            if (location != null) {
                connection.executeScript(location);
            }
            DatabaseConnection.setConnection(connection);
        } catch (DatabaseException e) {
            e.getOriginal().printStackTrace();
            throw new ServletException("Could not connect to database: " + e.getOriginal().getMessage(), e);
        }
    }

    public void destroy() {
        super.destroy();
        try {
            DatabaseConnection.getConnection().disconnect();
        } catch (DatabaseException e) {
            e.getOriginal().printStackTrace();
        }
    }


    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        doGet(httpServletRequest, httpServletResponse);
    }

    public void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession();

        String conceptParameter = req.getParameter("concept");
        String filterConceptParameter = req.getParameter("filterConcept");
        String diagramParameter = req.getParameter("diagram");
        DiagramHistory diagramHistory = (DiagramHistory) session.getAttribute("diagramHistory");
        if(diagramHistory == null) {
            diagramHistory = new DiagramHistory();
            session.setAttribute("diagramHistory", diagramHistory);
        }
        if(filterConceptParameter != null) {
            int diagramNumber = Integer.parseInt(diagramParameter);
            diagramHistory.addDiagram(conceptualSchema.getDiagram(diagramNumber));
            Concept concept = getConcept(diagramNumber, filterConceptParameter);
            diagramHistory.next(concept);
            resp.setContentType("text/html");
            printDiagramList(out);
        }
        else if(conceptParameter != null) {
            resp.setContentType("text/html");
            int diagramNumber = Integer.parseInt(diagramParameter);
            String queryParameter = req.getParameter("query");
            Query query;
            if(queryParameter != null) {
                int queryNumber = Integer.parseInt(queryParameter);
                query = (Query) conceptualSchema.getQueries().get(queryNumber);
                session.setAttribute("query", query);
            }
            else {
                query = (Query) session.getAttribute("query");
                if(query == null) {
                    query = (Query) conceptualSchema.getQueries().get(0);
                }
            }
            printConceptList(diagramNumber, conceptParameter, query, diagramHistory, out);
        }
        else if(diagramParameter != null) {
            resp.setContentType("image/svg-xml");
            printHead(out);
            printScript(out);
            int diagramNumber = Integer.parseInt(diagramParameter);
            printDiagram(diagramNumber, diagramHistory, out);
            printEnd(out);
        }
        else {
            resp.setContentType("text/html");
            printDiagramList(out);
        }
    }

    private void printScript(PrintWriter out) {
        out.println("<script type=\"text/javascript\">");
        out.println("function openWindow(URL) {");
        out.println("window1 = window.open(URL,\"my_new_window\",\"" + PopupOptions + "\")");
        out.println("}");
        out.println("</script>");
    }

    private void printConceptList(int diagramNumber, String nodeId, Query query, DiagramHistory diagramHistory, PrintWriter out) {
        out.println("<html><head><title>ToscanaJServlet</title>");
        out.println("<meta http-equiv=\"Expires\" content=\"0\">");
        out.println("<meta http-equiv=\"Pragma\" content=\"no-cache;\">");
        out.println("</head><body>");
        out.println("<h1>Result of query '" + query.getName() + "':</h1>");
        Concept concept = getConcept(diagramNumber, nodeId);
        Iterator objIterator = conceptInterpreter.executeQuery(query,
                                                    concept,
                                                    new ConceptInterpretationContext(diagramHistory,new EventBroker())).iterator();
        out.println("<ul>");
        while (objIterator.hasNext()) {
            String object = objIterator.next().toString();
            out.println("<li>" + object + "</li>");
        }
        out.println("</ul>");
        out.println("<h2>Change query:</h2>");
        Iterator queryIterator = conceptualSchema.getQueries().iterator();
        int i = 0;
        out.println("<ol>");
        while (queryIterator.hasNext()) {
            Query curQuery = (Query) queryIterator.next();
            out.println("<li><a href=\"" + SERVLET_URL + "?diagram=" + diagramNumber + "&amp;concept=" + nodeId +
                        "&amp;query=" + i + "\">" + curQuery.getName() + "</a></li>");
            i++;
        }
        out.println("</ol>");
        out.println("</body></html>");
    }

    private Concept getConcept(int diagramNumber, String nodeId) {
        Diagram2D diagram = conceptualSchema.getDiagram(diagramNumber);
        DiagramNode node = diagram.getNode(nodeId);
        Concept concept = node.getConcept();
        return concept;
    }

    private double addXPos(double oldPos) {
        return (oldPos + 350);
    }

    private double addYPos(double oldPos) {
        return (oldPos + 150);
    }

    /**
     * Calculates the width of the label given a specific font metric.
     *
     * The width is calculated as the maximum string width plus two times the
     * leading and the descent from the font metrics. When drawing the text the
     * horizontal position should be the left edge of the label plus one times
     * the two values (FontMetrics::getLeading() and FontMetrics::getDescent()).
     */
    public double getWidth(FontMetrics fontMetrics, List entries) {
        if (entries.size() == 0) {
            return 0;
        }
        double result = 0;

        // find maximum width of string
        Iterator it = entries.iterator();
        while (it.hasNext()) {
            String cur = it.next().toString();
            double w = fontMetrics.stringWidth(cur);
            if (w > result) {
                result = w;
            }
        }

        // add two leadings and two descents to have some spacing on the left
        // and right side
        result += 2 * fontMetrics.getLeading() + 2 * fontMetrics.getDescent();

        return result;
    }

    /**
     * Calculates the height of the label given a specific font metric.
     */
    public int getHeight(FontMetrics fontMetrics) {
        return fontMetrics.getHeight();
    }

    private void printDiagram(int diagramNumber, DiagramHistory diagramHistory, PrintWriter out) {
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
            Concept concept = node.getConcept();
            ConceptInterpretationContext interpretationContext =
                                                new ConceptInterpretationContext(diagramHistory, new EventBroker());

            Point2D pos = node.getPosition();
            double radius;
            if( this.conceptInterpreter.isRealized(concept, interpretationContext)) {
                radius = NODE_SIZE;
            }
            else {
                radius = NODE_SIZE/3.0;
            }
            double gradientPosition = this.conceptInterpreter.getRelativeExtentSize(
                    concept,
                    interpretationContext,
                    ConceptInterpreter.REFERENCE_DIAGRAM
            );
            Color nodeColor = diagramSchema.getGradientColor(gradientPosition);
            Color lineColor = diagramSchema.getLineColor();
            LabelInfo attrLabel = node.getAttributeLabelInfo();

            BufferedImage bi = new BufferedImage(IMAGE_WIDTH,IMAGE_HEIGHT,BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bi.createGraphics();
            Font font = new Font(FONT_FAMILY, Font.PLAIN, 12);
            FontMetrics fm = g2d.getFontMetrics(font);

            out.println("<a xlink:href=\"" + SERVLET_URL + "?diagram=" + diagramNumber + "&amp;filterConcept=" + node.getIdentifier() + "\">");
            out.println("<circle cx=\"" + addXPos(pos.getX()) + "\" cy=\"" + addYPos(pos.getY()) +"\" r=\"" +
                        radius + "\" style=\"fill:RGB(" + nodeColor.getRed() + "," + nodeColor.getGreen() + "," +
                        nodeColor.getBlue() + ")\" stroke=\"black\" />");
            out.println("</a>");

            if(attrLabel != null) {
                // diagram related info (layout)
                Color backgroundColor = attrLabel.getBackgroundColor();
                Point2D offset = attrLabel.getOffset();
                Color textColor = attrLabel.getTextColor();
                int textAlignment = attrLabel.getTextAlignment(); // do not use yet
                // lattice related info (content)
                Iterator attrIt = concept.getAttributeContingentIterator();
                List attrList = new ArrayList();
                while (attrIt.hasNext()) {
                    Attribute attribute = (Attribute) attrIt.next();
                    attrList.add(attribute.getData().toString());
                }
                double maxLabelWidth = getWidth(fm, attrList);
                int maxLabelHeight = getHeight(fm);

                if ((textAlignment == 1) && (maxLabelWidth != 0.0)) {
                     out.println("<rect width=\"" + maxLabelWidth + "\" height=\"" + maxLabelHeight + "\" x=\"" + (addXPos((pos.getX() + offset.getX())) - 3.0 - (0.5 * maxLabelWidth)) + "\" y=\"" + addYPos((pos.getY() + offset.getY() - 10.0 - maxLabelHeight )) + "\" style=\"fill:RGB(" + backgroundColor.getRed() + "," + backgroundColor.getGreen() + "," + backgroundColor.getBlue() +")\" stroke=\"Black\" />");
                }
                else if ((textAlignment == 2) && (maxLabelWidth != 0.0)) {
                     out.println("<rect width=\"" + maxLabelWidth + "\" height=\"" + maxLabelHeight + "\" x=\"" + (addXPos((pos.getX() + offset.getX())) - 3.0 - (0.65 * maxLabelWidth)) + "\" y=\"" + addYPos((pos.getY() + offset.getY() - 10.0 - maxLabelHeight )) + "\" style=\"fill:RGB(" + backgroundColor.getRed() + "," + backgroundColor.getGreen() + "," + backgroundColor.getBlue() +")\" stroke=\"Black\" />");
                }
                else if (maxLabelWidth != 0.0) {
                     out.println("<rect width=\"" + maxLabelWidth + "\" height=\"" + maxLabelHeight + "\" x=\"" + (addXPos((pos.getX() + offset.getX())) - 3.0 - (0.35 * maxLabelWidth)) + "\" y=\"" + addYPos((pos.getY() + offset.getY() - 10.0 - maxLabelHeight )) + "\" style=\"fill:RGB(" + backgroundColor.getRed() + "," + backgroundColor.getGreen() + "," + backgroundColor.getBlue() +")\" stroke=\"Black\" />");
                }
                else {
                    // do not print the label
                }

                drawLineConnector(out, pos, offset, maxLabelWidth, maxLabelHeight, textAlignment);

                attrIt = concept.getAttributeContingentIterator();
                while (attrIt.hasNext()) {
                    Attribute attribute = (Attribute) attrIt.next();
                    if (textAlignment == 1) {
                        out.println("<text font-family=\"" + FONT_FAMILY + "\" font-size=\"" + FONT_SIZE + "\" x=\"" + (addXPos((pos.getX() + offset.getX())) - (0.5 * maxLabelWidth)) + "\" y=\"" + addYPos((pos.getY() + offset.getY() - 12.0 )) + "\" style=\"fill:RGB(" + textColor.getRed() + "," + textColor.getGreen() + "," + textColor.getBlue() + ")\" >");
                        String attributeName = attribute.getData().toString();
                        out.println(escapeEntities(attributeName));
                        out.println("</text>");
                    }
                    else if (textAlignment == 2) {
                        out.println("<text font-family=\"" + FONT_FAMILY + "\" font-size=\"" + FONT_SIZE + "\" x=\"" + (addXPos((pos.getX() + offset.getX())) - (0.65 * maxLabelWidth)) + "\" y=\"" + addYPos((pos.getY() + offset.getY() - 12.0 )) + "\" style=\"fill:RGB(" + textColor.getRed() + "," + textColor.getGreen() + "," + textColor.getBlue() + ")\" >");
                        String attributeName = attribute.getData().toString();
                        out.println(escapeEntities(attributeName));
                        out.println("</text>");
                    }
                    else {
                        out.println("<text font-family=\"" + FONT_FAMILY + "\" font-size=\"" + FONT_SIZE + "\" x=\"" + (addXPos((pos.getX() + offset.getX())) - (0.35 * maxLabelWidth)) + "\" y=\"" + addYPos((pos.getY() + offset.getY() - 12.0 )) + "\" style=\"fill:RGB(" + textColor.getRed() + "," + textColor.getGreen() + "," + textColor.getBlue() + ")\" >");
                        String attributeName = attribute.getData().toString();
                        out.println(escapeEntities(attributeName));
                        out.println("</text>");
                    }
                }

            }
            LabelInfo objLabel = node.getObjectLabelInfo();
            if(objLabel != null) {
                Color backgroundColor = objLabel.getBackgroundColor();
                Point2D offset = objLabel.getOffset();
                Color textColor = objLabel.getTextColor();
                int textAlignment = objLabel.getTextAlignment(); // do not use yet
                int objectCount = conceptInterpreter.getObjectCount(concept, interpretationContext);

                if (objectCount != 0) {
                    double maxLabelWidth = fm.stringWidth(String.valueOf(objectCount)) + 2 * fm.getLeading() + 2 * fm.getDescent();
                    int maxLabelHeight = getHeight(fm);

                    if ((textAlignment == 1) && (maxLabelWidth != 0.0)) {
//                        out.println("<a onclick=\"openWindow('" + SERVLET_URL + "?concept=" + node.getIdentifier() +
//                                    "&amp;diagram=" + diagramNumber + "')\">");
                        out.println("<a xlink:href=\"" + SERVLET_URL + "?concept=" + node.getIdentifier() +
                                    "&amp;diagram=" + diagramNumber + "\" target=\"_blank\">");
                        out.println("<rect width=\"" + maxLabelWidth + "\" height=\"" + maxLabelHeight + "\" x=\"" + (addXPos((pos.getX() + offset.getX())) - 3.0 - (0.5 * maxLabelWidth)) + "\" y=\"" + addYPos((pos.getY() + offset.getY() - 10.0 + maxLabelHeight )) + "\" style=\"fill:RGB(" + backgroundColor.getRed() + "," + backgroundColor.getGreen() + "," + backgroundColor.getBlue() +")\" stroke=\"Black\" />");
//                        out.println("<rect width=\"" + maxLabelWidth + "\" height=\"" + maxLabelHeight + "\" x=\"" + (addXPos((pos.getX() + offset.getX())) - 3.0 - (0.5 * maxLabelWidth)) + "\" y=\"" + addYPos((pos.getY() + offset.getY() - 10.0 + maxLabelHeight )) + "\" style=\"fill:RGB(" + backgroundColor.getRed() + "," + backgroundColor.getGreen() + "," + backgroundColor.getBlue() +")\" stroke=\"Black\" />");
//                        out.println("<rect onclick=\"openWindow('" + SERVLET_URL + "?concept=" + node.getIdentifier() + "&amp;diagram=" + diagramNumber + "')\" width=\"" + maxLabelWidth + "\" height=\"" + maxLabelHeight + "\" x=\"" + (addXPos((pos.getX() + offset.getX())) - 3.0 - (0.5 * maxLabelWidth)) + "\" y=\"" + addYPos((pos.getY() + offset.getY() - 10.0 + maxLabelHeight )) + "\" style=\"fill:RGB(" + backgroundColor.getRed() + "," + backgroundColor.getGreen() + "," + backgroundColor.getBlue() +")\" stroke=\"Black\" />");
                        drawLineConnector2(out, pos, offset, maxLabelWidth, maxLabelHeight, textAlignment);
                        out.println("<text x=\"" + (addXPos(pos.getX() + offset.getX()) - (0.5 * maxLabelWidth))  + "\" y=\"" + addYPos((pos.getY() + NODE_SIZE + offset.getY() + 5.0)) + "\" font-size=\""+ FONT_SIZE + "\" font-family=\"" + FONT_FAMILY + "\" style=\"fill:RGB(" + textColor.getRed() + "," + textColor.getGreen() + "," + textColor.getBlue() +")\">");
                        try {
                            out.println(objectCount);
                        } catch (Exception e) {
                            e.printStackTrace(out);
                        }
                        out.println("</text>");
                        out.println("</a>");
                    }
                }
            }
        }
    }

    private void drawLineConnector (PrintWriter out, Point2D centerOfNode, Point2D offset, double labelWidth, int labelHeight, int theTextAlignment) {
        if ((theTextAlignment == 1) && (labelWidth != 0.0)) {
            out.println("<path d=\"M " + addXPos(centerOfNode.getX()) + " " + addYPos((centerOfNode.getY() - NODE_SIZE)) + " L " + addXPos((centerOfNode.getX() + offset.getX())) + " " + addYPos((centerOfNode.getY() - NODE_SIZE + offset.getY())) + "\" stroke=\"black\" stroke-width=\"1\" />");
        }
        else if ((theTextAlignment == 2) && (labelWidth != 0.0)) {
            out.println("<path d=\"M " + addXPos(centerOfNode.getX()) + " " + addYPos((centerOfNode.getY() - NODE_SIZE)) + " L " + (addXPos((centerOfNode.getX() + offset.getX())) - 3.0 - ( 0.5 * ( 0.65 * labelWidth))) + " " + addYPos((centerOfNode.getY() + offset.getY() - 10.0)) + "\" stroke=\"black\" stroke-width=\"1\" />");
        }
        else if (labelWidth != 0.0) {
            out.println("<path d=\"M " + addXPos(centerOfNode.getX()) + " " + addYPos((centerOfNode.getY() - NODE_SIZE)) + " L " + ((addXPos((centerOfNode.getX() + offset.getX())) - 3.0) + ( 0.5 * ( 0.35 * labelWidth))) + " " + addYPos((centerOfNode.getY() + offset.getY() - 10.0)) + "\" stroke=\"black\" stroke-width=\"1\" />");
        }
        else {
            // do not print the line connector
        }
    }

    private void drawLineConnector2(PrintWriter out, Point2D centerOfNode, Point2D offset, double labelWidth, int labelHeight, int theTextAlignment) {
        if ((theTextAlignment == 1) && (labelWidth != 0.0)) {
            out.println("<path d=\"M " + addXPos(centerOfNode.getX()) + " " + addYPos((centerOfNode.getY() + NODE_SIZE)) + " L " + addXPos((centerOfNode.getX() + offset.getX())) + " " + addYPos((centerOfNode.getY() + NODE_SIZE - 5.0 + offset.getY())) + "\" stroke=\"black\" stroke-width=\"1\" />");
        }
        else if ((theTextAlignment == 2) && (labelWidth != 0.0)) {
            out.println("<path d=\"M " + addXPos(centerOfNode.getX()) + " " + addYPos((centerOfNode.getY() + NODE_SIZE)) + " L " + (addXPos((centerOfNode.getX() + offset.getX())) - 3.0 - ( 0.5 * ( 0.65 * labelWidth))) + " " + addYPos((centerOfNode.getY() + offset.getY() - 10.0)) + "\" stroke=\"black\" stroke-width=\"1\" />");
        }
        else if (labelWidth != 0.0) {
            out.println("<path d=\"M " + addXPos(centerOfNode.getX()) + " " + addYPos((centerOfNode.getY() + NODE_SIZE)) + " L " + ((addXPos((centerOfNode.getX() + offset.getX())) - 3.0) + ( 0.5 * ( 0.35 * labelWidth))) + " " + addYPos((centerOfNode.getY() + offset.getY() - 10.0)) + "\" stroke=\"black\" stroke-width=\"1\" />");
        }
        else {
            // do not print the line connector
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

    private void parseSchemaFile() throws Exception, IOException {
        String initParameter = getInitParameter("schemaFile");
//        String initParameter = "/Adrian/examples/sql/pctest/pctest.csx";
        if(initParameter == null) {
            throw new RuntimeException("No file given as parameter");
        }
        File schemaFile = new File(initParameter);
        conceptualSchema = CSXParser.parse(new EventBroker(), schemaFile);
    }

    private void printHead(PrintWriter out) {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" \"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">");
        out.println("<svg viewBox=\"0 0 1000 850\" width=\"" + IMAGE_WIDTH + "\" height=\"" + IMAGE_HEIGHT + "\" xmlns=\"http://www.w3.org/2000/svg\">");
    }

    private void printEnd(PrintWriter out) {
        out.println("</svg>");
    }
}
