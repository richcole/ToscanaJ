/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
    public AllTests(String s) {
        super(s);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(net.sourceforge.toscanaj.controller.fca.tests.AllTests.suite());
        suite.addTest(net.sourceforge.toscanaj.view.diagram.tests.AllTests.suite());
        suite.addTest(net.sourceforge.toscanaj.model.lattice.tests.AllTests.suite());

        return suite;
    }

}
