/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.ndimlayout;

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.directedgraph.DirectedGraph;
import net.sourceforge.toscanaj.model.directedgraph.Node;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.ndimdiagram.Dimension;
import net.sourceforge.toscanaj.model.order.PartialOrderNode;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * This is taking the Cernatoesque layout to the concrete case.
 * 
 * While the layout used in Cernato-import makes use of metadata on the attributes (the
 * definitions of the value groups) to define a partial order on the attributes, this
 * dimension strategy does not assume any external order. Instead it uses the same
 * approach of finding all maximal chains in an order on the attribute, but this time
 * on the order induced by the concept lattice itself.
 * 
 * @todo Creating the attribute order externally could be a better idea. 
 */
public class MeetIrreducibleChainsDimensionStrategy implements DimensionCreationStrategy {
    public Vector<Dimension> calculateDimensions(Lattice lattice) {
        Concept[] concepts = lattice.getConcepts();
        Vector<Concept> redCons = new Vector<Concept>();
        for (int i = 0; i < concepts.length; i++) {
            Concept concept = concepts[i];
            if (concept.isMeetIrreducible()) {
                redCons.add(concept);
            }
        }
        Concept[] reducedConcepts = new Concept[redCons.size()];
        redCons.toArray(reducedConcepts);
        Vector<Dimension> dimensions = new Vector<Dimension>();
        DirectedGraph graph = PartialOrderOperations.createGraphFromOrder(reducedConcepts);
        Set<Vector<Node>> paths = graph.getMaximalPaths();
        for (Iterator<Vector<Node>> it = paths.iterator(); it.hasNext();) {
            Vector path = it.next();
            Vector<FCAElement> attributes = new Vector<FCAElement>();
            for (Iterator it2 = path.iterator(); it2.hasNext();) {
                PartialOrderNode node = (PartialOrderNode) it2.next();
                Concept concept = (Concept) node.getData();
                Iterator attrCont = concept.getAttributeContingentIterator();
                // we just take one attribute for the dimension (if instead of while)
                if (attrCont.hasNext()) {
                    FCAElement attribute = (FCAElement) attrCont.next();
                    attributes.add(attribute);
                }
            }
            dimensions.add(new Dimension(attributes));
        }
        return dimensions;
    }
}
