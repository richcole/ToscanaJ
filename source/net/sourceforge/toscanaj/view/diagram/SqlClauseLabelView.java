/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import java.util.Iterator;

import net.sourceforge.toscanaj.model.diagram.LabelInfo;

/**
 * A LabelView for displaying the SQL clauses.
 */
public class SqlClauseLabelView extends LabelView {
    public static LabelFactory getFactory() {
        return new LabelFactory(){
            public LabelView createLabelView(DiagramView diagramView,NodeView nodeView,LabelInfo label){
                return new SqlClauseLabelView(diagramView, nodeView, label);
            }
        };
    }

    protected SqlClauseLabelView(DiagramView diagramView, NodeView nodeView, LabelInfo label) {
        super(diagramView, nodeView, label);
    }

    protected int getPlacement() {
        return LabelView.BELOW;
    }

    public int getNumberOfEntries() {
        return this.labelInfo.getNode().getConcept().getObjectContingentSize();
    }

    public Iterator getEntryIterator() {
        return this.labelInfo.getNode().getConcept().getObjectContingentIterator();
    }

    protected boolean highlightedInIdeal() {
        return true;
    }

    protected boolean highlightedInFilter() {
        return false;
    }

    protected boolean isFaded() {
        return nodeView.getSelectionState() == DiagramView.NOT_SELECTED;
    }
}
