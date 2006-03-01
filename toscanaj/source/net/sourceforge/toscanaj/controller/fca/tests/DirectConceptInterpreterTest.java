/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca.tests;

import java.util.Iterator;

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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DirectConceptInterpreterTest extends TestCase {
    private static final DefaultDimensionStrategy DIMENSION_STRATEGY = new DefaultDimensionStrategy();
    final static Class THIS = DirectConceptInterpreterTest.class;

    public DirectConceptInterpreterTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testNesting() {
        Context context1 = ContextSetups.createAnimalSizeContext();
        Context context2 = ContextSetups.createAnimalMovementContext();
        
        LatticeGenerator lgen = new GantersAlgorithm();
        Lattice lattice1 = lgen.createLattice(context1);
        Lattice lattice2 = lgen.createLattice(context2);
        
        Diagram2D outerDiagram = NDimLayoutOperations.createDiagram(lattice1, context1.getName(), DIMENSION_STRATEGY);
        Diagram2D innerDiagram = NDimLayoutOperations.createDiagram(lattice2, context2.getName(), DIMENSION_STRATEGY);
        
        NestedLineDiagram nestedDiagram = new NestedLineDiagram(outerDiagram, innerDiagram);

        DiagramHistory diagramHistory = new DiagramHistory();
        diagramHistory.addDiagram(outerDiagram);
        diagramHistory.addDiagram(innerDiagram);
        diagramHistory.setNestingLevel(1);
        
        NestedDiagramNode someOuterNode = (NestedDiagramNode) nestedDiagram.getNode(0);
        DiagramNode someInnerNode = someOuterNode.getInnerDiagram().getNode(0);
        
        ConceptInterpretationContext interpretationContext = new ConceptInterpretationContext(diagramHistory, new EventBroker());
        Concept topConcept = interpretationContext.createNestedContext(someOuterNode.getConcept()).getOutermostTopConcept(someInnerNode.getConcept());
        
        ConceptInterpreter interpreter = new DirectConceptInterpreter();

        int count = 0;
        for (Iterator iter = interpreter.getIntentIterator(topConcept, interpretationContext); iter.hasNext();) {
            iter.next();
            count++;
        }
        assertEquals(0, count);
        
        assertEquals(16, interpreter.getExtentSize(topConcept, interpretationContext));

        count = 0;
        int[] contingentSizeBuckets = new int[] {0,0,0};
        for (Iterator iter = nestedDiagram.getNodes(); iter.hasNext();) {
            DiagramNode node = (DiagramNode) iter.next();
            count++;
            int contSize = interpreter.getObjectContingentSize(node.getConcept(), interpretationContext.createNestedContext(node.getOuterNode().getConcept()));
            contingentSizeBuckets[contSize]++;
        }
        /// @todo this actually tests the nested line diagram, not the interpreter
        /// we should probably have a separate test case for that
        assertEquals(40, count);
        
        assertEquals(29, contingentSizeBuckets[0]);
        assertEquals(6, contingentSizeBuckets[1]);
        assertEquals(5, contingentSizeBuckets[2]);
        
        // @todo test some more, e.g. the object concept for "tiger" is the same as the one for "lion", "fox" takes
        // you to the same as "wolf", similarly "owl"/"hawk", "duck"/"goose", "horse"/"zebra"
    }
}