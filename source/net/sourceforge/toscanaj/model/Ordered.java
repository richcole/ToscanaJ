/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model;

/**
 * Implements a partial order.
 *
 * This differs from java.lang.Comparable, which assumes a total order.
 */
public interface Ordered {
    boolean isLesserThan(Ordered other);
    boolean isEqual(Ordered other);
}
