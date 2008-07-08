/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.db.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTestControllerDB extends TestCase {
    public AllTestControllerDB(final String s) {
        super(s);
    }

    public static Test suite() {
        final TestSuite suite = new TestSuite();
        suite.addTest(DBConnectionEmbeddTest.suite());
        suite.addTest(DumpSqlScriptTest.suite());
        return suite;
    }

}
