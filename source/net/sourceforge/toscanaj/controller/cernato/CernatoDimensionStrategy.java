/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.cernato;

import net.sourceforge.toscanaj.model.cernato.Criterion;
import net.sourceforge.toscanaj.model.directedgraph.DirectedGraph;
import net.sourceforge.toscanaj.model.lattice.Attribute;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.ndimdiagram.Dimension;
import net.sourceforge.toscanaj.model.order.PartialOrderNode;
import net.sourceforge.toscanaj.controller.ndimlayout.DimensionCreationStrategy;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.Hashtable;

public class CernatoDimensionStrategy implements DimensionCreationStrategy {
    public Vector calculateDimensions(Lattice lattice) {
        Vector dimensions = new Vector();
        Concept bottom = lattice.getBottom();
        Iterator it = bottom.getIntentIterator();
        Criterion[] criteria = new Criterion[bottom.getIntentSize()];
        Hashtable map = new Hashtable();
        int count = 0;
        while (it.hasNext()) {
            Attribute attribute = (Attribute) it.next();
            criteria[count] = (Criterion) attribute.getData();
            map.put(attribute.getData(), attribute);
            count++;
        }
        DirectedGraph graph = PartialOrderOperations.createGraphFromOrder(criteria);
        Set paths = graph.getMaximalPaths();
        for (Iterator iterator2 = paths.iterator(); iterator2.hasNext();) {
            Vector path = (Vector) iterator2.next();
            Vector attributes = new Vector();
            for (Iterator it2 = path.iterator(); it2.hasNext();) {
                PartialOrderNode node = (PartialOrderNode) it2.next();
                attributes.add(map.get(node.getData()));
            }
            dimensions.add(new Dimension(attributes));
        }
        return dimensions;
    }
}
