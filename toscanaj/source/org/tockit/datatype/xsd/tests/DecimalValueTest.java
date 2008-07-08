/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype.xsd.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.tockit.datatype.Value;
import org.tockit.datatype.tests.ValueTest;
import org.tockit.datatype.xsd.DecimalValue;

public class DecimalValueTest extends ValueTest {
    private final static Class THIS = DecimalValueTest.class;

    public DecimalValueTest(final String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    @Override
    protected Value[] setUpValues() {
        return new Value[] { new DecimalValue(1), // 0
                new DecimalValue(2), // 1
                new DecimalValue(3), // 2
                new DecimalValue(1), // 3
                new DecimalValue(2) // 4
        };
    }

    @Override
    protected int[][] setUpEquivalenceSets() {
        return new int[][] { new int[] { 0, 3 }, new int[] { 1, 4 },
                new int[] { 2 }, new int[] { 0, 3 }, new int[] { 1, 4 } };
    }
}
