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
import org.tockit.datatype.xsd.StringValue;


public class StringValueTest extends ValueTest {
	private final static Class THIS = StringValueTest.class; 
	
	public StringValueTest(String s) {
		super(s);
	}
	
    public static Test suite() {
		return new TestSuite(THIS);
    }

    protected Value[] setUpValues() {
		return new Value[]{
				new StringValue("one"),    // 0
				new StringValue("two"),    // 1
				new StringValue("three"),  // 2
				new StringValue("one"),    // 3
				new StringValue("two")     // 4
				};
	}

	protected int[][] setUpEquivalenceSets() {
		return new int[][]{
				new int[]{0,3},
				new int[]{1,4},
				new int[]{2},
				new int[]{0,3},
				new int[]{1,4}
				};
	}
}
