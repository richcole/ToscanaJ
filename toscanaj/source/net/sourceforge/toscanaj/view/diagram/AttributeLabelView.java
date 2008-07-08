/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import java.awt.geom.Point2D;
import java.util.Iterator;

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;

/**
 * A LabelView for displaying the attributes.
 * 
 * This and the ObjectLabelView are used to distinguish between labels above and
 * below the nodes and the default display type (list or number).
 * 
 * @see ObjectLabelView
 */
public class AttributeLabelView extends LabelView {
    /**
     * @todo this is a quick hack to get a hide all feature, should be changed
     *       to some controller object or similar
     */
    protected static boolean allHidden = false;

    public static void setAllHidden(final boolean allHidden) {
        AttributeLabelView.allHidden = allHidden;
    }

    public static boolean allAreHidden() {
        return allHidden;
    }

    @Override
    public boolean isVisible() {
        return super.isVisible() && !allHidden;
    }

    public static LabelFactory getFactory() {
        return new LabelFactory() {
            public LabelView createLabelView(final DiagramView diagramView,
                    final NodeView nodeView, final LabelInfo label) {
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
    protected AttributeLabelView(final DiagramView diagramView,
            final NodeView nodeView, final LabelInfo label) {
        super(diagramView, nodeView, label);
    }

    /**
     * Returns LabelView.ABOVE
     */
    @Override
    protected int getPlacement() {
        return LabelView.ABOVE;
    }

    @Override
    public int getNumberOfEntries() {
        return this.labelInfo.getNode().getConcept()
                .getAttributeContingentSize();
    }

    @Override
    public Object getEntryAt(final int position) {
        final Iterator attributeContingentIterator = this.labelInfo.getNode()
                .getConcept().getAttributeContingentIterator();
        int i = 0;
        while (i < position) {
            attributeContingentIterator.next();
            i++;
        }
        return attributeContingentIterator.next();
    }

    @Override
    protected boolean highlightedInIdeal() {
        return false;
    }

    @Override
    protected boolean highlightedInFilter() {
        return true;
    }

    public FCAElement getEntryAtPosition(final Point2D canvasPosition) {
        final int i = getIndexOfPosition(canvasPosition);
        return (FCAElement) getEntryAt(i);
    }

    @Override
    protected boolean isFaded() {
        final int selectionState = nodeView.getSelectionState();
        return selectionState == DiagramView.NOT_SELECTED
                || selectionState == DiagramView.SELECTED_IDEAL;
    }
}
