package net.sourceforge.toscanaj.model.lattice;

import java.util.Iterator;

/**
 * Offers read access to information about a concept in a Formal Concept
 * Lattice.
 *
 * This does model the intent, the extent and the two contingents (attribute and
 * object) by giving access to the size information and an iterator for each. In
 * addition a query can be made if this concept is realized, thereby allowing
 * a distinction between the view from the data model (concrete scale) and the
 * data itself (realised scale).
 *
 * The terms used are:
 * - intent: all attributes matched in this concept
 * - extent: all objects matching all attributes in the intent
 * - attribute contingent: all attribute matched in this concept but not in any
 *                         upper neighbour
 * - object contingent:    all objects matching the intent but nothing else
 *
 * The contingents are usually used for labelling the diagram.
 */
public interface Concept {
    /**
     * Returns true if the concept is realised.
     *
     * Realised means it is distinguished from all other concepts by the data,
     * if there is another concept with the same extent but larger intent this
     * concept is only theoretically existent, if a lattice would be created
     * from the data it would not be in it.
     */
    boolean isRealised();

    /**
     * Returns the size of the intent.
     */
    int getIntentSize();

    /**
     * Returns the size of the intent relative to the number of attributes in
     * the diagram.
     */
    double getIntentSizeRelative();

    /**
     * Returns the size of the extent.
     */
    int getExtentSize();

    /**
     * Returns the size of the extent relative to the number of objects in the
     * diagram.
     */
    double getExtentSizeRelative();

    /**
     * Returns the size of the attribute contingent.
     */
    int getAttributeContingentSize();

    /**
     * Returns the size of the attribute contingent relative to all attributes
     * in the diagram.
     *
     * This will always be between 0 and 1.
     */
    double getAttributeContingentSizeRelative();

    /**
     * Returns the size of the object contingent.
     */
    int getObjectContingentSize();

    /**
     * Returns the size of the object contingent relative to all objects in
     * the current diagram.
     *
     * This will always be between 0 and 1.
     */
    double getObjectContingentSizeRelative();

    /**
     * Returns an iterator returning the attributes from the intent.
     *
     * The Java objects returned from the iterator should offer a toString()
     * implementation suited for displaying the attributes.
     */
    Iterator getIntentIterator();

    /**
     * Returns an iterator returning the objects from the extent.
     *
     * The Java objects returned from the iterator should offer a toString()
     * implementation suited for displaying the objects.
     */
    Iterator getExtentIterator();

    /**
     * Returns an iterator returning the attributes from the contingent.
     *
     * The Java objects returned from the iterator should offer a toString()
     * implementation suited for displaying the attributes.
     */
    Iterator getAttributeContingentIterator();

    /**
     * Returns an iterator returning the objects from the contingent.
     *
     * The Java objects returned from the iterator should offer a toString()
     * implementation suited for displaying the objects.
     */
    Iterator getObjectContingentIterator();

    /**
     * Returns the concept that is using only objects from the other concepts
     * extent.
     *
     * This is at the moment not necessarily a concept in the sense of FCA: the
     * concept returned might be not realised in the diagram we currently have
     * (not realised as in Toscana 2/3).
     *
     * If the other concept is null, a copy of the original concept will be returned.
     */
    Concept filterByExtent(Concept other);

    /**
     * Returns the concept that is using only objects from the other concepts
     * object contingent.
     *
     * This is at the moment not necessarily a concept in the sense of FCA: the
     * concept returned might be not realised in the diagram we currently have
     * (not realised as in Toscana 2/3).
     *
     * If the other concept is null, a copy of the original concept will be returned.
     */
    Concept filterByContingent(Concept other);

    /**
     * Returns true if this is the top concept.
     */
    boolean isTop();

    /**
     * Returns true if this is the bottom concept.
     */
    boolean isBottom();
}