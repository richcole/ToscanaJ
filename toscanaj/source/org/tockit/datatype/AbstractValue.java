/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype;

import net.sourceforge.toscanaj.model.order.Ordered;


public abstract class AbstractValue implements Value {
    /**
     * We default to be a co-chain.
     */
    public boolean isLesserThan(Ordered other) {
        return false;
    }

    /**
     * Ordered.isEqual() should match equals() since we assume value-identity.
     */
    public boolean isEqual(Ordered other) {
        return equals(other);
    }
    
    /**
     * Implements the requirement of Value that toString() matched getDisplayString().
     */
    public String toString() {
        return getDisplayString();
    }

    /**
     * This implementation just checks the basics (not null, same type) and
     * then delegates to the abstract sameTypeEquals(Object).
     */
    public final boolean equals(Object other) {
    	if(other == null) {
    		return false;
    	}
        if(!(this.getClass() == other.getClass())) {
            return false;
        }
    	return sameTypeEquals(other);
    }

    protected abstract boolean sameTypeEquals(Object other);

	// force overriding hashCode()
    public abstract int hashCode();
}
