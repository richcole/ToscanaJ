/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.tests;

import junit.framework.*;

public class AllTests extends TestCase {
    public AllTests(String s) {
        super(s);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(net.sourceforge.toscanaj.controller.fca.tests.AllTests.suite());
        suite.addTest(net.sourceforge.toscanaj.events.tests.AllTests.suite());
        suite.addTest(net.sourceforge.toscanaj.model.tests.AllTests.suite());
        suite.addTest(net.sourceforge.toscanaj.util.tests.AllTests.suite());
        suite.addTest(net.sourceforge.toscanaj.view.tests.AllTests.suite());

        return suite;
    }

}
