/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.lattice.Attribute;

import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 * A LabelView for displaying the attributes.
 *
 * This and the ObjectLabelView are used to distinguish between labels above
 * and below the nodes and the default display type (list or number).
 *
 * @see ObjectLabelView
 */
public class AttributeLabelView extends LabelView {
    /**
     * @todo this is a quick hack to get a hide all feature, should be changed
     * to some controller object or similar
     */
    protected static boolean allHidden = false;

    public static void setAllHidden(boolean allHidden) {
        AttributeLabelView.allHidden = allHidden;
    }

    public static boolean allAreHidden() {
        return allHidden;
    }

    public boolean isVisible() {
        return super.isVisible() && !allHidden;
    }
	
    public static LabelFactory getFactory() {
        return new LabelFactory(){
            public LabelView createLabelView(DiagramView diagramView,NodeView nodeView,LabelInfo label){
                return new AttributeLabelView(diagramView, nodeView, label);
            }

			public Class getLabelClass() {
				return AttributeLabelView.class;
			}
        };
    }

    /**
     * Creates a view for the given label information.
     */
    protected AttributeLabelView(DiagramView diagramView, NodeView nodeView, LabelInfo label) {
        super(diagramView, nodeView, label);
    }

    /**
     * Returns LabelView.ABOVE
     */
    protected int getPlacement() {
        return LabelView.ABOVE;
    }

    public int getNumberOfEntries() {
        return this.labelInfo.getNode().getConcept().getAttributeContingentSize();
    }

    public Object getEntryAt(int position) {
        Iterator attributeContingentIterator = this.labelInfo.getNode().getConcept().getAttributeContingentIterator();
        int i = 0;
        while(i < position) {
        	attributeContingentIterator.next();
        }
        return attributeContingentIterator.next();
    }

    protected boolean highlightedInIdeal() {
        return false;
    }

    protected boolean highlightedInFilter() {
        return true;
    }

    public Attribute getEntryAtPosition(Point2D canvasPosition) {
        int i = getIndexOfPosition(canvasPosition);
        return (Attribute) getEntryAt(i);
    }

    protected boolean isFaded() {
        return nodeView.getSelectionState() == DiagramView.NOT_SELECTED;
    }
}
