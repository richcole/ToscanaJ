/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.gui.dialog.DescriptionViewer;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.lattice.Attribute;
import org.jdom.Element;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
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
     * Creates a view for the given label information.
     */
    public AttributeLabelView(DiagramView diagramView, NodeView nodeView, LabelInfo label) {
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

    public Iterator getEntryIterator() {
        return this.labelInfo.getNode().getConcept().getAttributeContingentIterator();
    }

    public void openPopupMenu(MouseEvent event, Point2D pos) {
        int itemHit = getItemAtPosition(pos);
        Iterator it = getEntryIterator();
        Attribute attrib = null;
        while (itemHit >= 0) {
            itemHit--;
            attrib = (Attribute) it.next();
        }
        final Element description = attrib.getDescription();
        if (description == null) {
            return;
        }
        JPopupMenu popupMenu = new JPopupMenu();
        final DiagramView parent = this.diagramView;
        JMenuItem menuItem;
        menuItem = new JMenuItem("Description...");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DescriptionViewer.show(JOptionPane.getFrameForComponent(parent), description);
            }
        });
        popupMenu.add(menuItem);
        popupMenu.show(this.diagramView, event.getX(), event.getY());
    }

    protected boolean highlightedInIdeal() {
        return false;
    }

    protected boolean highlightedInFilter() {
        return true;
    }

    public String getEntryAtPosition(Point2D canvasPosition) {
        int i = getItemAtPosition(canvasPosition);
        Iterator it = getEntryIterator();
        String retVal = null;
        while(i != -1) {
            Attribute attr = (Attribute) it.next();
            retVal = attr.getData().toString();
            i--;
        }
        return retVal;
    }

    protected boolean isFaded() {
        return nodeView.getSelectionState() == DiagramView.NOT_SELECTED;
    }
}
