/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.model.XML_Serializable;
import net.sourceforge.toscanaj.model.XML_SyntaxError;

import java.awt.geom.Point2D;

import org.jdom.Element;

/**
 * This encapsulates all information about a line in a diagram.
 *
 * This is just the information which nodes the line connects.
 */
public class DiagramLine implements XML_Serializable {
    /**
     * Holds the starting node in the Diagram the line belongs to.
     */
    private DiagramNode fromNode;

    /**
     * Holds the ending node in the Diagram the line belongs to.
     */
    private DiagramNode toNode;
    private static final String DIAGRAM_LINE_ELEMENT_NAME = "edge";
    private static final String FROM_NODE_ATTRIBUTE_NAME = "from";
    private static final String TO_NODE_ATTRIBUTE_NAME = "to";

    /**
     * Initialises a line between the given nodes.
     */
    public DiagramLine(DiagramNode fromNode, DiagramNode toNode) {
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

    public DiagramLine(Element element) throws XML_SyntaxError {
        readXML(element);
    }

    public Element toXML() {
        Element retVal = new Element(DIAGRAM_LINE_ELEMENT_NAME);
        retVal.setAttribute(FROM_NODE_ATTRIBUTE_NAME, fromNode.getIdentifier());
        retVal.setAttribute(TO_NODE_ATTRIBUTE_NAME, toNode.getIdentifier());
        return retVal;
    }

    public void readXML(Element elem) throws XML_SyntaxError {
        throw new XML_SyntaxError("Not yet implemented");
    }

    /**
     * Returns the starting node in a Diagram in which DiagramLine object will be connecting.
     */
    public DiagramNode getFromNode() {
        return this.fromNode;
    }

    /**
     * Returns the end node in a Diagram in which DiagramLine object will be connecting.
     */
    public DiagramNode getToNode() {
        return this.toNode;
    }

    /**
     * Returns coordinates of first node in a Diagram in which DiagramLine object will be connecting.
     *
     * This is just a convenienced method to not having to get the node and then its position.
     */
    public Point2D getFromPosition() {
        return this.fromNode.getPosition();
    }

    /**
     * Returns coordinates of second node in a Diagram in which DiagramLine object will be connecting.
     *
     * This is just a convenienced method to not having to get the node and then its position.
     */
    public Point2D getToPosition() {
        return this.toNode.getPosition();
    }

    /**
     * Sets the fromPoint to a node in a Diagram.
     */
    public void setFromPoint(DiagramNode fromNode) {
        this.fromNode = fromNode;
    }

    /**
     * Sets the toPoint at the position of a node on a Diagram
     */
    public void setToPoint(DiagramNode toNode) {
        this.toNode = toNode;
    }

    /**
     * Returns the length of this line.
     */
    public double getLength() {
        double dx = this.toNode.getX() - this.fromNode.getX();
        double dy = this.toNode.getY() - this.fromNode.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}