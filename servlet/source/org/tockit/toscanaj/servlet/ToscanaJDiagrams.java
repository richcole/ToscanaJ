/**
 * Created by IntelliJ IDEA.
 * User: Adrian
 * Date: Nov 22, 2002
 * Time: 11:50:56 AM
 * To change this template use Options | File Templates.
 */
package org.tockit.toscanaj.servlet;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.Attribute;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.controller.fca.DatabaseConnectedConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.view.diagram.DiagramSchema;
import net.sourceforge.toscanaj.parser.CSXParser;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;

import org.tockit.events.EventBroker;

import java.net.URL;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.awt.geom.Point2D;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class ToscanaJDiagrams extends HttpServlet {
    private static String SERVLET_URL;
    private static double viewBoxX, viewBoxY, viewBoxWidth, viewBoxHeight;
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
        URL location = conceptualSchema.getDatabaseInfo().getEmbeddedSQLLocation();
//        try {
//            DatabaseConnection connection = new DatabaseConnection(new EventBroker());
//            connection.connect(databaseInfo);
//            URL location = conceptualSchema.getDatabaseInfo().getEmbeddedSQLLocation();
//            if (location != null) {
//                connection.executeScript(location);
//            }
//            DatabaseConnection.setConnection(connection);
//        } catch (DatabaseException e) {
//            e.getOriginal().printStackTrace();
//            throw new ServletException("Could not connect to database: " + e.getOriginal().getMessage(), e);
//        }
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

        resp.setContentType("image/svg-xml");
        int diagramNumber = Integer.parseInt(diagramParameter);
        printHead(diagramNumber, diagramHistory, out, req);
        printScript(out);
        printDiagram(diagramNumber, diagramHistory, out);
        printEnd(out);
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

    private void printScript(PrintWriter out) {
        out.println("<script type=\"text/javascript\">");
        out.println("function openWindow(URL) {");
        out.println("window1 = window.open(URL,\"my_new_window\",\"" + PopupOptions + "\")");
        out.println("}");
        out.println("</script>");
    }

    private void printHead(int diagramNumber, DiagramHistory diagramHistory, PrintWriter out, HttpServletRequest req) {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" \"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">");
        String screenWidth = req.getParameter("x");
        String screenHeight = req.getParameter("y");
        double width = Integer.parseInt(screenWidth) * 0.7;
        double height = Integer.parseInt(screenHeight) * 0.8;

        // calculate the appropriate view box size
        calculateViewBoxSize(diagramNumber, diagramHistory, out);
        out.println("<svg width=\"" + width + "\" height=\"" + height + "\" viewBox=\"" + viewBoxX + " " + viewBoxY + " " + viewBoxWidth + " " + viewBoxHeight + "\" xmlns=\"http://www.w3.org/2000/svg\">");
        //177.6 122.0 345.29999999999995 358.0
//        out.println("<svg width=\"" + 1024 + "\" height=\"" + 768 + "\" xmlns=\"http://www.w3.org/2000/svg\">");
    }

    private void printDiagram(int diagramNumber, DiagramHistory diagramHistory, PrintWriter out) {
        Diagram2D diagram = conceptualSchema.getDiagram(diagramNumber);
        int numLines = diagram.getNumberOfLines();
        int numLabel;

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

            out.println("<a target=\"diagrams\" xlink:href=\"" + SERVLET_URL + "?diagram=" + diagramNumber + "&amp;filterConcept=" + node.getIdentifier() + "\">");
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
                numLabel = 0;

                while (attrIt.hasNext()) {
                    Attribute attribute = (Attribute) attrIt.next();
                    numLabel++;

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
                                    "&amp;diagram=" + diagramNumber + "\" target=\"objectlabel\">");
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

    private void calculateViewBoxSize(int diagramNumber, DiagramHistory diagramHistory, PrintWriter out) {
        Diagram2D diagram = conceptualSchema.getDiagram(diagramNumber);

        double marginLeft = 0.0; // initializes the left most margin
        double marginRight = 0.0; // initializes the right most margin
        double marginTop = 0.0; // initializes the top most margin
        double marginBottom = 0.0; // initializes the bottom margin

        int numNodes = diagram.getNumberOfNodes();

        // iterate through the nodes to find to obtain the appropriate view box size for the diagram
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

            // if it is the first node
            if (i==0) {
                marginLeft = addXPos(pos.getX());
                marginRight = addXPos(pos.getX());
                marginTop = addYPos(pos.getY());
                marginBottom = addYPos(pos.getY());
            }
            // if it is not the first node
            else {
                // if current node is the left most node in the diagram
                if (addXPos(pos.getX()) < marginLeft) {
                    // update left margin
                    marginLeft = addXPos(pos.getX());
                }
                // if current node is not the left most node in the diagram
                else {
                    // do not update left margin
                }

                // if current node is the right most node in the diagram
                if (marginRight < addXPos(pos.getX())) {
                    // update right margin
                    marginRight = addXPos(pos.getX());
                }
                // if current node is not the right most node in the diagram
                else {
                    // do not update right margin
                }

                // if current node is the upper most node in the diagram
                if (addYPos(pos.getY()) < marginTop) {
                    // update top margin
                    marginTop = addYPos(pos.getY());
                }
                // if current node is not the upper most node in the diagram
                else {
                    // do not update top margin
                }

                // if current node is the bottom most node in the diagram
                if (marginBottom < addYPos(pos.getY())) {
                    // update bottom margin
                    marginBottom = addYPos(pos.getY());
                }
                else {
                    // do not update bottom margin
                }
            }

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

                // if node label is the right most part of the diagram
                if (marginRight < ((addXPos((pos.getX() + offset.getX())) - 3.0 - (0.35 * maxLabelWidth)) + maxLabelWidth)) {
                    // update right margin
                    marginRight = (addXPos((pos.getX() + offset.getX())) - 3.0 - (0.35 * maxLabelWidth)) + maxLabelWidth;
                }
                else {
                    // do not update right margin
                }

                // if node label is the left most part of the diagram
                if ((addXPos((pos.getX() + offset.getX())) - 3.0 - (0.65 * maxLabelWidth)) < marginLeft) {
                    // update left margin
                    marginLeft = addXPos((pos.getX() + offset.getX())) - 3.0 - (0.65 * maxLabelWidth);
                }
                else {
                    // do not update left margin
                }

                // if the node label is the upper most part of the diagram
                if (addYPos((pos.getY() + offset.getY() - 10.0 - maxLabelHeight )) < marginTop) {
                    // update top margin
                    marginTop = addYPos((pos.getY() + offset.getY() - 10.0 - maxLabelHeight ));
                }
                else {
                    // do not update top margin
                }

                // if the node label is the bottom most part of the diagram
                if (marginBottom < (addYPos((pos.getY() + offset.getY() - 10.0 - maxLabelHeight )) + maxLabelHeight)) {
                    // update bottom margin
                    marginBottom = addYPos((pos.getY() + offset.getY() - 10.0 - maxLabelHeight )) + maxLabelHeight;
                }
                else {
                    // do not update bottom margin
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

                    // if this label info is the right most part of the diagram
                    if (marginRight < ((addXPos((pos.getX() + offset.getX())) - 3.0 - (0.5 * maxLabelWidth)) + maxLabelWidth)) {
                        // update the right margin
                        marginRight = (addXPos((pos.getX() + offset.getX())) - 3.0 - (0.5 * maxLabelWidth)) + maxLabelWidth;
                    }
                    else {
                        // do not update right margin
                    }

                    // if this label info is the left most part of the diagram
                    if ((addXPos((pos.getX() + offset.getX())) - 3.0 - (0.5 * maxLabelWidth)) < marginLeft) {
                        // update margin left
                        marginLeft = (addXPos((pos.getX() + offset.getX())) - 3.0 - (0.5 * maxLabelWidth));
                    }
                    else {
                        // do not update left margin
                    }

                    // if this label info is the top most part of the diagram
                    if (addYPos((pos.getY() + offset.getY() - 10.0 + maxLabelHeight )) < marginTop) {
                        // update top margin
                        marginTop = addYPos((pos.getY() + offset.getY() - 10.0 - maxLabelHeight ));
                    }
                    else {
                        // do not update top margin
                    }

                    if (marginBottom < (addYPos((pos.getY() + offset.getY() - 10.0 + maxLabelHeight )) + maxLabelHeight)) {
                        // update bottom margin
                        marginBottom = addYPos((pos.getY() + offset.getY() - 10.0 + maxLabelHeight )) + maxLabelHeight;
                    }
                    else {
                        // do not update bottom margin
                    }

                }
            }
        }
        viewBoxX = marginLeft - 10.0;
        viewBoxY = marginTop;
        viewBoxWidth = (marginRight - marginLeft) + 20.0;
        viewBoxHeight = (marginBottom - marginTop) + 170.0;

    }

    private void printEnd(PrintWriter out) {
        out.println("</svg>");
    }

}
