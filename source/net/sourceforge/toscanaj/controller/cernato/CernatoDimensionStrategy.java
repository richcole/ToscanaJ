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
import net.sourceforge.toscanaj.controller.ndimlayout.DimensionCreationStrategy;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class CernatoDimensionStrategy implements DimensionCreationStrategy {
    public Vector calculateDimensions(Lattice lattice) {
        Vector dimensions = new Vector();
        Concept bottom = lattice.getBottom();
        Iterator it = bottom.getIntentIterator();
        Criterion[] criteria = new Criterion[bottom.getIntentSize()];
        int count = 0;
        while (it.hasNext()) {
            Attribute attribute = (Attribute) it.next();
            criteria[count] = (Criterion) attribute.getData();
            count++;
        }
        DirectedGraph graph = PartialOrderOperations.createGraphFromOrder(criteria);
        Set paths = graph.getMaximalPaths();
        for (Iterator iterator2 = paths.iterator(); iterator2.hasNext();) {
            Vector path = (Vector) iterator2.next();
            dimensions.add(new Dimension(path));
        }
        return dimensions;
    }
}
