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

    // force overriding these
    public abstract boolean equals(Object other);
    public abstract int hashCode();
}
