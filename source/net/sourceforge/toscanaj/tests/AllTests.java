/*
 * Date: 13.04.2002
 * Time: 22:06:38
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
