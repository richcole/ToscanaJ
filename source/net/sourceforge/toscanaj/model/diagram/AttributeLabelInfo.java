package net.sourceforge.toscanaj.model.diagram;

import java.util.Iterator;

/**
 * Implements LabelInfo for the attribute contingent.
 */
public class AttributeLabelInfo extends LabelInfo {
    /**
     * Calls the default constructor of the superclass.
     */
    public AttributeLabelInfo() {
        super();
    }

    /**
     * Calls the copy constructor of the superclass.
     */
    public AttributeLabelInfo(LabelInfo other) {
        super(other);
    }

    /**
     * Returns the number of attributes in the contingent of the concept attached.
     */
    public int getNumberOfEntries(boolean contingentOnly) {
        if(contingentOnly) {
            return this.getNode().getConcept().getAttributeContingentSize();
        }
        else {
            return this.getNode().getConcept().getIntentSize();
        }
    }

    /**
     * Returns the number of attributes in the contingent of the concept attached
     * as relative number in comparison to all attributes in the diagram.
     */
    public double getNumberOfEntriesRelative(boolean contingentOnly) {
        if(contingentOnly) {
            return this.getNode().getConcept().getAttributeContingentSizeRelative();
        }
        else {
            return this.getNode().getConcept().getIntentSizeRelative();
        }
    }

    /**
     * Returns an attribute from the contingent of the concept attached.
     */
    public Iterator getEntryIterator(boolean contingentOnly) {
        if(contingentOnly) {
            return this.getNode().getConcept().getAttributeContingentIterator();
        }
        else {
            return this.getNode().getConcept().getIntentIterator();
        }
    }
}