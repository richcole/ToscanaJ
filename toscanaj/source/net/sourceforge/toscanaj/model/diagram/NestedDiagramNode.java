/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.toscanaj.model.lattice.Concept;

/**
 * A class representing a diagram node with an inner diagram.
 */
public class NestedDiagramNode<O, A> extends DiagramNode<O, A> {
    /**
     * Stores the inner diagram.
     */
    private final Diagram2D<O, A> innerDiagram;

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
     * given diagram is the inner diagram in a nested diagram. The scale
     * parameter determines how much the new diagram will be scaled to
     * accomodate the inner diagrams.
     * 
     * If the dropAttributeLabels flag is set, the inner nodes will have no
     * attribute labels attached.
     */
    public NestedDiagramNode(final WriteableDiagram2D<O, A> outerDiagram,
            final DiagramNode<O, A> outerNode,
            final Diagram2D<O, A> innerDiagram, final double scale) {
        super(outerDiagram, "outer:" + outerNode.getIdentifier(),
                new Point2D.Double(outerNode.getX() * OUTER_SCALE_FACTOR,
                        outerNode.getY() * OUTER_SCALE_FACTOR), outerNode
                        .getConcept(), new LabelInfo(outerNode
                        .getAttributeLabelInfo()), null, outerNode
                        .getOuterNode());

        final double innerScale = (scale / OUTER_SCALE_FACTOR)
                * EXTRA_ELLIPSE_SCALING;

        // scale attribute label position
        this.attributeLabelInfo.setOffset(new Point2D.Double(
                this.attributeLabelInfo.getOffset().getX(),
                this.attributeLabelInfo.getOffset().getY()));

        // calculate an offset that places center of the inner diagram into the
        // middle of the node
        final Rectangle2D rect = innerDiagram.getBounds();
        final Point2D offset = new Point2D.Double(this.getX() - rect.getX()
                / innerScale - (rect.getWidth() / innerScale) / 2, this.getY()
                - rect.getY() / innerScale - (rect.getHeight() / innerScale)
                / 2);
        final SimpleLineDiagram<O, A> newDiag = new SimpleLineDiagram<O, A>();
        final Map<DiagramNode<O, A>, DiagramNode<O, A>> nodeMap = new Hashtable<DiagramNode<O, A>, DiagramNode<O, A>>();
        for (int i = 0; i < innerDiagram.getNumberOfNodes(); i++) {
            final DiagramNode<O, A> oldNode = innerDiagram.getNode(i);
            final Point2D newPos = new Point2D.Double(oldNode.getX()
                    / innerScale + offset.getX(), oldNode.getY() / innerScale
                    + offset.getY());
            final LabelInfo newAttrLabel = new LabelInfo(oldNode
                    .getAttributeLabelInfo());
            final LabelInfo newObjLabel = new LabelInfo(oldNode
                    .getObjectLabelInfo());
            final DiagramNode<O, A> newNode = new DiagramNode<O, A>(
                    newDiag,
                    outerNode.getIdentifier() + " x " + oldNode.getIdentifier(),
                    newPos, oldNode.getConcept(), newAttrLabel, newObjLabel,
                    this, oldNode.getRadiusX() / innerScale, oldNode
                            .getRadiusY()
                            / innerScale);
            nodeMap.put(oldNode, newNode);
            newDiag.addNode(newNode);
        }
        for (int i = 0; i < innerDiagram.getNumberOfLines(); i++) {
            final DiagramLine<O, A> line = innerDiagram.getLine(i);
            final DiagramNode<O, A> from = nodeMap.get(line.getFromNode());
            final DiagramNode<O, A> to = nodeMap.get(line.getToNode());
            newDiag.addLine(from, to);
        }
        newDiag.setEventBroker(innerDiagram.getEventBroker());
        this.innerDiagram = newDiag;

        calculateEllipse();
    }

    /**
     * Returns the inner diagram of the node.
     */
    public Diagram2D<O, A> getInnerDiagram() {
        return this.innerDiagram;
    }

    @Override
    public double getRadiusX() {
        return this.ellipse.getWidth() / 2;
    }

    @Override
    public double getRadiusY() {
        return this.ellipse.getHeight() / 2;
    }

    @Override
    public Concept<O, A>[] getConceptNestingList() {
        return this.innerDiagram.getNode(0).getConceptNestingList();
    }

    @Override
    public void setPosition(final double x, final double y) {
        final double dx = x - getPosition().getX();
        final double dy = y - getPosition().getY();
        final Iterator<DiagramNode<O, A>> it = this.innerDiagram.getNodes();
        while (it.hasNext()) {
            final DiagramNode<O, A> node = it.next();
            // do not call setPosition() to avoid callbacks
            node.position.setLocation(node.getX() + dx, node.getY() + dy);
        }
        super.setPosition(x, y);
    }

    /*
     * @todo the outer scaling should probably happen outside the node -- this
     * getter is just a hack to fix problems that shouldn't exist
     */
    public double getOuterScaleFactor() {
        return OUTER_SCALE_FACTOR;
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
     * (x/rx) ^2 + (y/ry)^2 = 1
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
        // (1) find the center of the bounding rectangle as center of the
        // ellipse
        final Rectangle2D bounds = this.innerDiagram.getBounds();
        final double cx = bounds.getCenterX();
        final double cy = bounds.getCenterY();

        // (2) set two initial radii that are never too big
        double rx = bounds.getWidth() / 2;
        double ry = bounds.getHeight() / 2;

        // (2b) we don't want too longish ellipses
        if (ry > rx * MAX_TALLNESS_RATIO) {
            rx = ry / MAX_TALLNESS_RATIO;
        }

        // (3) check all inner nodes if they are inside
        for (int i = 0; i < this.innerDiagram.getNumberOfNodes(); i++) {
            final DiagramNode<O, A> node = this.innerDiagram.getNode(i);
            double x = node.getX() - cx;
            double y = node.getY() - cy;
            if (x > 0) {
                x += node.getRadiusX();
            } else {
                x -= node.getRadiusX();
            }
            if (y > 0) {
                y += node.getRadiusY();
            } else {
                y -= node.getRadiusY();
            }
            final double val = x * x / (rx * rx) + y * y / (ry * ry);
            if (val > 1) { // point outside the ellipse
                final double scale = Math.sqrt(val);
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

    public void updateInnerDiagram(final Diagram2D<O, A> updatedInnerDiagram,
            final double scale) {
        final double innerScale = (scale / OUTER_SCALE_FACTOR)
                * EXTRA_ELLIPSE_SCALING;

        // calculate an offset that places center of the inner diagram into the
        // middle of the node
        final Rectangle2D rect = updatedInnerDiagram.getBounds();
        final Point2D offset = new Point2D.Double(this.getX() - rect.getX()
                / innerScale - (rect.getWidth() / innerScale) / 2, this.getY()
                - rect.getY() / innerScale - (rect.getHeight() / innerScale)
                / 2);

        // update all node positions
        for (int i = 0; i < innerDiagram.getNumberOfNodes(); i++) {
            final DiagramNode<O, A> oldNode = innerDiagram.getNode(i);
            final DiagramNode<O, A> updatedNode = updatedInnerDiagram
                    .getNode(i);
            oldNode.position.setLocation(updatedNode.getX() / innerScale
                    + offset.getX(), updatedNode.getY() / innerScale
                    + offset.getY());
        }

        calculateEllipse();
    }
}
