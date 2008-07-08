/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import java.awt.geom.Point2D;

import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.lattice.Concept;

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
     *       to some controller object or similar
     */
    protected static boolean allHidden = false;

    public static void setAllHidden(final boolean allHidden) {
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

    // do not initialize this with null -- it happens after the superconstructor
    // call which initializes
    // it by calling back through updateEntries(), which causes double queries
    private FCAElement[] contents;

    public static LabelFactory getFactory() {
        return new LabelFactory() {
            public LabelView createLabelView(final DiagramView diagramView,
                    final NodeView nodeView, final LabelInfo label) {
                return new ObjectLabelView(diagramView, nodeView, label);
            }

            public Class getLabelClass() {
                return ObjectLabelView.class;
            }
        };
    }

    /**
     * Creates a view for the given label information.
     */
    protected ObjectLabelView(final DiagramView diagramView,
            final NodeView nodeView, final LabelInfo label) {
        super(diagramView, nodeView, label);
    }

    /**
     * Avoids drawing object labels for non-realised concepts.
     */
    @Override
    public boolean isVisible() {
        final Concept concept = this.labelInfo.getNode().getConcept();
        final ConceptInterpretationContext context = nodeView
                .getConceptInterpretationContext();
        final ConceptInterpreter interpreter = diagramView
                .getConceptInterpreter();
        return interpreter.isVisible(concept, context) && super.isVisible()
                && !allHidden;
    }

    @Override
    public void updateEntries() {
        doQuery();
        if (this.getNumberOfEntries() > DEFAULT_DISPLAY_LINES) {
            this.displayLines = DEFAULT_DISPLAY_LINES;
        } else {
            this.displayLines = this.getNumberOfEntries();
        }
        this.firstItem = 0;
        update(this);
    }

    /**
     * Sets the default query for new labels.
     */
    static public void setDefaultQuery(final Query query) {
        ObjectLabelView.defaultQuery = query;
    }

    static public Query getDefaultQuery() {
        return ObjectLabelView.defaultQuery;
    }

    /**
     * Returns LabelView.BELOW
     */
    @Override
    protected int getPlacement() {
        return LabelView.BELOW;
    }

    /**
     */
    public void setQuery(final Query query) {
        this.query = query;
        updateEntries();
    }

    @Override
    public int getNumberOfEntries() {
        if (this.contents == null) {
            return 0;
        }
        return this.contents.length;
    }

    @Override
    public Object getEntryAt(final int position) {
        if (this.contents == null) {
            updateEntries();
        }
        return this.contents[position];
    }

    protected void doQuery() {
        final DiagramNode node = this.labelInfo.getNode();
        final Concept concept = node.getConcept();
        final ConceptInterpretationContext context = nodeView
                .getConceptInterpretationContext();
        final ConceptInterpreter conceptInterpreter = this.diagramView
                .getConceptInterpreter();
        try {
            this.contents = conceptInterpreter.executeQuery(getQuery(),
                    concept, context);
        } catch (final Exception e) {
            ErrorDialog.showError(this.diagramView, e,
                    "Getting object label content failed");
        }
    }

    public FCAElement getObjectAtPosition(final Point2D position) {
        final int itemHit = getIndexOfPosition(position);
        if (itemHit == -1) {
            return null;
        }
        return this.contents[itemHit];
    }

    @Override
    protected boolean highlightedInIdeal() {
        return true;
    }

    @Override
    protected boolean highlightedInFilter() {
        return false;
    }

    public Query getQuery() {
        if (this.query == null) {
            this.query = defaultQuery;
        }
        return this.query;
    }

    @Override
    protected boolean isFaded() {
        final int selectionState = nodeView.getSelectionState();
        return selectionState == DiagramView.NOT_SELECTED
                || selectionState == DiagramView.SELECTED_FILTER;
    }
}
