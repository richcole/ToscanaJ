/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.cernato;

import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue;
import net.sourceforge.toscanaj.model.order.Ordered;

public class NumericalValue implements AttributeValue {
    private double value;

    public NumericalValue(double value) {
        this.value = value;
    }

    public String getDisplayString() {
        return String.valueOf(value);
    }

    public double getValue() {
        return value;
    }
    
    public String toString() {
    	return getDisplayString();
    }
    
    public boolean equals(Object other) {
        // copied from Double.equals()
        return (other instanceof NumericalValue)
               && (Double.doubleToLongBits(((NumericalValue)other).value) ==
                  Double.doubleToLongBits(this.value));
    }
    
    public int hashCode() {
    	// copied from Double.hashCode()
        long bits = Double.doubleToLongBits(value);
        return (int)(bits ^ (bits >>> 32));
    }
    
    public boolean isLesserThan(Ordered other) {
    	if(!(other instanceof NumericalValue)) {
    		return false;
    	}
    	NumericalValue otherNV = (NumericalValue) other;
        return this.value < otherNV.value;
    }
    
    public boolean isEqual(Ordered other) {
        return this.equals(other);
    }
}
