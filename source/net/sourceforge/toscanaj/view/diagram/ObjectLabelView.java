package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;

import java.awt.Graphics2D;

import java.text.DecimalFormat;

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
     * Sets the default value for a special query.
     */
    static private String defaultSpecialQuery = null;

    /**
     * Sets the default format for a special query.
     */
    static private DecimalFormat defaultSpecialQueryFormat = null;

    /**
     * Stores a special query we use for aggregates etc.
     */
    private String specialQuery = null;

    /**
     * Stores the format for the results of special queries.
     *
     * If null it is just returned as string, if not it will be formatted as
     * decimal (double).
     */
    private DecimalFormat specialQueryFormat = null;

    /**
     * Creates a view for the given label information.
     */
    public ObjectLabelView( DiagramView diagramView, LabelInfo label ) {
        super(diagramView,label);
        setDisplayType(defaultDisplayType,defaultShowContingentOnly);
        setShowPercentage(defaultShowPercentage);
        this.specialQuery = defaultSpecialQuery;
        this.specialQueryFormat = defaultSpecialQueryFormat;
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
     * Resets teh special query string before calling the implementation of the
     * parent class.
     */
    public void setDisplayType(int type, boolean contingentOnly) {
        super.setDisplayType(type,contingentOnly);
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

    /**
     * Sets a special query to use for display.
     *
     * If the format string is given, this will be used to format the query
     * results using a DecimalFormat instance.
     *
     * @see java.text.DecimalFormat
     */
    public void setSpecialQuery(String query, String format) {
        this.setDisplayType(LabelView.DISPLAY_SPECIAL, this.showOnlyContingent);
        defaultDisplayType = LabelView.DISPLAY_SPECIAL;
        this.specialQuery = query;
        defaultSpecialQuery = query;
        if(format != null) {
            this.specialQueryFormat = new DecimalFormat(format);
        }
        else {
            this.specialQueryFormat = null;
        }
        defaultSpecialQueryFormat = this.specialQueryFormat;
    }

    /**
     * Overwrites LabelView.getNumberDisplay() to return the result of an
     * special query if needed.
     *
     * This assumes the label belongs to a DatabaseConnectedConcept.
     */
    protected String getNumberDisplay() {
        if(this.displayType == LabelView.DISPLAY_SPECIAL) {
            DatabaseConnectedConcept concept = (DatabaseConnectedConcept) this.labelInfo.getNode().getConcept();
            String val = concept.doSpecialQuery(this.specialQuery, this.showOnlyContingent);
            if(this.specialQueryFormat != null) {
                return this.specialQueryFormat.format(Double.parseDouble(val));
            }
            else {
                return val;
            }
        }
        else {
            return super.getNumberDisplay();
        }
    }
}