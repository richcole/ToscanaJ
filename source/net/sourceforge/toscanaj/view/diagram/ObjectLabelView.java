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
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.lattice.Concept;

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
     * @todo this is a quick hack to get a hide all feature, should be changed
     * to some controller object or similar
     */
    protected static boolean allHidden = false;

    public static void setAllHidden(boolean allHidden) {
        ObjectLabelView.allHidden = allHidden;
    }

    public static boolean allAreHidden() {
        return allHidden;
    }

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

    public static LabelFactory getFactory() {
        return new LabelFactory(){
            public LabelView createLabelView(DiagramView diagramView,NodeView nodeView,LabelInfo label){
                return new ObjectLabelView(diagramView, nodeView, label);
            }
        };
    }

    /**
     * Creates a view for the given label information.
     */
    protected ObjectLabelView(DiagramView diagramView, NodeView nodeView, LabelInfo label) {
        super(diagramView, nodeView, label);
    }

    /**
     * Avoids drawing object labels for non-realised concepts.
     */
    public boolean isVisible() {
        Concept concept = this.labelInfo.getNode().getConcept();
        ConceptInterpretationContext context = nodeView.getConceptInterpretationContext();
        ConceptInterpreter interpreter = diagramView.getConceptInterpreter();
        return interpreter.isRealized(concept, context) && super.isVisible() && !allHidden;
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
        DiagramNode node = this.labelInfo.getNode();
        Concept concept = node.getConcept();
        ConceptInterpretationContext context = nodeView.getConceptInterpretationContext();
        ConceptInterpreter conceptInterpreter = this.diagramView.getConceptInterpreter();
        if(this.query == null) {
        	this.query = defaultQuery;
        }
        try {
			this.contents = conceptInterpreter.executeQuery(this.query, concept, context);
        } catch (Exception e) {
			ErrorDialog.showError(this.diagramView, e, "Getting object label content failed");
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
