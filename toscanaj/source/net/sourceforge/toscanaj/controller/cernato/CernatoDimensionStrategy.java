/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.cernato;

import net.sourceforge.toscanaj.controller.ndimlayout.DimensionCreationStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.PartialOrderOperations;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.directedgraph.DirectedGraph;
import net.sourceforge.toscanaj.model.directedgraph.Node;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.ndimdiagram.Dimension;
import net.sourceforge.toscanaj.model.order.Ordered;
import net.sourceforge.toscanaj.model.order.PartialOrderNode;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.tockit.cernatoXML.model.Criterion;

public class CernatoDimensionStrategy implements DimensionCreationStrategy {
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
    public Vector<Dimension> calculateDimensions(Lattice lattice) {
        Vector<Dimension> dimensions = new Vector<Dimension>();
        Concept bottom = lattice.getBottom();
        Iterator it = bottom.getIntentIterator();
        OrderedCriterion[] criteria = new OrderedCriterion[bottom.getIntentSize()];
        Hashtable<OrderedCriterion, FCAElement> map = new Hashtable<OrderedCriterion, FCAElement>();
        int count = 0;
        while (it.hasNext()) {
            FCAElement attribute = (FCAElement) it.next();
            criteria[count] = new OrderedCriterion((Criterion) attribute.getData());
            map.put(criteria[count], attribute);
            count++;
        }
        DirectedGraph graph = PartialOrderOperations.createGraphFromOrder(criteria);
        Set<Vector<Node>> paths = graph.getMaximalPaths();
        for (Iterator<Vector<Node>> iterator2 = paths.iterator(); iterator2.hasNext();) {
            Vector path = iterator2.next();
            Vector<FCAElement> attributes = new Vector<FCAElement>();
            for (Iterator it2 = path.iterator(); it2.hasNext();) {
                PartialOrderNode node = (PartialOrderNode) it2.next();
                attributes.add(map.get(node.getData()));
            }
            dimensions.add(new Dimension(attributes));
        }
        return dimensions;
    }
}
