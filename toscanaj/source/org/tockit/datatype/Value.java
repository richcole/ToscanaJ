/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.datatype;

import net.sourceforge.toscanaj.model.order.Ordered;

/**
 * Values for type-based systems.
 * 
 * Any Value should be immutable and should have value identity (i.e. override
 * equals(Object) and hashcode() accordingly). The return value
 * of toString should be the same as getDisplayString(). Two values are considered
 * incomparable (i.e. !(a=b), !(a<b) !(a>b)) if the their classes do not match.
 */
public interface Value extends Ordered<Value> {
    String getDisplayString();
    
    static final Value NULL = new Value(){
        public String getDisplayString() {
            return null;
        }
        public boolean isLesserThan(Value other) {
            return false;
        }
        public boolean isEqual(Value other) {
            return other == NULL;
        }
    };
}
