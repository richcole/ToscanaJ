/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.tests;

import junit.framework.*;

public class AllTests extends TestCase {
    public AllTests(String s) {
        super(s);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(TableTest.suite());

        suite.addTest(net.sourceforge.toscanaj.model.cernato.tests.AllTests.suite());
        suite.addTest(net.sourceforge.toscanaj.model.diagram.tests.AllTests.suite());
        suite.addTest(net.sourceforge.toscanaj.model.directedgraph.tests.AllTests.suite());
        suite.addTest(net.sourceforge.toscanaj.model.lattice.tests.AllTests.suite());
        return suite;
    }

}
