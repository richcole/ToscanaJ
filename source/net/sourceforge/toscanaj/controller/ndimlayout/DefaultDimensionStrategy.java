/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.ndimlayout;

import net.sourceforge.toscanaj.controller.cernato.PartialOrderOperations;
import net.sourceforge.toscanaj.model.context.Attribute;
import net.sourceforge.toscanaj.model.directedgraph.DirectedGraph;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.ndimdiagram.Dimension;
import net.sourceforge.toscanaj.model.order.PartialOrderNode;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class DefaultDimensionStrategy implements DimensionCreationStrategy {
    public Vector calculateDimensions(Lattice lattice) {
        Concept[] concepts = lattice.getConcepts();
        Vector redCons = new Vector();
        for (int i = 0; i < concepts.length; i++) {
            Concept concept = concepts[i];
            if (concept.isMeetIrreducible()) {
                redCons.add(concept);
            }
        }
        Concept[] reducedConcepts = new Concept[redCons.size()];
        redCons.toArray(reducedConcepts);
        Vector dimensions = new Vector();
        DirectedGraph graph = PartialOrderOperations.createGraphFromOrder(reducedConcepts);
        Set paths = graph.getMaximalPaths();
        for (Iterator it = paths.iterator(); it.hasNext();) {
            Vector path = (Vector) it.next();
            Vector attributes = new Vector();
            for (Iterator it2 = path.iterator(); it2.hasNext();) {
                PartialOrderNode node = (PartialOrderNode) it2.next();
                Concept concept = (Concept) node.getData();
                Iterator attrCont = concept.getAttributeContingentIterator();
                // we just take one attribute for the dimension (if instead of while)
                if (attrCont.hasNext()) {
                    Attribute attribute = (Attribute) attrCont.next();
                    attributes.add(attribute);
                }
            }
            dimensions.add(new Dimension(attributes));
        }
        return dimensions;
    }
}
