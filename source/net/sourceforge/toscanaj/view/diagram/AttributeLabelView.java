package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.model.diagram.LabelInfo;

import java.util.Iterator;

/**
 * A LabelView for displaying the attributes.
 *
 * This and the ObjectLabelView are used to distinguish between labels above
 * and below the nodes and the default display type (list or number).
 *
 * @see ObjectLabelView
 */
public class AttributeLabelView extends LabelView {
    /**
     * Creates a view for the given label information.
     */
    public AttributeLabelView( DiagramView diagramView, LabelInfo label ) {
        super(diagramView,label);
        setDisplayType(true);
    }

    /**
     * Returns LabelView.ABOVE
     */
    protected int getPlacement() {
        return LabelView.ABOVE;
    }

    protected int getNumberOfEntries() {
        if(this.showOnlyContingent) {
            return this.labelInfo.getNode().getConcept().getAttributeContingentSize();
        }
        else {
            return this.labelInfo.getNode().getConcept().getIntentSize();
        }
    }

    protected Iterator getEntryIterator() {
        if(this.showOnlyContingent) {
            return this.labelInfo.getNode().getConcept().getAttributeContingentIterator();
        }
        else {
            return this.labelInfo.getNode().getConcept().getIntentIterator();
        }
    }
}