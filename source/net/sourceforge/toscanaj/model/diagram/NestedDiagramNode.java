/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.List;

/**
 * A class representing a diagram node with an inner diagram.
 */
public class NestedDiagramNode extends DiagramNode {
    /**
     * Stores the inner diagram.
     */
    private Diagram2D innerDiagram;
    
    /**
     * Determines how much the outer diagram gets scaled.
     */
    private final static double OUTER_SCALE_FACTOR = 3.0;
    
    private Ellipse2D ellipse;
    
    private static final double EXTRA_ELLIPSE_SCALING = 1.1;
    
    private static final double MAX_TALLNESS_RATIO = 4;

    /**
     * Creates a new diagram node by copying the information from the given
     * other node and attaching the diagram.
     *
     * The given node determines the position in the outer diagram while the
     * given diagram is the inner diagram in a nested diagram. The scale parameter
     * determines how much the new diagram will be scaled to accomodate the inner
     * diagrams.
     *
     * If the dropAttributeLabels flag is set, the inner nodes will have no
     * attribute labels attached.
     *
     * Parameter scale is float due to JIT error in JDK 1.3, where in case of double
     * JIT was breaking transmission of value of scale after a while of program usage
     * , that lead to a distorted nested line diagrams.
     * 
     * @todo since we require 1.4 by now we could go back to double and see if
     * it breaks.
     */
    public NestedDiagramNode(DiagramNode outerNode, Diagram2D innerDiagram, float scale,
                             boolean dropAttributeLabels) {
        super("outer:" + outerNode.getIdentifier(),
                new Point2D.Double(outerNode.getX() * OUTER_SCALE_FACTOR, 
                                   outerNode.getY() * OUTER_SCALE_FACTOR),
                outerNode.getConcept(),
                new LabelInfo(outerNode.getAttributeLabelInfo()), null,
                outerNode.getOuterNode());
                
        double innerScale = (scale / OUTER_SCALE_FACTOR) * EXTRA_ELLIPSE_SCALING;
                
        // scale attribute label position
        this.attributeLabel.setOffset(new Point2D.Double(this.attributeLabel.getOffset().getX(),
                this.attributeLabel.getOffset().getY()));

        // calculate an offset that places center of the inner diagram into the middle of the node
        Rectangle2D rect = innerDiagram.getBounds();
        Point2D offset = new Point2D.Double(
                this.getX() - rect.getX()/innerScale - (rect.getWidth()/innerScale)/2,
                this.getY() - rect.getY()/innerScale - (rect.getHeight()/innerScale)/2);
        SimpleLineDiagram newDiag = new SimpleLineDiagram();
        Hashtable nodeMap = new Hashtable();
        for (int i = 0; i < innerDiagram.getNumberOfNodes(); i++) {
            DiagramNode oldNode = innerDiagram.getNode(i);
            Point2D newPos = new Point2D.Double(oldNode.getX()/innerScale + offset.getX(),
                    oldNode.getY()/innerScale + offset.getY());
            Concept newConcept = oldNode.getConcept();
            LabelInfo newAttrLabel;
            if (dropAttributeLabels) {
                newAttrLabel = null;
            } else {
                newAttrLabel = new LabelInfo(oldNode.getAttributeLabelInfo());
            }
            LabelInfo newObjLabel = new LabelInfo(oldNode.getObjectLabelInfo());
            DiagramNode newNode = new DiagramNode(outerNode.getIdentifier() + " x " + oldNode.getIdentifier(),
                    newPos, newConcept, newAttrLabel, newObjLabel, this, oldNode.getRadiusX()/innerScale, oldNode.getRadiusY()/innerScale);
            nodeMap.put(oldNode, newNode);
            newDiag.addNode(newNode);
        }
        for (int i = 0; i < innerDiagram.getNumberOfLines(); i++) {
            DiagramLine line = innerDiagram.getLine(i);
            DiagramNode from = (DiagramNode) nodeMap.get(line.getFromNode());
            DiagramNode to = (DiagramNode) nodeMap.get(line.getToNode());
            newDiag.addLine(from, to);

            // add direct neighbours in inner diagram to concepts
            ConceptImplementation concept1 =
                    (ConceptImplementation) from.getConcept();
            ConceptImplementation concept2 =
                    (ConceptImplementation) to.getConcept();
            concept1.addSubConcept(concept2);
            concept2.addSuperConcept(concept1);
        }

        this.innerDiagram = newDiag;
        
        calculateEllipse();
    }

    /**
     * Returns the inner diagram of the node.
     */
    public Diagram2D getInnerDiagram() {
        return this.innerDiagram;
    }

    public double getRadiusX() {
    	return this.ellipse.getWidth()/2;
    }

    public double getRadiusY() {
        return this.ellipse.getHeight()/2;
    }

    public List getConceptNestingList() {
        return innerDiagram.getNode(0).getConceptNestingList();
    }
    
    /**
     * Calculates the ellipse we should use.
     * 
     * The basic algorithm goes like this: 1) find the center of the bounding
     * rectangle as center of the ellipse 2) set the two radii as half the
     * width/height of the rectangle 3) iterate through each node and check if
     * they are outside or outside the ellipse, if outside, rescale the ellipse
     * so they are on the boundary
     *
     * The last step is based on the formula that
     *
     *    (x/rx) ^2 + (y/ry)^2 = 1
     *
     * for each point (x,y) on the ellipse with radii (rx,ry) in (0,0).
     *
     * If the left hand part is larger than 1, the point is on the outside, if
     * it is less than one, it is on the inside.
     * 
     * The x and y coordinates are shifted a bit away from the center to
     * accommodate for the radii of the inner nodes. This means the actual point
     * tested is the farmost corner of the bounding rectangle of the inner node
     * instead of the node center.
     */
    private void calculateEllipse() {
        // (1) find the center of the bounding rectangle as center of the ellipse
        Rectangle2D bounds = this.innerDiagram.getBounds();
        double cx = bounds.getCenterX();
        double cy = bounds.getCenterY();

        // (2) set two initial radii that are never too big
        double rx = bounds.getWidth()/2;
        double ry = bounds.getHeight()/2;
        
        // (2b) we don't want too longish ellipses
        if(ry > rx * MAX_TALLNESS_RATIO) {
        	rx = ry / MAX_TALLNESS_RATIO;
        }

        // (3) check all inner nodes if they are inside
        for(int i = 0; i < this.innerDiagram.getNumberOfNodes(); i++) {
        	DiagramNode node = this.innerDiagram.getNode(i);
        	double x = node.getX() - cx;
        	double y = node.getY() - cy;
            if( x > 0 ) {
                x += node.getRadiusX();
            } else {
                x -= node.getRadiusX();
            }
            if( y > 0 ) {
                y += node.getRadiusY();
            } else {
                y -= node.getRadiusY();
            }
            double val = x * x / (rx * rx) + y * y / (ry * ry);
            if(val > 1) { // point outside the ellipse
            	double scale = Math.sqrt(val);
            	rx *= scale;
            	ry *= scale;
            }
        }
        
        // add a bit
        rx *= EXTRA_ELLIPSE_SCALING;
        ry *= EXTRA_ELLIPSE_SCALING;
		
		// done -- store results
		this.ellipse = new Ellipse2D.Double(cx - rx, cy - ry, 2 * rx, 2 * ry);
    }
}
