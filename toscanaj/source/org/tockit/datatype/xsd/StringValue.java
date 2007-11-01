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


public class StringValue extends AbstractValue {
    private String value;

    public StringValue(String value) {
        this.value = value;
    }
    
    public String getDisplayString() {
        return this.value;
    }
    
    @Override
	public boolean isLesserThan(Value other) {
        if(!(this.getClass() == other.getClass())) {
            return false;
        }
        StringValue otherValue = (StringValue) other;
        return this.value.compareTo(otherValue.value) < 0;
    }

    @Override
	public boolean sameTypeEquals(Object other) {
        StringValue otherValue = (StringValue) other;
        return otherValue.value.equals(this.value);
    }
    
    @Override
	public int hashCode() {
        return value.hashCode();
    }

    public String getValue() {
        return this.value;
    }
}
