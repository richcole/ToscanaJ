/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.controller.fca.*;
import net.sourceforge.toscanaj.model.database.*;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.lattice.Concept;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
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

    private List contents = null;

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
        if (this.contents == null) {
            return 0;
        }
        return this.contents.size();
    }

    protected Iterator getEntryIterator() {
        return this.contents.iterator();
    }

    protected void doQuery() {
        /// @todo try to get the distinction of query/no query somehow out of here
        DiagramNode node = this.labelInfo.getNode();
        ConceptInterpretationContext context = nodeView.getConceptInterpretationContext();
        if (query != null) {
            DatabaseConnectedConceptInterpreter conceptInterpreter =
                                (DatabaseConnectedConceptInterpreter) this.diagramView.getConceptInterpreter();
            contents = conceptInterpreter.executeQuery(query, node.getConcept(), context);
        }
        else {
            contents = new ArrayList();
            Iterator it = this.diagramView.getConceptInterpreter().getObjectSetIterator(node.getConcept(), context);
            while (it.hasNext()) {
                Object o = (Object) it.next();
                contents.add(o);
            }
        }
    }

    public Object getObjectAtPosition(Point2D position) {
        int itemHit = getItemAtPosition(position);
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
}