/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.gui.dialog.DescriptionViewer;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.view.diagram.AttributeLabelView;
import net.sourceforge.toscanaj.view.diagram.DiagramView;

import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

public class AttributeLabelViewPopupMenuHandler implements EventBrokerListener {
    private final DiagramView diagramView;

    public AttributeLabelViewPopupMenuHandler(final DiagramView diagramView) {
        this.diagramView = diagramView;
    }

    public void processEvent(final Event e) {
        CanvasItemEventWithPosition itemEvent = null;
        try {
            itemEvent = (CanvasItemEventWithPosition) e;
        } catch (final ClassCastException e1) {
            throw new RuntimeException(
                    getClass().getName()
                            + " has to be subscribed to CanvasItemEventWithPositions only");
        }
        AttributeLabelView labelView = null;
        try {
            labelView = (AttributeLabelView) itemEvent.getSubject();
        } catch (final ClassCastException e1) {
            throw new RuntimeException(
                    getClass().getName()
                            + " has to be subscribed to events from ObjectLabelViews only");
        }
        openPopupMenu(labelView, itemEvent.getCanvasPosition(), itemEvent
                .getAWTPosition());
    }

    public void openPopupMenu(final AttributeLabelView labelView,
            final Point2D canvasPosition, final Point2D screenPosition) {
        final FCAElement attribute = labelView
                .getEntryAtPosition(canvasPosition);
        if (attribute == null) {
            return;
        }
        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem;

        final List<String> attributeViewNames = DatabaseViewerManager
                .getAttributeViewNames();
        if ((attribute.getDescription() == null)
                && attributeViewNames.isEmpty()) {
            return;
        }

        if (attribute.getDescription() != null) {
            final DiagramView parent = this.diagramView;
            menuItem = new JMenuItem("Description...");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    DescriptionViewer.show(JOptionPane
                            .getFrameForComponent(parent), attribute
                            .getDescription());
                }
            });
            popupMenu.add(menuItem);
        }

        if (!attributeViewNames.isEmpty()) {
            addAttributeViewOptions(attributeViewNames, attribute.toString(),
                    popupMenu);
        }
        popupMenu.show(this.diagramView, (int) screenPosition.getX(),
                (int) screenPosition.getY());
    }

    private void addAttributeViewOptions(final List<String> attributeViewNames,
            final String attribute, final JPopupMenu popupMenu) {
        JMenuItem menuItem;
        final Iterator<String> it = attributeViewNames.iterator();
        while (it.hasNext()) {
            final String attributeViewName = it.next();
            menuItem = new JMenuItem(attributeViewName);
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    try {
                        DatabaseViewerManager.showAttribute(attributeViewName,
                                attribute);
                    } catch (final Exception exc) { // we catch any exception
                        // and show it to the user
                        ErrorDialog
                                .showError(
                                        AttributeLabelViewPopupMenuHandler.this.diagramView,
                                        exc,
                                        "Database View Failed",
                                        "The database view could not be shown,\n"
                                                + "possibly due to a misconfiguration.");
                    }
                }
            });
            popupMenu.add(menuItem);
        }
    }
}
