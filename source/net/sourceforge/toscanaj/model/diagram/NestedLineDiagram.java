/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.model.lattice.AbstractConceptImplementation;

import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Stores a nested line diagram.
 *
 * The derivation from SimpleLineDiagram is just implementation, not design.
 *
 * @TODO Create common abstract class for both types of diagrams.
 */
public class NestedLineDiagram extends SimpleLineDiagram {
    /**
     * Creates a new nested line diagram from the given two diagrams.
     *
     * The inner diagram will be nested in each node of the outer diagram.
     * The level gives the depth of nesting for this diagram, starting with one
     * for a diagram on top level.
     */
    public NestedLineDiagram(Diagram2D outerDiagram, Diagram2D innerDiagram) {
        float scale = 1.2f * calculateNeededScaling(outerDiagram, innerDiagram);
        Hashtable nodeMap = new Hashtable();
        HashSet concepts = new HashSet(); // stores all concepts involved
//        long start = System.currentTimeMillis();
        for (int i = 0; i < outerDiagram.getNumberOfNodes(); i++) {
            DiagramNode oldNode = outerDiagram.getNode(i);
            NestedDiagramNode node = new NestedDiagramNode(oldNode, innerDiagram, scale,
                    !oldNode.getConcept().isTop());
            this.addNode(node);
            nodeMap.put(oldNode, node);
        }
        //      long end = System.currentTimeMillis();

        //       System.out.println("creating nodes"+(end-start));
        //       start = end;

        for (int i = 0; i < outerDiagram.getNumberOfLines(); i++) {
            DiagramLine oldLine = outerDiagram.getLine(i);
            NestedDiagramNode from = (NestedDiagramNode) nodeMap.get(oldLine.getFromNode());
            NestedDiagramNode to = (NestedDiagramNode) nodeMap.get(oldLine.getToNode());
            this.addLine(from, to);
            // add edges between inner nodes of two outer nodes
            for (int j = 0; j < innerDiagram.getNumberOfNodes(); j++) {
                AbstractConceptImplementation concept1 =
                        (AbstractConceptImplementation) from.getInnerDiagram().getNode(j).getConcept();
                AbstractConceptImplementation concept2 =
                        (AbstractConceptImplementation) to.getInnerDiagram().getNode(j).getConcept();
                concept1.addSubConcept(concept2);
                concept2.addSuperConcept(concept1);
                concepts.add(concept1);
                concepts.add(concept2);
            }
        }
//        end = System.currentTimeMillis();
//        System.out.println("creating lines and edges"+(end-start));
        //start = end;
        // build transitive closures for each concept
        Iterator it = concepts.iterator();
        while (it.hasNext()) {
            AbstractConceptImplementation cur = (AbstractConceptImplementation) it.next();
            cur.buildClosures();
        }
//        end = System.currentTimeMillis();
//        System.out.println("building the closures"+(end-start));
    }

    /**
     * Calculates the scaling we need to fit the inner nodes into the outer ones.
     *
     * The basic algorithm goes like this:
     * 1) minDist := minimal distance between two nodes in the outer diagram
     * 2) maxRadius := half of minDist (this radius would cause two nodes in
     *                 the outer diagram to just meet)
     * 3) possibleScaling := maxRadius/currentRadius (we can scale the radius of
     *                       the outer nodes by this without getting trouble)
     * 4) neededScaling := (innerSize/2)/currentRadius (this is what we need to fit
     *                     the inner diagram)
     * 5) additionalScaling := neededScaling/possibleScaling (we need to scale
     *                         that much more to fit the inner diagrams)
     *
     * The steps can be combined to:
     * 3) additionalScaling := innerSize/minDist
     *
     * This is of course oversimplified: we ignore the distinction between x and
     * y sizes, we ignore the distinction between ellipses and rectangles. The
     * code does more details but the idea is the same.
     */
    protected float calculateNeededScaling(Diagram2D outerDiagram, Diagram2D innerDiagram) {
        // if we have only one node we just don't scale at all (we can make this node as
        // big as we want, if there is no node we shouldn't be called at all
        if (outerDiagram.getNumberOfNodes() < 2) {
            return 1;
        }
        // first find minimal distance between nodes in the outer diagram
        float minDist = Float.MAX_VALUE;
        // compare first n-1 nodes to all after them
        for (int i = 0; i < outerDiagram.getNumberOfNodes() - 1; i++) {
            for (int j = i + 1; j < outerDiagram.getNumberOfNodes(); j++) {
                DiagramNode node1 = outerDiagram.getNode(i);
                DiagramNode node2 = outerDiagram.getNode(j);
                float dist = (float) node1.getPosition().distance(node2.getPosition());
                if (dist < minDist) {
                    minDist = dist;
                }
            }
        }

        // now we know how big we could draw circles in the outer diagram
        // the scaling we still need relates to the size of the inner diagram
        Rectangle2D rect = innerDiagram.getBounds();
        if (rect.getWidth() > rect.getHeight()) {
            return (float) rect.getWidth() / minDist;
        } else {
            return (float) rect.getHeight() / minDist;
        }
    }
}