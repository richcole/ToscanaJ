package net.sourceforge.toscanaj.model.diagram;

import java.util.Iterator;

/**
 * Implements LabelInfo for the object contingent.
 */
public class ObjectLabelInfo extends LabelInfo {
    /**
     * Returns the number of objects in the contingent of the concept attached.
     */
    public int getNumberOfEntries() {
        return this.getNode().getConcept().getObjectContingentSize();
    }

    /**
     * Returns an object from the contingent of the concept attached.
     */
    public Iterator getEntryIterator() {
        return this.getNode().getConcept().getObjectContingentIterator();
    }
}