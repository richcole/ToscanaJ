/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.order;

/**
 * Implements a partial order.
 *
 * This differs from java.lang.Comparable, which assumes a total order. It also
 * does not implement the mathematical definition in a single method, which would
 * be comparing on a lesser-or-equal, instead of having these separated.
 */
public interface Ordered {
    /**
     * Compares the current with another object on ordering.
     * 
     * @param other the object to compare to, must not be null
     * @return true iff the current object is considered less than the one passed
     * @throws NullPointerException iff the parameter is null
     */
    boolean isLesserThan(Ordered other);

    /**
     * Compare the current with another object on equality.
     * 
     * This can be useful if a different notion of equality should be used than
     * the one implemented by Object.equals(Object). Otherwise it should be just
     * mapped to the latter (which in turn might map to reference identity).
     * 
     * Note that this method does not accept null values. This also means when
     * overriding it by mapping it to the standard equals method, a check for
     * null should be done first.
     * 
     * @param other the object to compare to, must not be null
     * @return true iff the current object is considered equal to the one passed
     * @throws NullPointerException iff the parameter is null
     */
    boolean isEqual(Ordered other);
}
