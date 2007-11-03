/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import java.awt.geom.Point2D;

import net.sourceforge.toscanaj.util.xmlize.XMLHelper;
import net.sourceforge.toscanaj.util.xmlize.XMLSyntaxError;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

import org.jdom.Element;

/**
 * This encapsulates all information about a line in a diagram.
 *
 * This is just the information which nodes the line connects.
 */
public class DiagramLine<O,A> implements XMLizable {
    /**
     * Holds the starting node in the Diagram the line belongs to.
     */
    private DiagramNode<O,A> fromNode;

    private Diagram2D<O,A> diagram;

    /**
     * Holds the ending node in the Diagram the line belongs to.
     */
    private DiagramNode<O,A> toNode;
    public static final String DIAGRAM_LINE_ELEMENT_NAME = "edge";
    public static final String FROM_NODE_ATTRIBUTE_NAME = "from";
    public static final String TO_NODE_ATTRIBUTE_NAME = "to";

    /**
     * Initialises a line between the given nodes.
     */
    public DiagramLine(DiagramNode<O,A> fromNode, DiagramNode<O,A> toNode, Diagram2D<O,A> diagram) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.diagram = diagram;
    }

    public DiagramLine(Element element, Diagram2D<O,A> diagram) throws XMLSyntaxError {
        this.diagram = diagram;
        readXML(element);
    }

    public Element toXML() {
        Element retVal = new Element(DIAGRAM_LINE_ELEMENT_NAME);
        retVal.setAttribute(FROM_NODE_ATTRIBUTE_NAME, fromNode.getIdentifier());
        retVal.setAttribute(TO_NODE_ATTRIBUTE_NAME, toNode.getIdentifier());
        return retVal;
    }

    public void readXML(Element elem) throws XMLSyntaxError {
        XMLHelper.checkName(elem, DIAGRAM_LINE_ELEMENT_NAME);
        String fromId = XMLHelper.getAttribute(elem, FROM_NODE_ATTRIBUTE_NAME).getValue();
        String toId = XMLHelper.getAttribute(elem, TO_NODE_ATTRIBUTE_NAME).getValue();
        fromNode = diagram.getNode(fromId);
        toNode = diagram.getNode(toId);
    }

    /**
     * Returns the starting node in a Diagram in which DiagramLine object will be connecting.
     */
    public DiagramNode<O,A> getFromNode() {
        return this.fromNode;
    }

    /**
     * Returns the end node in a Diagram in which DiagramLine object will be connecting.
     */
    public DiagramNode<O,A> getToNode() {
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
     * This is just a convenience method to not having to get the node and then its position.
     */
    public Point2D getToPosition() {
        return this.toNode.getPosition();
    }

    /**
     * Sets the fromPoint to a node in a Diagram.
     */
    public void setFromPoint(DiagramNode<O,A> fromNode) {
        this.fromNode = fromNode;
    }

    /**
     * Sets the toPoint at the position of a node on a Diagram
     */
    public void setToPoint(DiagramNode<O,A> toNode) {
        this.toNode = toNode;
    }

    /**
     * Returns the length of this line.
     */
    public double getLength() {
        // do not use the position of the nodes, since this might be an offset in the
        // case of NDimDiagramNode
        double dx = this.toNode.getX() - this.fromNode.getX();
        double dy = this.toNode.getY() - this.fromNode.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Returns the distance of the given point to the line.
     * 
     * Note that the line has ends, a point which is further not to the side
     * of the line will have its distance determined by its distance to the
     * closer end point.
     */
    public double calculateDistance(Point2D point) {
        // first store all coordinates in nice short vars
        double fx = this.fromNode.getX();
        double fy = this.fromNode.getY();
        double tx = this.toNode.getX();
        double ty = this.toNode.getY();
        double x = point.getX();
        double y = point.getY();
        double dx = this.toNode.getX() - this.fromNode.getX();
        double dy = this.toNode.getY() - this.fromNode.getY();
        
        // calculate distance from line without considering end points
        // stay in square values until the end to avoid too many complex operations
        double lengthSq = (dx*dx + dy*dy);
        double distLineSq = ((tx-fx)*(fy-y) - (fx-x)*(ty-fy)) *
                            ((tx-fx)*(fy-y) - (fx-x)*(ty-fy)) / 
                                    lengthSq;
        // calculate the distance to the end points
        // do not use the node's position since that is only the offset in the
        // case of NDimDiagramNode
        double distFromSq = (fx-x)*(fx-x) + (fy-y)*(fy-y);
        double distToSq = (tx-x)*(tx-x) + (ty-y)*(ty-y);
        // the distance of a point on a perpendicular in an end node, distLine
        // from that end node away, measured from the other end node (Pythagoras)
        double distOppSq = distLineSq + lengthSq;
        
        // now check if we are outside the sides
        if(distFromSq > distToSq && distFromSq > distOppSq) {
            return Math.sqrt(distToSq);
        }
        if(distToSq > distFromSq && distToSq > distOppSq) {
            return Math.sqrt(distToSq);
        }
        return Math.sqrt(distLineSq);
    }
}
