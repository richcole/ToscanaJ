/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.DatabaseConnectedConceptInterpreter;
import net.sourceforge.toscanaj.model.database.AggregateQuery;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.lattice.Concept;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
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

	// do not initialize this with null -- it happens after the superconstructor call which initializes
	// it by calling back through updateEntries(), which causes double queries
    private List contents;

    /**
     * Creates a view for the given label information.
     */
    public ObjectLabelView(DiagramView diagramView, NodeView nodeView, LabelInfo label) {
        super(diagramView, nodeView, label);
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
        updateEntries();
    }

    public int getNumberOfEntries() {
        if (this.contents == null) {
            return 0;
        }
        return this.contents.size();
    }

    public Iterator getEntryIterator() {
        if(this.contents == null) {
            updateEntries();
        }
        return this.contents.iterator();
    }

    protected void doQuery() {
        /// @todo try to get the distinction of query/no query somehow out of here
        DiagramNode node = this.labelInfo.getNode();
        Concept concept = node.getConcept();
        ConceptInterpretationContext context = nodeView.getConceptInterpretationContext();
        ConceptInterpreter conceptInterpreter = this.diagramView.getConceptInterpreter();
        if(this.query == null) {
        	this.query = defaultQuery;
        }
        if(!conceptInterpreter.isRealized(concept, context)) {
        	this.contents = null;
        } else if (query == ListQuery.KEY_LIST_QUERY) {
            int objectCount = conceptInterpreter.getObjectCount(concept, context);
            if( objectCount != 0) {
	            contents = new ArrayList();
	            Iterator it = conceptInterpreter.getObjectSetIterator(concept, context);
	            while (it.hasNext()) {
	                Object o = it.next();
	                contents.add(o);
	            }
            } else {
                contents = null;
            }
        } else if (query == AggregateQuery.COUNT_QUERY) {
            int objectCount = conceptInterpreter.getObjectCount(concept, context);
            if( objectCount != 0) {
		        contents = new ArrayList();
		        contents.add(new Integer(objectCount));
            } else {
            	contents = null;
            }
        } else {
            DatabaseConnectedConceptInterpreter dbConceptInterpreter =
                    (DatabaseConnectedConceptInterpreter) conceptInterpreter;
            contents = dbConceptInterpreter.executeQuery(query, concept, context);
        }
    }

    public Object getObjectAtPosition(Point2D position) {
        int itemHit = getIndexOfPosition(position);
        if (itemHit == -1) {
            return null;
        }
        return this.contents.get(itemHit);
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

    protected boolean isFaded() {
        int selectionState = nodeView.getSelectionState();
        return selectionState == DiagramView.NOT_SELECTED || selectionState == DiagramView.SELECTED_FILTER;
    }
}
