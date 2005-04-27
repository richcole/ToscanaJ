/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca.tests;



import org.tockit.context.model.Context;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.tests.ContextSetups;

public class LatticeGeneratorTest extends TestCase {
    final static Class THIS = LatticeGeneratorTest.class;

    protected Context context;

    public LatticeGeneratorTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }
    
    public void testGantersAlgorithmOnValidContext() {
        LatticeGenerator lgen = new GantersAlgorithm();

        Lattice lattice = lgen.createLattice(this.context);

        checkLatticeSizes(lattice);
    }

    protected void checkLatticeSizes(Lattice lattice) {
        assertEquals(31, lattice.getConcepts().length);
        
        assertEquals(16, lattice.getTop().getExtentSize());
        assertEquals(0, lattice.getTop().getIntentSize());
        assertEquals(0, lattice.getTop().getObjectContingentSize());
        assertEquals(0, lattice.getTop().getAttributeContingentSize());
        
        assertEquals(0, lattice.getBottom().getExtentSize());
        assertEquals(13, lattice.getBottom().getIntentSize());
        assertEquals(0, lattice.getBottom().getObjectContingentSize());
        assertEquals(0, lattice.getBottom().getAttributeContingentSize());
    }

    protected void setUp() {
        this.context = ContextSetups.createCompleteAnimalContext();
    }
}
