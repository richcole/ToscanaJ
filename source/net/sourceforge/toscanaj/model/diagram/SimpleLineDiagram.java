/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import org.jdom.Element;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is an abstraction of all diagram related information.
 *
 * We assume that the first node we get is the top node of the diagram. The order
 * of all other nodes and the order of the lines does not matter.
 *
 * The coordinate system given can use y-coordinates either pointing upwards (the
 * usual mathematical system) or downwards (the usual computer coordinates). The
 * first call to getNode() or getBounds() will make sure that the coordinates
 * will be pointing downwards when reading.
 */

public class SimpleLineDiagram implements WriteableDiagram2D {
    /**
     * The title used for this diagram.
     */
    private String title = new String();

    /**
     * The list of nodes in the diagram.
     */
    private List nodes = new LinkedList();

    /**
     * The list of lines in the diagram.
     */
    private List lines = new LinkedList();

    /**
     * This is set to true once we determined the direction of the y-axis.
     */
    private boolean coordinateSystemChecked = false;

    private Element description = null;

    /**
     * The default constructor creates a diagram with just nothing in it at all.
     */
    public SimpleLineDiagram() {
    }

    /**
     * Returns the title of the diagram.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Change the title of the diagram.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the number of nodes in the diagram.
     */
    public int getNumberOfNodes() {
        return this.nodes.size();
    }

    /**
     * Returns the number of lines in the diagram.
     */
    public int getNumberOfLines() {
        return this.lines.size();
    }

    /**
     * Calculates a rectangle that includes all points.
     */
    public Rectangle2D getBounds() {
        if (!coordinateSystemChecked) {
            checkCoordinateSystem();
        }
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        for (int i = 0; i < this.nodes.size(); i++) {
            DiagramNode node = (DiagramNode) this.nodes.get(i);
            double x = node.getX();
            double y = node.getY();
            double rx = node.getRadiusX();
            double ry = node.getRadiusY();

            if (x - rx < minX) {
                minX = x - rx;
            }
            if (x + rx > maxX) {
                maxX = x + rx;
            }
            if (y - ry < minY) {
                minY = y - ry;
            }
            if (y + ry > maxY) {
                maxY = y + ry;
            }
        }
        return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Returns a node in the diagram.
     *
     * Numbers start with zero.
     */
    public DiagramNode getNode(int nodeNumber) {
        if (!coordinateSystemChecked) {
            checkCoordinateSystem();
        }
        return (DiagramNode) this.nodes.get(nodeNumber);
    }

    /**
     * Implements Diagram2D.getLine(int).
     */
    public DiagramLine getLine(int lineNumber) {
        return (DiagramLine) this.lines.get(lineNumber);
    }

    /**
     * Adds a node to the diagram (at the end of the list).
     *
     * The top node of a diagram always has to be added first.
     */
    public void addNode(DiagramNode node) {
        this.nodes.add(node);
    }

    /**
     * Returns the coordinates of a starting point of a line.
     *
     * Numbers start with zero.
     */
    public Point2D getFromPosition(int lineNumber) {
        DiagramLine line = (DiagramLine) this.lines.get(lineNumber);
        return line.getFromPosition();
    }

    /**
     * Returns the coordinates of an end point of a line.
     *
     * Numbers start with zero.
     */
    public Point2D getToPosition(int lineNumber) {
        DiagramLine line = (DiagramLine) this.lines.get(lineNumber);
        return line.getToPosition();
    }

    /**
     * Adds a line to the diagram (at the end of the list).
     */
    public void addLine(DiagramNode from, DiagramNode to) {
        this.lines.add(new DiagramLine(from, to));
    }

    /**
     * Returns the information on the object label of the diagram.
     */
    public LabelInfo getObjectLabel(int nodeNumber) {
        return ((DiagramNode) this.nodes.get(nodeNumber)).getObjectLabelInfo();
    }

    /**
     * Returns the information on the attribute label of the diagram.
     */
    public LabelInfo getAttributeLabel(int nodeNumber) {
        return ((DiagramNode) this.nodes.get(nodeNumber)).getAttributeLabelInfo();
    }

    /**
     * Makes sure the y-coordinates increase in the downward direction.
     */
    protected void checkCoordinateSystem() {
        if (this.nodes.size() > 1) { // no point in checking direction otherwise
            DiagramNode topNode = (DiagramNode) this.nodes.get(0);
            DiagramNode otherNode = (DiagramNode) this.nodes.get(1);
            if (topNode.getY() > otherNode.getY()) {
                // inverse coordinates (mirror using x-axis)
                Iterator it = this.nodes.iterator();
                while (it.hasNext()) {
                    DiagramNode node = (DiagramNode) it.next();
                    node.invertY();
                }
            }
        }
        this.coordinateSystemChecked = true;
    }

    public void setDescription(Element desc) {
        this.description = desc;
    }

    public Element getDescription() {
        return this.description;
    }
}