/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.controller.db.WhereClauseGenerator;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.model.database.*;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;

import java.awt.*;
import java.awt.geom.Point2D;
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
     * Sets the default query used for new labels.
     */
    static private Query defaultQuery = null;

    /**
     * Stores the query we currently use.
     */
    private Query query = null;

    private List queryResults = null;

    /**
     * Creates a view for the given label information.
     */
    public ObjectLabelView(DiagramView diagramView, NodeView nodeView, LabelInfo label) {
        super(diagramView, nodeView, label);
        setQuery(defaultQuery);
    }

    /**
     * Avoids drawing object labels for non-realised concepts.
     */
    public void draw(Graphics2D graphics) {
        Concept concept = this.labelInfo.getNode().getConcept();
        ConceptInterpretationContext context = nodeView.getConceptInterpretationContext();
        ConceptInterpreter interpreter = diagramView.getConceptInterpreter();
        if (interpreter.isRealized(concept, context)) {
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
        if (this.getNumberOfEntries() > DEFAULT_DISPLAY_LINES) {
            this.displayLines = DEFAULT_DISPLAY_LINES;
        } else {
            this.displayLines = this.getNumberOfEntries();
        }
        this.firstItem = 0;
        update(this);
    }

    protected int getNumberOfEntries() {
        if (this.queryResults == null) {
            return 0;
        }
        return this.queryResults.size();
    }

    protected Iterator getEntryIterator() {
        return this.queryResults.iterator();
    }

    protected void doQuery() {
        if (query != null) {
            DiagramNode node = this.labelInfo.getNode();
            DatabaseConnectedConcept concept = (DatabaseConnectedConcept) node.getConcept();
            ConceptInterpretationContext context = nodeView.getConceptInterpretationContext();
            boolean objectDisplayMode = context.getObjectDisplayMode();
            boolean filterMode = context.getFilterMode();
            if (concept.getObjectClause() != null ||
                    ((objectDisplayMode == ConceptInterpretationContext.EXTENT) && !concept.isBottom())
            ) {
                String whereClause = WhereClauseGenerator.createWhereClause(concept,
                        context.getDiagramHistory(),
                        context.getNestingConcepts(),
                        objectDisplayMode,
                        filterMode);
                queryResults = this.query.execute(whereClause);
            } else {
                queryResults = null;
            }
        }
    }

    public DatabaseRetrievedObject getObjectAtPosition(Point2D position) {
        int itemHit = getItemAtPosition(position);
        if (itemHit == -1) {
            return null;
        }
        return (DatabaseRetrievedObject) this.queryResults.get(itemHit);
    }

    protected boolean highlightedInIdeal() {
        return true;
    }

    protected boolean highlightedInFilter() {
        return false;
    }

    public Query getQuery() {
        return this.query;
    }
}