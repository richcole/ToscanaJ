/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype.xsd;

import org.tockit.datatype.AbstractValue;
import org.tockit.datatype.Value;


public class DecimalValue extends AbstractValue {
    private double value;

    public DecimalValue(double value) {
        this.value = value;
    }
    
    public String getDisplayString() {
        return String.valueOf(this.value);
    }

    @Override
	public boolean isLesserThan(Value other) {
        if(other.getClass() != DecimalValue.class) {
            return false;
        }
        DecimalValue otherValue = (DecimalValue) other;
        return otherValue.value > this.value;
    }

    public double getValue() {
        return this.value;
    }
    
    @Override
	public boolean sameTypeEquals(Object other) {
        DecimalValue otherValue = (DecimalValue) other;
        return otherValue.value == this.value;
    }
    
    @Override
	public int hashCode() {
        // create hashCode a la java.lang.Double
        long bits = Double.doubleToLongBits(value);
        return (int)(bits ^ (bits >>> 32));
    }
}
