package net.sourceforge.toscanaj.model.diagram;

import java.util.Iterator;

/**
 * Implements LabelInfo for the object contingent.
 */
public class ObjectLabelInfo extends LabelInfo {
    /**
     * Calls the default constructor of the superclass.
     */
    public ObjectLabelInfo() {
        super();
    }

    /**
     * Calls the copy constructor of the superclass.
     */
    public ObjectLabelInfo(LabelInfo other) {
        super(other);
    }

    /**
     * Returns the number of objects in the contingent of the concept attached.
     */
    public int getNumberOfEntries(boolean contingentOnly) {
        if(contingentOnly) {
            return this.getNode().getConcept().getObjectContingentSize();
        }
        else {
            return this.getNode().getConcept().getExtentSize();
        }
    }

    /**
     * Returns the number of objects in the contingent of the concept attached as
     * a relative number compared to all objects still available.
     */
    public double getNumberOfEntriesRelative(boolean contingentOnly) {
        if(contingentOnly) {
            return this.getNode().getConcept().getObjectContingentSizeRelative();
        }
        else {
            return this.getNode().getConcept().getExtentSizeRelative();
        }
    }

    /**
     * Returns an object from the contingent of the concept attached.
     */
    public Iterator getEntryIterator(boolean contingentOnly) {
        if(contingentOnly) {
            return this.getNode().getConcept().getObjectContingentIterator();
        }
        else {
            return this.getNode().getConcept().getExtentIterator();
        }
    }
}