package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.model.Query;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

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
     * Sets the default value for showing contingent or extent.
     */
    static private boolean defaultShowContingentOnly = true;

    /**
     * Sets the default query used for new labels.
     */
    static private Query defaultQuery = null;

    /**
     * Stores the query we currently use.
     */
    private Query query = null;

    /**
     * Caches the result of the query.
     */
    private List queryResult = null;

    /**
     * Creates a view for the given label information.
     */
    public ObjectLabelView( DiagramView diagramView, LabelInfo label ) {
        super(diagramView,label);
        setDisplayType(defaultShowContingentOnly);
        setQuery(defaultQuery);
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
     * Overwritten to reset the query cache.
     */
    public void setDisplayType(boolean contingentOnly) {
        super.setDisplayType(contingentOnly);
        doQuery();
        if( this.getNumberOfEntries() > DEFAULT_DISPLAY_LINES ) {
            this.displayLines = DEFAULT_DISPLAY_LINES;
        }
        else {
            this.displayLines = this.getNumberOfEntries();
        }
        update(this);
    }

    /**
     * Sets the default display type for new labels.
     */
    static public void setDefaultDisplayType(boolean contingentOnly) {
        ObjectLabelView.defaultShowContingentOnly = contingentOnly;
    }

    /**
     * Sets the default query for new labels.
     */
    static public void setDefaultQuery(Query query) {
        ObjectLabelView.defaultQuery = query;
    }

    /**
     * Returns LabelView.BELOW
     */
    protected int getPlacement() {
        return LabelView.BELOW;
    }

    /**
     */
    public void setQuery(Query query) {
        this.query = query;
        doQuery();
        if( this.getNumberOfEntries() > DEFAULT_DISPLAY_LINES ) {
            this.displayLines = DEFAULT_DISPLAY_LINES;
        }
        else {
            this.displayLines = this.getNumberOfEntries();
        }
        update(this);
    }

    protected int getNumberOfEntries() {
        if(this.query == null) {
            return 0;
        }
        return this.queryResult.size();
    }

    protected Iterator getEntryIterator() {
        return this.queryResult.iterator();
    }

    protected void doQuery() {
        if(query != null) {
            this.queryResult = this.labelInfo.getNode().getConcept().executeQuery(
                                               this.query, this.showOnlyContingent);
        }
    }
}