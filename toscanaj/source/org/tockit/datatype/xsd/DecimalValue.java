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
    private final double value;

    public DecimalValue(final double value) {
        this.value = value;
    }

    public String getDisplayString() {
        return String.valueOf(this.value);
    }

    @Override
    public boolean isLesserThan(final Value other) {
        if (other.getClass() != DecimalValue.class) {
            return false;
        }
        final DecimalValue otherValue = (DecimalValue) other;
        return otherValue.value > this.value;
    }

    public double getValue() {
        return this.value;
    }

    @Override
    public boolean sameTypeEquals(final Object other) {
        final DecimalValue otherValue = (DecimalValue) other;
        return otherValue.value == this.value;
    }

    @Override
    public int hashCode() {
        // create hashCode a la java.lang.Double
        final long bits = Double.doubleToLongBits(value);
        return (int) (bits ^ (bits >>> 32));
    }
}
