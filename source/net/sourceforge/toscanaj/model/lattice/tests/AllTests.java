/*
 * Date: 13.04.2002
 * Time: 22:06:38
 */
package net.sourceforge.toscanaj.model.lattice.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
    public AllTests(String s) {
        super(s);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(MemoryMappedConceptTest.suite());
        suite.addTest(DatabaseConnectedConceptTest.suite());
        return suite;
    }

}
