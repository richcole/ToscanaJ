/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype.xsd;

import net.sourceforge.toscanaj.model.order.Ordered;

import org.tockit.datatype.Value;


public class DecimalValue implements Value {
    private double value;

    public DecimalValue(double value) {
        this.value = value;
    }
    
    public String getDisplayString() {
        return String.valueOf(this.value);
    }

    public boolean isLesserThan(Ordered other) {
        if(other.getClass() != DecimalValue.class) {
            return false;
        }
        DecimalValue otherValue = (DecimalValue) other;
        return otherValue.value < this.value;
    }

    public boolean isEqual(Ordered other) {
        if(other.getClass() != DecimalValue.class) {
            return false;
        }
        DecimalValue otherValue = (DecimalValue) other;
        return otherValue.value == this.value;
    }
    
    public double getValue() {
        return this.value;
    }
}
