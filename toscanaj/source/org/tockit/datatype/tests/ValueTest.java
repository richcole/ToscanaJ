/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype.tests;

import java.util.Arrays;

import org.tockit.datatype.Value;

import junit.framework.TestCase;


public abstract class ValueTest extends TestCase {
	public ValueTest(String s) {
		super(s);
	}
	
    public void testValueComparisons() {
    	Value[] values = setUpValues();
    	int[][] equivalenceSets = setUpEquivalenceSets();
    	if(values.length != equivalenceSets.length) {
    		throw new RuntimeException("Test badly configured: number of equivalence sets does not match number of values");
    	}
    	for (int i = 0; i < values.length; i++) {
			Value value1 = values[i];
			// check reflexivity
			assertEquals("Value is not equal to itself: " + value1,
					     true, value1.equals(value1));
			// check non-equality to null (also it breaks reflexivity, but the spec says it this way)
			assertEquals("Value is equal to null: " + value1, 
					     false, value1.equals(null));
			Arrays.sort(equivalenceSets[i]);
			for (int j = i+1; j < values.length; j++) {
				Value value2 = values[j];
				// check expected result
				boolean expectedValue = Arrays.binarySearch(equivalenceSets[i], j) >= 0;
				assertEquals("Equality not as expected for values " + value1 + " and " + value2,
						     expectedValue, value1.equals(value2));
				// check symnetry
				assertEquals("Symnetry broken between " + value1 + " and " + value2,
						     value1.equals(value2), value2.equals(value1));
				if(value1.equals(value2)) {
					// we test all values for transitivity, which might add duplicates,
					// but that tests consistency at least a bit
					for (int k = 0; k < values.length; k++) {
						Value value3 = values[k];
						assertEquals("Transitivity broken between " + value1 + " / " + value2 + " and " + value3,
							     value1.equals(value3), value2.equals(value3));
					}
				}
			}
		}
    }

	protected abstract Value[] setUpValues();

	protected abstract int[][] setUpEquivalenceSets();
}
