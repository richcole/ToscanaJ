package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.model.diagram.LabelInfo;

import java.awt.Graphics2D;

/**
 * A LabelView for displaying the objects.
 *
 * This and the AttributeLabelView are used to distinguish between labels above
 * and below the nodes and the default display type (list or number).
 *
 * @see AttributeLabelView
 */
public class ObjectLabelView extends LabelView {
    /**
     * Sets the default display type for new labels.
     */
    static private int defaultDisplayType = DISPLAY_NUMBER;

    /**
     * Sets the default value for showing contingent or extent.
     */
    static private boolean defaultShowContingentOnly = true;

    /**
     * Sets the default value for showing percentual distribution or not.
     */
    static private boolean defaultShowPercentage = false;

    /**
     * Creates a view for the given label information.
     */
    public ObjectLabelView( DiagramView diagramView, LabelInfo label ) {
        super(diagramView,label);
        setDisplayType(defaultDisplayType,defaultShowContingentOnly);
        setShowPercentage(defaultShowPercentage);
    }

    /**
     * Avoids drawing object labels for non-realised concepts.
     */
    public void draw(Graphics2D graphics) {
        if(this.labelInfo.getNode().getConcept().isRealised()) {
            super.draw(graphics);
        }
    }

    /**
     * Sets the default display type for new labels.
     */
    static public void setDefaultDisplayType(int type, boolean contingentOnly) {
        ObjectLabelView.defaultDisplayType = type;
        ObjectLabelView.defaultShowContingentOnly = contingentOnly;
    }

    /**
     * Changes the startup behaviour of new labels on showing percentual
     * distribution or not.
     */
    static public void setDefaultShowPercentage(boolean toggle) {
        ObjectLabelView.defaultShowPercentage = toggle;
    }

    /**
     * Returns LabelView.BELOW
     */
    protected int getPlacement() {
        return LabelView.BELOW;
    }
}