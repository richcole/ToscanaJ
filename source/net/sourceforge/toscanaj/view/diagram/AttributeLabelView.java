package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.model.diagram.LabelInfo;

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
        // attribute labels always display the list (therefore we don't really mind
        // about the showPercentage toggle)
        setDisplayType(DISPLAY_LIST, true);
        setShowPercentage(false);
    }

    /**
     * Returns LabelView.ABOVE
     */
    protected int getPlacement() {
        return LabelView.ABOVE;
    }
}