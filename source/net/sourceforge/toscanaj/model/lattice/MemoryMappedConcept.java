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
public class MemoryMappedConcept extends AbstractConceptImplementation {
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
     * Implements Concept.filterByExtent(Concept).
     */
    public Concept filterByExtent(Concept other) {
        if(other == null) {
            return this;
        }
        MemoryMappedConcept retVal = new MemoryMappedConcept();
        Iterator it = other.getExtentIterator();
        while(it.hasNext()) {
            Object cur = it.next();
            Iterator it2 = this.objectContingent.iterator();
            while(it2.hasNext()) {
                Object cur2 = it2.next();
                if(cur.equals(cur2)) {
                    retVal.addObject(cur);
                }
            }
        }
        it = this.attributeContingent.iterator();
        while(it.hasNext()) {
            Object cur = it.next();
            retVal.addAttribute(cur);
        }
        return retVal;
    }

    /**
     * Implements Concept.filterByContingent(Concept).
     */
    public Concept filterByContingent(Concept other) {
        if(other == null) {
            return this;
        }
        MemoryMappedConcept retVal = new MemoryMappedConcept();
        Iterator it = other.getObjectContingentIterator();
        while(it.hasNext()) {
            Object cur = it.next();
            Iterator it2 = this.objectContingent.iterator();
            while(it2.hasNext()) {
                Object cur2 = it2.next();
                if(cur.equals(cur2)) {
                    retVal.addObject(cur);
                }
            }
        }
        it = this.attributeContingent.iterator();
        while(it.hasNext()) {
            Object cur = it.next();
            retVal.addAttribute(cur);
        }
        return retVal;
    }
}