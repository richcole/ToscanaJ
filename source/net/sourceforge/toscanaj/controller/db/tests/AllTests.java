/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.db.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.events.tests.EventBrokerTest;

public class AllTests extends TestCase {
    public AllTests(String s) {
        super(s);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(DBConnectionEmbeddTest.suite());
        return suite;
    }

}
