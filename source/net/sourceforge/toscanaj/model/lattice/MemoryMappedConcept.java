package net.sourceforge.toscanaj.model.lattice;

import net.sourceforge.toscanaj.model.ObjectListQuery;
import net.sourceforge.toscanaj.model.ObjectNumberQuery;
import net.sourceforge.toscanaj.model.Query;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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
    private List attributeContingent = makeList();

    /**
     * Stores the information on the object contingent.
     */
    private List objectContingent = makeList();

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
     * Implements Concept.executeQuery(Query, boolean).
     */
    public List executeQuery(Query query, boolean contingentOnly) {
        if (query instanceof ObjectListQuery) {
            if (contingentOnly) {
                List retVal = makeList();
                Iterator it = this.objectContingent.iterator();
                while (it.hasNext()) {
                    String cur = it.next().toString();
                    Vector item = new Vector(2);
                    item.add(0, null);
                    item.add(1, cur);
                    retVal.add(item);
                }
                return retVal;
            } else {
                List retVal = makeList();
                Iterator it = this.ideal.iterator();
                while (it.hasNext()) {
                    MemoryMappedConcept cur = (MemoryMappedConcept) it.next();
                    Iterator it2 = cur.objectContingent.iterator();
                    while (it2.hasNext()) {
                        String cur2 = it2.next().toString();
                        Vector item = new Vector(2);
                        item.add(0, null);
                        item.add(1, cur2);
                        retVal.add(item);
                    }
                }
                return retVal;
            }
        } else if (query instanceof ObjectNumberQuery) {
            List retVal = new LinkedList();
            int size = contingentOnly ? this.getObjectContingentSize() : this.getExtentSize();

            if(size >0){
                Vector item = new Vector(2);
                item.add(0, null);
                item.add(1, new Integer(size).toString());
                retVal.add(item);
            }
            return retVal;
        } else {
            throw new RuntimeException("Unknown Query type");
        }
    }

    /**
     * Implements Concept.filterByExtent(Concept).
     */
    public Concept filterByExtent(Concept other) {
        MemoryMappedConcept retVal = new MemoryMappedConcept();
        if (other == null) {
            retVal.objectContingent.addAll(this.objectContingent);
        } else {
            Iterator it = other.getExtentIterator();
            while (it.hasNext()) {
                Object cur = it.next();
                Iterator it2 = this.objectContingent.iterator();
                while (it2.hasNext()) {
                    Object cur2 = it2.next();
                    if (cur.equals(cur2)) {
                        retVal.addObject(cur);
                    }
                }
            }
        }
        retVal.attributeContingent.addAll(this.attributeContingent);
        return retVal;
    }

    /**
     * Implements Concept.filterByContingent(Concept).
     */
    public Concept filterByContingent(Concept other) {
        MemoryMappedConcept retVal = new MemoryMappedConcept();
        if (other == null) {
            retVal.objectContingent.addAll(this.objectContingent);
        } else {
            Iterator it = other.getObjectContingentIterator();
            while (it.hasNext()) {
                Object cur = it.next();
                Iterator it2 = this.objectContingent.iterator();
                while (it2.hasNext()) {
                    Object cur2 = it2.next();
                    if (cur.equals(cur2)) {
                        retVal.addObject(cur);
                    }
                }
            }
        }
        retVal.attributeContingent.addAll(this.attributeContingent);
        return retVal;
    }

    /**
     * Implements Concept.getCollapsedConcept().
     */
    public Concept getCollapsedConcept() {
        MemoryMappedConcept retVal = new MemoryMappedConcept();
        Iterator it = this.getExtentIterator();
        while (it.hasNext()) {
            Object cur = it.next();
            retVal.addObject(cur);
        }
        retVal.attributeContingent.addAll(this.attributeContingent);
        return retVal;
    }
}