/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype.xsd;

import net.sourceforge.toscanaj.model.order.Ordered;

import org.tockit.datatype.AbstractValue;


public class StringValue extends AbstractValue {
    private String value;

    public StringValue(String value) {
        this.value = value;
    }
    
    public String getDisplayString() {
        return this.value;
    }
    
    public boolean isLesserThan(Ordered other) {
        if(!(this.getClass() == other.getClass())) {
            return false;
        }
        StringValue otherValue = (StringValue) other;
        return this.value.compareTo(otherValue.value) < 0;
    }

    public boolean equals(Object other) {
        if(!(this.getClass() == other.getClass())) {
            return false;
        }
        StringValue otherValue = (StringValue) other;
        return otherValue.value.equals(this.value);
    }
    
    public int hashCode() {
        return value.hashCode();
    }

    public String getValue() {
        return this.value;
    }
}
