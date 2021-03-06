/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.lattice;

import java.util.Collection;
import java.util.Iterator;

import net.sourceforge.toscanaj.model.order.Ordered;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

/**
 * Offers read access to information about a concept in a Formal Concept
 * Lattice.
 * 
 * This does model the intent, the extent and the two contingents (attribute and
 * object) by giving access to the size information and an iterator for each. In
 * addition a query can be made if this concept is realized, thereby allowing a
 * distinction between the view from the data model (concrete scale) and the
 * data itself (realised scale).
 * 
 * The terms used are: - intent: all attributes matched in this concept -
 * extent: all objects matching all attributes in the intent - attribute
 * contingent: all attribute matched in this concept but not in any upper
 * neighbour - object contingent: all objects matching the intent but nothing
 * else
 * 
 * The contingents are usually used for labelling the diagram.
 * 
 * @param <O>
 *            The type of objects in the extent/object contingent.
 * @param <A>
 *            The type of attributes in the intent/attribute contingent.
 */
public interface Concept<O, A> extends XMLizable, Ordered<Concept<O, A>> {
    /**
     * Returns the size of the intent.
     */
    int getIntentSize();

    /**
     * Returns the size of the extent.
     */
    int getExtentSize();

    /**
     * Returns the size of the attribute contingent.
     */
    int getAttributeContingentSize();

    /**
     * Returns the size of the object contingent.
     */
    int getObjectContingentSize();

    /**
     * Returns an iterator returning the attributes from the intent.
     * 
     * The Java objects returned from the iterator should offer a toString()
     * implementation suited for displaying the attributes.
     */
    Iterator<A> getIntentIterator();

    /**
     * Returns an iterator returning the objects from the extent.
     * 
     * The Java objects returned from the iterator should offer a toString()
     * implementation suited for displaying the objects.
     */
    Iterator<O> getExtentIterator();

    /**
     * Returns an iterator returning the attributes from the contingent.
     * 
     * The Java objects returned from the iterator should offer a toString()
     * implementation suited for displaying the attributes.
     */
    Iterator<A> getAttributeContingentIterator();

    /**
     * Returns an iterator returning the objects from the contingent.
     * 
     * The Java objects returned from the iterator should offer a toString()
     * implementation suited for displaying the objects.
     */
    Iterator<O> getObjectContingentIterator();

    /**
     * Returns true iff this is the top concept.
     */
    boolean isTop();

    /**
     * Returns true iff this is the bottom concept.
     */
    boolean isBottom();

    /**
     * Returns true iff the given concept is a superconcept of the object.
     */
    boolean hasSuperConcept(Concept<O, A> concept);

    /**
     * Returns true iff the given concept is a subconcept of the object.
     */
    boolean hasSubConcept(Concept<O, A> concept);

    Collection<Concept<O, A>> getDownset();

    Collection<Concept<O, A>> getUpset();

    boolean isMeetIrreducible();

    boolean isJoinIrreducible();

    public Concept<O, A> getTopConcept();

    public Concept<O, A> getBottomConcept();
}
