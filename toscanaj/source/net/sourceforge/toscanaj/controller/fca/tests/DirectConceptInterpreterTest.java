/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca.tests;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.DirectConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.NestedDiagramNode;
import net.sourceforge.toscanaj.model.diagram.NestedLineDiagram;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.tests.ContextSetups;

import org.tockit.context.model.Context;
import org.tockit.events.EventBroker;

public class DirectConceptInterpreterTest extends TestCase {
    private static final DefaultDimensionStrategy DIMENSION_STRATEGY = new DefaultDimensionStrategy();
    final static Class THIS = DirectConceptInterpreterTest.class;

    public DirectConceptInterpreterTest(final String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testNesting() {
        final Context context1 = ContextSetups.createAnimalSizeContext();
        final Context context2 = ContextSetups.createAnimalMovementContext();

        final LatticeGenerator lgen = new GantersAlgorithm();
        final Lattice lattice1 = lgen.createLattice(context1);
        final Lattice lattice2 = lgen.createLattice(context2);

        final Diagram2D outerDiagram = NDimLayoutOperations.createDiagram(
                lattice1, context1.getName(), DIMENSION_STRATEGY);
        final Diagram2D innerDiagram = NDimLayoutOperations.createDiagram(
                lattice2, context2.getName(), DIMENSION_STRATEGY);

        final NestedLineDiagram nestedDiagram = new NestedLineDiagram(
                outerDiagram, innerDiagram);

        final DiagramHistory diagramHistory = new DiagramHistory();
        diagramHistory.addDiagram(outerDiagram);
        diagramHistory.addDiagram(innerDiagram);
        diagramHistory.setNestingLevel(1);

        final NestedDiagramNode someOuterNode = (NestedDiagramNode) nestedDiagram
                .getNode(0);
        final DiagramNode someInnerNode = someOuterNode.getInnerDiagram()
                .getNode(0);

        final ConceptInterpretationContext interpretationContext = new ConceptInterpretationContext(
                diagramHistory, new EventBroker());
        final Concept topConcept = interpretationContext.createNestedContext(
                someOuterNode.getConcept()).getOutermostTopConcept(
                someInnerNode.getConcept());

        final ConceptInterpreter interpreter = new DirectConceptInterpreter();

        int count = 0;
        for (final Iterator iter = interpreter.getIntentIterator(topConcept,
                interpretationContext); iter.hasNext();) {
            iter.next();
            count++;
        }
        assertEquals(0, count);

        assertEquals(16, interpreter.getExtentSize(topConcept,
                interpretationContext));

        count = 0;
        final int[] contingentSizeBuckets = new int[] { 0, 0, 0 };
        for (final Iterator<DiagramNode> iter = nestedDiagram.getNodes(); iter
                .hasNext();) {
            final DiagramNode node = iter.next();
            count++;
            final int contSize = interpreter.getObjectContingentSize(node
                    .getConcept(), interpretationContext
                    .createNestedContext(node.getOuterNode().getConcept()));
            contingentSizeBuckets[contSize]++;
        }
        // / @todo this actually tests the nested line diagram, not the
        // interpreter
        // / we should probably have a separate test case for that
        assertEquals(40, count);

        assertEquals(29, contingentSizeBuckets[0]);
        assertEquals(6, contingentSizeBuckets[1]);
        assertEquals(5, contingentSizeBuckets[2]);

        // @todo test some more, e.g. the object concept for "tiger" is the same
        // as the one for "lion", "fox" takes
        // you to the same as "wolf", similarly "owl"/"hawk", "duck"/"goose",
        // "horse"/"zebra"
    }
}