/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.cernato;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import net.sourceforge.toscanaj.controller.ndimlayout.DimensionCreationStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.PartialOrderOperations;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.directedgraph.DirectedGraph;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.ndimdiagram.Dimension;
import net.sourceforge.toscanaj.model.order.Ordered;
import net.sourceforge.toscanaj.model.order.PartialOrderNode;

import org.tockit.cernatoXML.model.Criterion;

public class CernatoDimensionStrategy implements
        DimensionCreationStrategy<FCAElement> {
    private static class OrderedCriterion implements Ordered<OrderedCriterion> {
        private final Criterion criterion;

        public OrderedCriterion(final Criterion criterion) {
            this.criterion = criterion;
        }

        public Criterion getCriterion() {
            return this.criterion;
        }

        public boolean isLesserThan(final OrderedCriterion other) {
            if (other.getClass() != this.getClass()) {
                return false;
            }
            return this.getCriterion().isLesserThan(other.getCriterion());
        }

        public boolean isEqual(final OrderedCriterion other) {
            if (other.getClass() != this.getClass()) {
                return false;
            }
            return this.getCriterion().isEqual(other.getCriterion());
        }
    }

    public <O> List<Dimension<FCAElement>> calculateDimensions(
            final Lattice<O, FCAElement> lattice) {
        final List<Dimension<FCAElement>> dimensions = new ArrayList<Dimension<FCAElement>>();
        final Concept<O, FCAElement> bottom = lattice.getBottom();
        final Iterator<FCAElement> it = bottom.getIntentIterator();
        final OrderedCriterion[] criteria = new OrderedCriterion[bottom
                .getIntentSize()];
        final Hashtable<OrderedCriterion, FCAElement> map = new Hashtable<OrderedCriterion, FCAElement>();
        int count = 0;
        while (it.hasNext()) {
            final FCAElement attribute = it.next();
            criteria[count] = new OrderedCriterion((Criterion) attribute
                    .getData());
            map.put(criteria[count], attribute);
            count++;
        }
        final DirectedGraph<PartialOrderNode<OrderedCriterion>> graph = PartialOrderOperations
                .createGraphFromOrder(criteria);
        final Set<Vector<PartialOrderNode<OrderedCriterion>>> paths = graph
                .getMaximalPaths();
        for (final Vector<PartialOrderNode<OrderedCriterion>> path : paths) {
            final Vector<FCAElement> attributes = new Vector<FCAElement>();
            for (final PartialOrderNode<OrderedCriterion> node : path) {
                attributes.add(map.get(node.getData()));
            }
            dimensions.add(new Dimension<FCAElement>(attributes));
        }
        return dimensions;
    }
}
