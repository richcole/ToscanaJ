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
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.Attribute;

import java.util.*;
import java.awt.geom.Point2D;

public class LayoutOperations {
    public static final Vector calculateDimensions(CernatoModel model) {
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

    public static final Diagram2D createDiagram(CernatoModel model, Lattice lattice, String title) {
        SimpleLineDiagram diagram = new SimpleLineDiagram();
        diagram.setTitle(title);
        Vector dimensions = calculateDimensions(model);
        Vector base = createBase(dimensions);
        Concept[] concepts = lattice.getConcepts();
        Hashtable nodemap = new Hashtable();
        for (int i = 0; i < concepts.length; i++) {
            Concept concept = concepts[i];
            double[] ndimVector = new double[dimensions.size()];
            Iterator attributes = concept.getIntentIterator();
            while (attributes.hasNext()) {
                Attribute attribute = (Attribute) attributes.next();
                Criterion criterion = (Criterion) attribute.getData();
                addVector(ndimVector, criterion, dimensions);
            }
            DiagramNode node = new NDimDiagramNode(String.valueOf(i), ndimVector, concept,
                                               new LabelInfo(), new LabelInfo(), null, base);
            nodemap.put(concept, node);
            diagram.addNode(node);
        }
        createConnections(diagram, nodemap);
        return diagram;
    }

    private static Vector createBase(Vector dimensions) {
        Vector base = new Vector();
        double x = 0.5;
        double size = 30;
        for (Iterator iterator = dimensions.iterator(); iterator.hasNext();) {
            iterator.next();
            base.add(new Point2D.Double(x*size, size));
            if(x>0) {
                x = -x - 1;
            } else {
                x = -x + 1;
            }
        }
        return base;
    }

    private static void addVector(double[] ndimVector, Criterion criterion, Vector dimensions) {
        int dimCount = 0;
        for (Iterator it = dimensions.iterator(); it.hasNext();) {
            net.sourceforge.toscanaj.model.cernato.Dimension dimension =
                                            (net.sourceforge.toscanaj.model.cernato.Dimension) it.next();
            Vector path = dimension.getPath();
            for (Iterator it2 = path.iterator(); it2.hasNext();) {
                PartialOrderNode node = (PartialOrderNode) it2.next();
                if(node.getValueGroup() == criterion.getValueGroup()) {
                    ndimVector[dimCount] += 1;
                }
            }
            dimCount++;
        }
    }

    private static void createConnections(SimpleLineDiagram diagram, Hashtable nodemap) {
        Iterator nodesIt = diagram.getNodes();
        while (nodesIt.hasNext()) {
            DiagramNode node = (DiagramNode) nodesIt.next();
            Concept concept = node.getConcept();
            Collection upset = new HashSet(concept.getUpset());
            upset.remove(concept);
            Set indirectSuperConcepts = new HashSet();
            for (Iterator iterator = upset.iterator(); iterator.hasNext();) {
                Concept superconcept = (Concept) iterator.next();
                Collection upset2 = new HashSet(superconcept.getUpset());
                upset2.remove(superconcept);
                indirectSuperConcepts.addAll(upset2);
            }
            upset.removeAll(indirectSuperConcepts);
            for (Iterator iterator = upset.iterator(); iterator.hasNext();) {
                Concept upperNeighbour = (Concept) iterator.next();
                diagram.addLine((DiagramNode) nodemap.get(upperNeighbour), node);
            }
        }
    }
}
