/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.model.database.*;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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
     * Sets the default query used for new labels.
     */
    static private DatabaseQuery defaultQuery = null;

    /**
     * Stores the query we currently use.
     */
    private DatabaseQuery query = null;

    private List queryResults = null;

    /**
     * Creates a view for the given label information.
     */
    public ObjectLabelView(DiagramView diagramView, LabelInfo label) {
        super(diagramView, label);
        setQuery(defaultQuery);
    }

    /**
     * Avoids drawing object labels for non-realised concepts.
     */
    public void draw(Graphics2D graphics) {
        if (this.labelInfo.getNode().getConcept().isRealised()) {
            super.draw(graphics);
        }
    }

    public void updateEntries() {
        doQuery();
        if (this.getNumberOfEntries() > DEFAULT_DISPLAY_LINES) {
            this.displayLines = DEFAULT_DISPLAY_LINES;
        } else {
            this.displayLines = this.getNumberOfEntries();
        }
        update(this);
    }

    /**
     * Sets the default query for new labels.
     */
    static public void setDefaultQuery(DatabaseQuery query) {
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
    public void setQuery(DatabaseQuery query) {
        this.query = query;
        doQuery();
        if (this.getNumberOfEntries() > DEFAULT_DISPLAY_LINES) {
            this.displayLines = DEFAULT_DISPLAY_LINES;
        } else {
            this.displayLines = this.getNumberOfEntries();
        }
        this.firstItem = 0;
        update(this);
    }

    protected int getNumberOfEntries() {
        if (this.query == null) {
            return 0;
        }
        return this.queryResults.size();
    }

    protected Iterator getEntryIterator() {
        return this.queryResults.iterator();
    }

    protected void doQuery() {
        if (query != null) {
            queryResults = this.query.execute(this.labelInfo.getNode().getConcept(),
                                              diagramView.getConceptInterpretationContext().getObjectDisplayMode() );
        }
    }

    public DatabaseRetrievedObject getObjectAtPosition(Point2D position) {
        int itemHit = getItemAtPosition(position);
        if( itemHit == -1 ) {
            return null;
        }
        return (DatabaseRetrievedObject) this.queryResults.get(itemHit);
    }
}