/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $ID$
 */
package org.tockit.datatype.xsd;

import net.sourceforge.toscanaj.model.order.Ordered;

import org.tockit.datatype.Value;


public class StringValue implements Value {
    private String value;

    public StringValue(String value) {
        this.value = value;
    }
    
    public String getDisplayString() {
        return this.value;
    }

    public boolean isLesserThan(Ordered other) {
        return false;
    }

    public boolean isEqual(Ordered other) {
        if(other.getClass() != StringValue.class) {
            return false;
        }
        StringValue otherValue = (StringValue) other;
        return otherValue.value == this.value;
    }
    
    public String getValue() {
        return this.value;
    }
}
