/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.ndimlayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.directedgraph.DirectedGraph;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.ndimdiagram.Dimension;
import net.sourceforge.toscanaj.model.order.PartialOrderNode;

/**
 * This is taking the Cernatoesque layout to the concrete case.
 * 
 * While the layout used in Cernato-import makes use of metadata on the
 * attributes (the definitions of the value groups) to define a partial order on
 * the attributes, this dimension strategy does not assume any external order.
 * Instead it uses the same approach of finding all maximal chains in an order
 * on the attribute, but this time on the order induced by the concept lattice
 * itself.
 * 
 * @todo Creating the attribute order externally could be a better idea.
 */
public class MeetIrreducibleChainsDimensionStrategy implements
        DimensionCreationStrategy<FCAElement> {
    @SuppressWarnings("unchecked")
    public <O> List<Dimension<FCAElement>> calculateDimensions(
            final Lattice<O, FCAElement> lattice) {
        final Concept<O, FCAElement>[] concepts = lattice.getConcepts();
        final Vector<Concept<O, FCAElement>> redCons = new Vector<Concept<O, FCAElement>>();
        for (final Concept<O, FCAElement> concept : concepts) {
            if (concept.isMeetIrreducible()) {
                redCons.add(concept);
            }
        }
        final Concept<O, FCAElement>[] reducedConcepts = new Concept[redCons
                .size()];
        redCons.toArray(reducedConcepts);
        final List<Dimension<FCAElement>> dimensions = new ArrayList<Dimension<FCAElement>>();
        final DirectedGraph<PartialOrderNode<Concept<O, FCAElement>>> graph = PartialOrderOperations
                .createGraphFromOrder(reducedConcepts);
        final Set<Vector<PartialOrderNode<Concept<O, FCAElement>>>> paths = graph
                .getMaximalPaths();
        for (final Vector<PartialOrderNode<Concept<O, FCAElement>>> path : paths) {
            final Vector<FCAElement> attributes = new Vector<FCAElement>();
            for (final PartialOrderNode<Concept<O, FCAElement>> node : path) {
                final Concept<O, FCAElement> concept = node.getData();
                final Iterator<FCAElement> attrCont = concept
                        .getAttributeContingentIterator();
                // we just take one attribute for the dimension (if instead of
                // while)
                if (attrCont.hasNext()) {
                    final FCAElement attribute = attrCont.next();
                    attributes.add(attribute);
                }
            }
            dimensions.add(new Dimension<FCAElement>(attributes));
        }
        return dimensions;
    }
}
