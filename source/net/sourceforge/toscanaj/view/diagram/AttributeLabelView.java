/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.gui.dialog.DescriptionViewer;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.lattice.Attribute;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
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
    public AttributeLabelView(DiagramView diagramView, LabelInfo label, ConceptInterpreter conceptInterpreter) {
        super(diagramView, label, conceptInterpreter);
        setDisplayType(true);
    }

    /**
     * Returns LabelView.ABOVE
     */
    protected int getPlacement() {
        return LabelView.ABOVE;
    }

    protected int getNumberOfEntries() {
        if (this.showOnlyContingent) {
            return this.labelInfo.getNode().getConcept().getAttributeContingentSize();
        } else {
            return this.labelInfo.getNode().getConcept().getIntentSize();
        }
    }

    protected Iterator getEntryIterator() {
        if (this.showOnlyContingent) {
            return this.labelInfo.getNode().getConcept().getAttributeContingentIterator();
        } else {
            return this.labelInfo.getNode().getConcept().getIntentIterator();
        }
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
}