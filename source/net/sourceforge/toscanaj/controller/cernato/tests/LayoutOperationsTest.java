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
import net.sourceforge.toscanaj.controller.cernato.CernatoDimensionStrategy;
import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DimensionCreationStrategy;
import net.sourceforge.toscanaj.model.cernato.tests.TestData;
import net.sourceforge.toscanaj.model.context.Context;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.ViewContext;

import java.util.Vector;

public class LayoutOperationsTest extends TestCase {
    final static Class THIS = LayoutOperationsTest.class;

    public LayoutOperationsTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testDimensionCalculation() {
        Vector dimensions;
        DimensionCreationStrategy cernatoDimensionStrategy = new CernatoDimensionStrategy();
        Lattice lattice;
        LatticeGenerator latticeGenerator = new GantersAlgorithm();
        Context context;

        context = new ViewContext(TestData.Model.getContext(), TestData.View1);
        lattice = latticeGenerator.createLattice(context);
        dimensions = cernatoDimensionStrategy.calculateDimensions(lattice);
        assertEquals(6, dimensions.size());

        context = new ViewContext(TestData.Model.getContext(), TestData.View2);
        lattice = latticeGenerator.createLattice(context);
        dimensions = cernatoDimensionStrategy.calculateDimensions(lattice);
        assertEquals(2, dimensions.size());

        context = new ViewContext(TestData.Model.getContext(), TestData.View3);
        lattice = latticeGenerator.createLattice(context);
        dimensions = cernatoDimensionStrategy.calculateDimensions(lattice);
        assertEquals(6, dimensions.size());
    }
}
