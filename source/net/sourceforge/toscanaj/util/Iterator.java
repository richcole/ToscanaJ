/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
/*
 * Package Summary:
 *   Author: Richard Cole
 *   Descr:
 *     The purpose of this package is to construct an abstraction
 *     of sets based on sequences. The assumption is that all sets
 *     are ordered in some way and object comparison via comparable may
 *     be used.
 *
 *     iterator(set):
 *       member(x)
 *       reset()
 *       atEnd()
 *       next()
 *       nextGTE(x)
 *       val()
 *       count()
 *
 *     Example code:
 *
 *       {{{
 *         Iterator it = new STD_Iterator(set);
 *
 *         for(it.reset(); !it.at_end(); it->next()) {
 *           String x = (String) it.val();
 *         }
 *
 *         Iterator it = new Std_Iterator(dom.path("context.attribute"));
 *         sequence.insert(it);
 *       }}}
 *
 *    Status:
 *      Descr:
 *        Currently an iterator may be constructed from either a set
 *        or a collection. As needs arrise the idea is to construct
 *        iterators that perform set intersection, set union and set-minus.
 *
 *        Example:
 *          {{{
 *            class Relation {
 *              public Iterator image_intersection(Iterator A) {
 *                IntersectionIterator retValue = new IntersectionIterator;
 *                for(A.reset(); !A.atEnd(); A.next()) {
 *                  retValue.insert(R.image(A.val()));
 *                }
 *                return retValue;
 *              }
 *            }
 *          }}}
 *
 */

package net.sourceforge.toscanaj.util;


/**
 * An iterator ranges over a sequence of values.
 */
public interface Iterator
        extends Cloneable {
    /**
     * returns true if the set ranged over by this iterator contains
     * the value x.
     */
    public boolean isMember(Object x);

    /**
     * Set the iterator to refer to the first element in the sequence
     * ranged over by this iterator.
     */
    public void reset();

    /**
     * Set the iterator to refer to the next element in the sequence
     * ranged over by this iterator.
     */
    public void next();

    /**
     * Return the value refered to by this iterator. If the iterator
     * refers to the end of the list then throw an exception.
     */
    public Object val();

    /**
     * returns true if the set ranged over by this iterator contains
     * the value x.
     */
    public boolean atEnd();

    /**
     * Return the number of objects in the sequence refered to by
     * this iterator.
     */
    public int count() throws Exception;

    /**
     * Make a copy of the iterator. The copy will refer to the same element
     * as does the iterator element.
     */
    public Object clone();

    /**
     * Advance until an element greater than or equal to o is found.
     */
    public void nextGTE(Comparable o);
}


