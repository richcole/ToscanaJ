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
    public NestedLineDiagram(Diagram2D outerDiagram, Diagram2D innerDiagram, int level) {
        Rectangle2D innerBounds = innerDiagram.getBounds();
        double scale = (innerBounds.getWidth()+2*innerDiagram.getNode(0).getRadiusX()) /
                                      outerDiagram.getNode(0).getRadiusX();
        Hashtable nodeMap = new Hashtable();
        HashSet concepts = new HashSet(); // stores all concepts involved
        for(int i=0; i < outerDiagram.getNumberOfNodes(); i++ ) {
            DiagramNode oldNode = outerDiagram.getNode(i);
            NestedDiagramNode node = new NestedDiagramNode(oldNode, innerDiagram, scale,
                                                           !oldNode.getConcept().isTop(),
                                                           level );
            this.addNode(node);
            nodeMap.put(oldNode,node);
        }
        for(int i=0; i < outerDiagram.getNumberOfLines(); i++ ) {
            DiagramLine oldLine = outerDiagram.getLine(i);
            NestedDiagramNode from = (NestedDiagramNode)nodeMap.get(oldLine.getFromNode());
            NestedDiagramNode to = (NestedDiagramNode)nodeMap.get(oldLine.getToNode());
            this.addLine(from,to);
            // add edges between inner nodes of two outer nodes
            for(int j=0; j < innerDiagram.getNumberOfNodes(); j++ ) {
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

        // build transitive closures for each concept
        Iterator it = concepts.iterator();
        while(it.hasNext()) {
            AbstractConceptImplementation cur = (AbstractConceptImplementation)it.next();
            cur.buildClosures();
        }
    }
}