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

public class CernatoDimensionStrategy implements DimensionCreationStrategy<FCAElement> {
    private static class OrderedCriterion implements Ordered<OrderedCriterion> {
        private Criterion criterion;
        public OrderedCriterion(Criterion criterion) {
            this.criterion = criterion;
        }
        public Criterion getCriterion() {
            return this.criterion;
        }
        public boolean isLesserThan(OrderedCriterion other) {
            if(other.getClass() != this.getClass()) {
                return false;
            }
            OrderedCriterion ocOther = other;
            return this.getCriterion().isLesserThan(ocOther.getCriterion());
        }
        public boolean isEqual(OrderedCriterion other) {
            if(other.getClass() != this.getClass()) {
                return false;
            }
            OrderedCriterion ocOther = other;
            return this.getCriterion().isEqual(ocOther.getCriterion());
        }
    }
    
    public<O> List<Dimension<FCAElement>> calculateDimensions(Lattice<O,FCAElement> lattice) {
        List<Dimension<FCAElement>> dimensions = new ArrayList<Dimension<FCAElement>>();
        Concept<O,FCAElement> bottom = lattice.getBottom();
        Iterator<FCAElement> it = bottom.getIntentIterator();
        OrderedCriterion[] criteria = new OrderedCriterion[bottom.getIntentSize()];
        Hashtable<OrderedCriterion, FCAElement> map = new Hashtable<OrderedCriterion, FCAElement>();
        int count = 0;
        while (it.hasNext()) {
            FCAElement attribute = it.next();
            criteria[count] = new OrderedCriterion((Criterion) attribute.getData());
            map.put(criteria[count], attribute);
            count++;
        }
        DirectedGraph<PartialOrderNode<OrderedCriterion>> graph = PartialOrderOperations.createGraphFromOrder(criteria);
        Set<Vector<PartialOrderNode<OrderedCriterion>>> paths = graph.getMaximalPaths();
        for (Iterator<Vector<PartialOrderNode<OrderedCriterion>>> iterator2 = paths.iterator(); iterator2.hasNext();) {
            Vector<PartialOrderNode<OrderedCriterion>> path = iterator2.next();
            Vector<FCAElement> attributes = new Vector<FCAElement>();
            for (Iterator<PartialOrderNode<OrderedCriterion>> it2 = path.iterator(); it2.hasNext();) {
            	PartialOrderNode<OrderedCriterion> node = it2.next();
                attributes.add(map.get(node.getData()));
            }
            dimensions.add(new Dimension<FCAElement>(attributes));
        }
        return dimensions;
    }
}
