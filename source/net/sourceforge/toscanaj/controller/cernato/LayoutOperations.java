/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.cernato;

import net.sourceforge.toscanaj.model.cernato.*;
import net.sourceforge.toscanaj.model.directedgraph.DirectedGraph;

import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class LayoutOperations {
    static public Vector calculateDimensions(CernatoModel model) {
        Vector dimensions = new Vector();
        Collection properties = model.getContext().getProperties();
        for (Iterator iterator = properties.iterator(); iterator.hasNext();) {
            Property property = (Property) iterator.next();
            Type type = property.getType();
            Collection valueGroups = type.getValueGroups();
            ValueGroup[] groups = new ValueGroup[valueGroups.size()];
            valueGroups.toArray(groups);
            DirectedGraph graph = PartialOrderOperations.createGraphFromOrder(groups);
            Set paths = graph.getMaximalPaths();
            for (Iterator iterator2 = paths.iterator(); iterator2.hasNext();) {
                Vector path = (Vector) iterator2.next();
                dimensions.add(new Dimension(property, path));
            }
        }
        return dimensions;
    }
}
