package net.sourceforge.toscanaj.model.lattice;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This implements some shortcuts for implementing concepts.
 *
 * Intent and extent are mapped into filter and ideal resp. to avoid redundant
 * storage. Filter and ideal are explicitely stored to reduce computational
 * efforts for these operations. The calculation of intent and extent size is
 * done in this class, the joins on the sets themselves are done by creating an
 * iterator which iterates over all contingents in filter and ideal resp.
 */
abstract class AbstractConceptImplementation implements Concept
{
    /**
     * This class implements an iterator that iterates over all attribute
     * contingents of a given concept set.
     */
    class AttributeIterator implements Iterator {
        /**
         * Stores the main iterator on the concepts.
         */
        Iterator mainIterator;

        /**
         * Stores the secondary iterator on the attributes of one concept.
         */
        Iterator secondaryIterator;

        /**
         * We start with the iterator of all concepts that we want to visit.
         */
        AttributeIterator( Iterator main ) {
            this.mainIterator = main;
            if( main.hasNext() ) {
                Concept first = (Concept) main.next();
                this.secondaryIterator = first.getAttributeContingentIterator();
            }
            else {
                this.secondaryIterator = null;
            }
        }

        /**
         * Returns true if we didn't iterate through all attributes in the filter
         * yet.
         */
        public boolean hasNext() {
            return this.mainIterator.hasNext() && this.secondaryIterator.hasNext();
        }

        /**
         * Returns the next attribute.
         */
        public Object next() {
            if( this.secondaryIterator == null ) {
                throw new NoSuchElementException();
            }
            // Assume: secIt not null
            if( ! this.secondaryIterator.hasNext() && ! this.mainIterator.hasNext() ) {
                // we were already finished
                throw new NoSuchElementException();
            }
            // Assume: there is something to go for
            Object retVal = this.secondaryIterator.next();
            // make sure that we point to the next attribute, even if there are
            // empty contingents coming ahead
            while( ! this.secondaryIterator.hasNext() &&  this.mainIterator.hasNext() ) {
                // go to next concept
                Concept next = (Concept) this.mainIterator.next();
                this.secondaryIterator = next.getAttributeContingentIterator();
            }
            return this.secondaryIterator.next();
         }

         /**
          * Throws UnsupportedOperationException.
          */
         public void remove() {
            throw new UnsupportedOperationException();
         }
    }

    /**
     * This class implements an iterator that iterates over all object
     * contingents of a given concept set.
     */
    class ObjectIterator implements Iterator {
        /**
         * Stores the main iterator on the concepts.
         */
        Iterator mainIterator;

        /**
         * Stores the secondary iterator on the objects of one concept.
         */
        Iterator secondaryIterator;

        /**
         * We start with the iterator of all concepts that we want to visit.
         */
        ObjectIterator( Iterator main ) {
            this.mainIterator = main;
            if( main.hasNext() ) {
                Concept first = (Concept) main.next();
                this.secondaryIterator = first.getObjectContingentIterator();
            }
            else {
                this.secondaryIterator = null;
            }
        }

        /**
         * Returns true if we didn't iterate through all objects in the ideal
         * yet.
         */
        public boolean hasNext() {
            return this.mainIterator.hasNext() && this.secondaryIterator.hasNext();
        }

        /**
         * Returns the next object.
         */
        public Object next() {
            if( this.secondaryIterator == null ) {
                throw new NoSuchElementException();
            }
            // Assume: secIt not null
            if( ! this.secondaryIterator.hasNext() && ! this.mainIterator.hasNext() ) {
                // we were already finished
                throw new NoSuchElementException();
            }
            // Assume: there is something to go for
            Object retVal = this.secondaryIterator.next();
            // make sure that we point to the next object, even if there are
            // empty contingents coming ahead
            while( ! this.secondaryIterator.hasNext() &&  this.mainIterator.hasNext() ) {
                // go to next concept
                Concept next = (Concept) this.mainIterator.next();
                this.secondaryIterator = next.getObjectContingentIterator();
            }
            return this.secondaryIterator.next();
         }

         /**
          * Throws UnsupportedOperationException.
          */
         public void remove() {
            throw new UnsupportedOperationException();
         }
    }

    /**
     * Stores all concepts in the filter, including this.
     */
    List filter = null;

    /**
     * Stores all concepts in the ideal, including this.
     */
    List ideal = null;

    /**
     * Initializes the ideal and filter with linked list having a reference to
     * this.
     *
     * Use addSuperConcept(Concept) and addSubConcept(Concept) to extent filter
     * and ideal.
     */
    public AbstractConceptImplementation() {
        this.filter = new LinkedList();
        this.filter.add( this );
        this.ideal = new LinkedList();
        this.ideal.add( this );
    }

    /**
     * Adds a concept to the filter.
     */
    public void addSuperConcept( Concept superConcept ) {
        this.filter.add( superConcept );
    }

    /**
     * Adds a concept to the ideal.
     */
    public void addSubConcept( Concept superConcept ) {
        this.ideal.add( superConcept );
    }

    /**
     * Calculates the intent size based on the contingent sizes in the filter.
     */
    public int getIntentSize() {
        int retVal = this.getAttributeContingentSize();
        Iterator it = ideal.iterator();
        while( it.hasNext() ) {
            Concept cur = (Concept) it.next();
            retVal += cur.getAttributeContingentSize();
        }
        return retVal;
    }

    /**
     * Calculates the extent size based on the contingent sizes in the ideal.
     */
    public int getExtentSize() {
        int retVal = this.getObjectContingentSize();
        Iterator it = ideal.iterator();
        while( it.hasNext() ) {
            Concept cur = (Concept) it.next();
            retVal += cur.getObjectContingentSize();
        }
        return retVal;
    }

    /**
     * Iterates over all attribute contingents in the filter.
     */
    public Iterator getIntentIterator() {
        return new AttributeIterator( this.filter.iterator() );
    }

    /**
     * Iterates over all object contingents in the ideal.
     */
    public Iterator getExtentIterator() {
        return new ObjectIterator( this.ideal.iterator() );
    }
}