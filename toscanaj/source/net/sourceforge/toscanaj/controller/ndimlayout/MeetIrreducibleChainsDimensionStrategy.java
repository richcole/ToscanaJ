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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
public class MeetIrreducibleChainsDimensionStrategy implements DimensionCreationStrategy<FCAElement> {
    @SuppressWarnings("unchecked")
	public<O> List<Dimension<FCAElement>> calculateDimensions(Lattice<O,FCAElement> lattice) {
        Concept<O,FCAElement>[] concepts = lattice.getConcepts();
        Vector<Concept<O,FCAElement>> redCons = new Vector<Concept<O,FCAElement>>();
        for (int i = 0; i < concepts.length; i++) {
            Concept<O,FCAElement> concept = concepts[i];
            if (concept.isMeetIrreducible()) {
                redCons.add(concept);
            }
        }
        Concept<O,FCAElement>[] reducedConcepts = new Concept[redCons.size()];
        redCons.toArray(reducedConcepts);
        List<Dimension<FCAElement>> dimensions = new ArrayList<Dimension<FCAElement>>();
        DirectedGraph<PartialOrderNode<Concept<O,FCAElement>>> graph = PartialOrderOperations.createGraphFromOrder(reducedConcepts);
        Set<Vector<PartialOrderNode<Concept<O, FCAElement>>>> paths = graph.getMaximalPaths();
        for (Iterator<Vector<PartialOrderNode<Concept<O, FCAElement>>>> it = paths.iterator(); it.hasNext();) {
            Vector<PartialOrderNode<Concept<O, FCAElement>>> path = it.next();
            Vector<FCAElement> attributes = new Vector<FCAElement>();
            for (Iterator<PartialOrderNode<Concept<O, FCAElement>>> it2 = path.iterator(); it2.hasNext();) {
                PartialOrderNode<Concept<O, FCAElement>> node = it2.next();
                Concept<O,FCAElement> concept = node.getData();
                Iterator<FCAElement> attrCont = concept.getAttributeContingentIterator();
                // we just take one attribute for the dimension (if instead of while)
                if (attrCont.hasNext()) {
                    FCAElement attribute = attrCont.next();
                    attributes.add(attribute);
                }
            }
            dimensions.add(new Dimension<FCAElement>(attributes));
        }
        return dimensions;
    }
}
