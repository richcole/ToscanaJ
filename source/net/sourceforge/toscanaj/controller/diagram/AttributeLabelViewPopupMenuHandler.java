/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.AttributeLabelView;
import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

public class AttributeLabelViewPopupMenuHandler implements EventListener {
    private DiagramView diagramView;

    public AttributeLabelViewPopupMenuHandler(DiagramView diagramView, EventBroker schemaBroker) {
        this.diagramView = diagramView;
    }

    public void processEvent(Event e) {
        CanvasItemEventWithPosition itemEvent = null;
        try {
            itemEvent = (CanvasItemEventWithPosition) e;
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to CanvasItemEventWithPositions only");
        }
        AttributeLabelView labelView = null;
        try {
            labelView = (AttributeLabelView) itemEvent.getItem();
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to events from ObjectLabelViews only");
        }
        openPopupMenu(labelView, itemEvent.getCanvasPosition(), itemEvent.getAWTPosition());
    }

    public void openPopupMenu(final AttributeLabelView labelView, Point2D canvasPosition, Point2D screenPosition) {
        final String attribute = labelView.getEntryAtPosition(canvasPosition);
        if (attribute == null) {
            return;
        }
        List attributeViewNames = DatabaseViewerManager.getAttributeViewNames();
        if (attributeViewNames.isEmpty()) { // nothing to display
            return;
        }
        // create the menu
        JPopupMenu popupMenu = new JPopupMenu();
        addAttributeViewOptions(attributeViewNames, attribute, popupMenu);
        popupMenu.show(this.diagramView, (int) screenPosition.getX(), (int) screenPosition.getY());
    }

    private void addAttributeViewOptions(List attributeViewNames, final String attribute, JPopupMenu popupMenu) {
        JMenuItem menuItem;
        Iterator it = attributeViewNames.iterator();
        while (it.hasNext()) {
            final String attributeViewName = (String) it.next();
            menuItem = new JMenuItem(attributeViewName);
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    DatabaseViewerManager.showAttribute(attributeViewName, attribute);
                }
            });
            popupMenu.add(menuItem);
        }
    }
}
