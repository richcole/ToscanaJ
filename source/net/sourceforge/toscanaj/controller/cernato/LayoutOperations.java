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
    // constants for base vector calculation
    private static final double BASE_SCALE = 30;
    private static final double BASE_X_STRETCH = 2;
    private static final double BASE_X_SHEAR = -0.1;

    public static final Vector calculateDimensions(Lattice lattice) {
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

    public static final Diagram2D createDiagram(Lattice lattice, String title) {
        SimpleLineDiagram diagram = new SimpleLineDiagram();
        diagram.setTitle(title);
        Vector dimensions = calculateDimensions(lattice);
        Vector base = createBase(dimensions);
        Concept[] concepts = lattice.getConcepts();
        Hashtable nodemap = new Hashtable();
        double[] topVector = null;
        for (int i = 0; i < concepts.length; i++) {
            Concept concept = concepts[i];
            double[] ndimVector = new double[dimensions.size()];
            Iterator attributes = concept.getIntentIterator();
            while (attributes.hasNext()) {
                Attribute attribute = (Attribute) attributes.next();
                Criterion criterion = (Criterion) attribute.getData();
                addVector(ndimVector, criterion, dimensions);
            }
            if(concept.isTop()) {
                topVector = ndimVector;
            }
            DiagramNode node = new NDimDiagramNode(String.valueOf(i), ndimVector, concept,
                                               new LabelInfo(), new LabelInfo(), null, base);
            nodemap.put(concept, node);
            diagram.addNode(node);
        }
        // the top node has to be at (0,0), otherwise base changes will affect the overall position of the diagram
        Iterator it = nodemap.values().iterator();
        while (it.hasNext()) {
            NDimDiagramNode node = (NDimDiagramNode) it.next();
            node.setNdimVector(substract(node.getNdimVector(),topVector));
        }
        createConnections(diagram, nodemap);
        return diagram;
    }

    private static double[] substract(double[] a, double[] b) {
        double[] retVal = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            retVal[i] = a[i] - b[i];
        }
        return retVal;
    }

    /**
     * Creates a set of base vectors.
     *
     * The return value is a set of base vectors as given in Frank Vogt's book "Formale Begriffsanalyse mit C++",
     * page 61, rescaled and stretched by the two constants BASE_SCALE and BASE_X_STRETCH, and sheared by BASE_X_SHEAR.
     */
    private static Vector createBase(Vector dimensions) {
        Vector base = new Vector();
        int n = dimensions.size();
        int i = 0;
        for (Iterator iterator = dimensions.iterator(); iterator.hasNext();) {
            iterator.next();
            double a = Math.pow(2,i);
            double b = Math.pow(2,n-i-1);
            base.add(new Point2D.Double( (a-b + BASE_X_SHEAR) * BASE_X_STRETCH * BASE_SCALE,
                                         (a+b) * BASE_SCALE));
            i++;
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
                if(node.getData() == criterion) {
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
