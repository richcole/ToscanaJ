/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.cernato.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.controller.cernato.PartialOrderOperations;
import net.sourceforge.toscanaj.model.cernato.*;
import net.sourceforge.toscanaj.model.directedgraph.DirectedGraph;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ScaleColumn;

import java.util.Set;

public class PartialOrderOperationsTest extends TestCase {
    final static Class THIS = PartialOrderOperationsTest.class;

    public PartialOrderOperationsTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testPartialOrder() {
        NumericalType numType1 = new NumericalType("numtype1");

        NumericalValueGroup numGroup1 = new NumericalValueGroup(numType1, "num1", "num1", 0, true, 3, true);
        NumericalValueGroup numGroup2 = new NumericalValueGroup(numType1, "num2", "num2", 0, true, 3, false);
        NumericalValueGroup numGroup3 = new NumericalValueGroup(numType1, "num3", "num3", 0, false, 3, false);
        NumericalValueGroup numGroup4 = new NumericalValueGroup(numType1, "num4", "num4", 1, true, 2, true);
        NumericalValueGroup numGroup5 = new NumericalValueGroup(numType1, "num5", "num5", 1, true, 2, true);
        NumericalValueGroup numGroup6 = new NumericalValueGroup(numType1, "num6", "num6", 0, false, 3, true);
        NumericalValueGroup numGroup7 = new NumericalValueGroup(numType1, "num7", "num7", 3, true, 5, true);
        NumericalValueGroup numGroup8 = new NumericalValueGroup(numType1, "num8", "num8", 3, true, 3, true);

        DirectedGraph graph = PartialOrderOperations.createGraphFromOrder(
                new ScaleColumn[]{numGroup1, numGroup2, numGroup3, numGroup4, numGroup5,
                                 numGroup6, numGroup7, numGroup8});
        Set sources;
        Set sinks;
        Set maximalPaths;

        sources = graph.getSources();
        assertEquals(2, sources.size());
        sinks = graph.getSinks();
        assertEquals(3, sinks.size());
        maximalPaths = graph.getMaximalPaths();
        assertEquals(6, maximalPaths.size());

        TextualType textType1 = new TextualType("texttype1");
        TextualType textType2 = new TextualType("texttype2");

        TextualValueGroup textGroup1 = new TextualValueGroup(textType1, "text1", "text1");
        textGroup1.addValue(new TextualValue("one"));
        textGroup1.addValue(new TextualValue("two"));
        TextualValueGroup textGroup2 = new TextualValueGroup(textType1, "text2", "text2");
        textGroup2.addValue(new TextualValue("one"));
        TextualValueGroup textGroup3 = new TextualValueGroup(textType1, "text3", "text3");
        textGroup3.addValue(new TextualValue("two"));
        TextualValueGroup textGroup4 = new TextualValueGroup(textType1, "text4", "text4");
        TextualValueGroup textGroup5 = new TextualValueGroup(textType2, "text5", "text5");
        textGroup5.addValue(new TextualValue("two"));

        graph = PartialOrderOperations.createGraphFromOrder(
                new ScaleColumn[]{textGroup1, textGroup2, textGroup3, textGroup4, textGroup5});
        sources = graph.getSources();
        assertEquals(2, sources.size());

        sinks = graph.getSinks();
        assertEquals(2, sinks.size());

        maximalPaths = graph.getMaximalPaths();
        assertEquals(3, graph.getMaximalPaths().size());
    }
}
