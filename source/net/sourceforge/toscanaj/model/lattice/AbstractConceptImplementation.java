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
 *
 * To use this class one has to ensure all sub- and superconcept relations
 * are set up properly. If only the neighbourhood relation is set the method
 * buildClosures() can be called to extent this to the full sub-/superconcept
 * relation.
 */
public abstract class AbstractConceptImplementation implements Concept
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
            // make sure that we point to the next object, even if there are
            // empty contingents coming ahead
            while( ! this.secondaryIterator.hasNext() &&  this.mainIterator.hasNext() ) {
                // go to next concept
                Concept next = (Concept) this.mainIterator.next();
                this.secondaryIterator = next.getAttributeContingentIterator();
            }
            return this.secondaryIterator.hasNext();
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
            // make sure that we point to the next object, even if there are
            // empty contingents coming ahead
            while( ! this.secondaryIterator.hasNext() &&  this.mainIterator.hasNext() ) {
                // go to next concept
                Concept next = (Concept) this.mainIterator.next();
                this.secondaryIterator = next.getObjectContingentIterator();
            }
            return this.secondaryIterator.hasNext();
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
     * Calculates the ideal and filter for this concept if only direct neighbours
     * are given.
     *
     * If only direct neighbours in the neighbourhoud relation where given this
     * method can be called to create the ideal and filter by building the
     * transitive closures.
     */
    public void buildClosures() {
        for(int i = 0; i < ideal.size(); i++) {
            AbstractConceptImplementation other = (AbstractConceptImplementation) ideal.get(i);
            Iterator it = other.ideal.iterator();
            while(it.hasNext()) {
                Object trans = it.next();
                if(! ideal.contains(trans) ) {
                    ideal.add(trans);
                }
            }
        }
        for(int i = 0; i < filter.size(); i++) {
            AbstractConceptImplementation other = (AbstractConceptImplementation) filter.get(i);
            Iterator it = other.filter.iterator();
            while(it.hasNext()) {
                Object trans = it.next();
                if(! filter.contains(trans) ) {
                    filter.add(trans);
                }
            }
        }
    }

    /**
     * Checks if the concept is realised.
     *
     * This is done be looking if any concept in the ideal has the same extent.
     */
    public boolean isRealised() {
        int extentSize = this.getExtentSize();
        Iterator it = this.ideal.iterator();
        while(it.hasNext()) {
            Concept cur = (Concept) it.next();
            if(cur != this) {
                if(cur.getExtentSize() == extentSize) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Calculates the intent size based on the contingent sizes in the filter.
     */
    public int getIntentSize() {
        int retVal = 0;
        Iterator it = ideal.iterator();
        while( it.hasNext() ) {
            Concept cur = (Concept) it.next();
            retVal += cur.getAttributeContingentSize();
        }
        return retVal;
    }

    /**
     * Calculates the relative intent size.
     */
    public double getIntentSizeRelative() {
        return getIntentSize() / (double)getNumberOfAttributes();
    }

    /**
     * Calculates the extent size based on the contingent sizes in the ideal.
     */
    public int getExtentSize() {
        int retVal = 0;
        Iterator it = ideal.iterator();
        while( it.hasNext() ) {
            Concept cur = (Concept) it.next();
            retVal += cur.getObjectContingentSize();
        }
        return retVal;
    }

    /**
     * Calculates the relative extent size.
     */
    public double getExtentSizeRelative() {
        return getExtentSize() / (double)getNumberOfObjects();
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

    /**
     * Calculates the relative attribute contingent size.
     */
    public double getAttributeContingentSizeRelative() {
        return getAttributeContingentSize() / (double)getNumberOfAttributes();
    }

    /**
     * Calculates the relative object contingent size.
     */
    public double getObjectContingentSizeRelative() {
        return getObjectContingentSize() / (double)getNumberOfObjects();
    }

    /**
     * Find the number of objects in this diagram.
     *
     * This is equal to the size of the extent of the top node.
     */
    private int getNumberOfObjects() {
        AbstractConceptImplementation cur = this;
        while(cur.filter.size() != 1) {
            // there is another concept in the filter which is not this
            // (this is always the first) ==> go up
            cur = (AbstractConceptImplementation) cur.filter.get(1);
        }
        // now we are at the top
        return cur.getExtentSize();
    }

    /**
     * Find the number of attributes in this diagram.
     *
     * This is equal to the size of the intent of the bottom node.
     */
    private int getNumberOfAttributes() {
        AbstractConceptImplementation cur = this;
        while(cur.ideal.size() != 1) {
            // there is another concept in the ideal which is not this
            // (this is always the first) ==> go down
            cur = (AbstractConceptImplementation) cur.ideal.get(1);
        }
        // now we are at the bottom
        return cur.getIntentSize();
    }
}