/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.cernato.tests;

import net.sourceforge.toscanaj.model.cernato.*;
import net.sourceforge.toscanaj.model.cernato.tests.TestData;
import net.sourceforge.toscanaj.controller.cernato.LayoutOperations;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

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
        Vector dimensions = LayoutOperations.calculateDimensions(TestData.Model);
        assertEquals(18, dimensions.size());
    }
}
