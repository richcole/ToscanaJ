package net.sourceforge.toscanaj.model.lattice;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An implementation of the Concept interface which holds all data in memory.
 *
 * This is based on AbstractConceptImplementation to reduce implementation
 * effort and to increase reuse.
 */
public class MemoryMappedConcept
    extends AbstractConceptImplementation
    implements Concept
{
    /**
     * Stores the information on the attribute contingent.
     */
    List attributeContingent = new LinkedList();

    /**
     * Stores the information on the object contingent.
     */
    List objectContingent = new LinkedList();

    /**
     * Creates a concept without contingents.
     */
    public MemoryMappedConcept() {
        super();
    }

    /**
     * Implements Concept.getAttributeContingentSize().
     */
    public int getAttributeContingentSize() {
        return attributeContingent.size();
    }

    /**
     * Implements Concept.getObjectContingentSize().
     */
    public int getObjectContingentSize() {
        return objectContingent.size();
    }

    /**
     * Implements Concept.getAttributeContingentIterator().
     */
    public Iterator getAttributeContingentIterator() {
        return attributeContingent.iterator();
    }

    /**
     * Implements Concept.getObjectContingentIterator().
     */
    public Iterator getObjectContingentIterator() {
        return objectContingent.iterator();
    }

    /**
     * Adds an attribute to the contingent.
     */
    public void addAttribute(Object attribute) {
        this.attributeContingent.add(attribute);
    }

    /**
     * Adds an object to the contingent.
     */
    public void addObject(Object object) {
        this.objectContingent.add(object);
    }

    /**
     * Will some day implement Concept.directProduct(Concept), returns >this< at
     * the moment.
     */
    public Concept directProduct( Concept other ) {
        return this;
    }
}