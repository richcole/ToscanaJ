/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.gui.dialog.DescriptionViewer;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.AttributeLabelView;

import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

public class AttributeLabelViewPopupMenuHandler implements EventBrokerListener {
    private DiagramView diagramView;

    public AttributeLabelViewPopupMenuHandler(DiagramView diagramView) {
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
        final FCAElement attribute = labelView.getEntryAtPosition(canvasPosition);
        if (attribute == null) {
            return;
        }
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem menuItem;

        List<String> attributeViewNames = DatabaseViewerManager.getAttributeViewNames();
		if( (attribute.getDescription() == null) && attributeViewNames.isEmpty() ){
			return;
		}
		
        if( attribute.getDescription() != null) {
			final DiagramView parent = this.diagramView;
			menuItem = new JMenuItem("Description...");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DescriptionViewer.show(JOptionPane.getFrameForComponent(parent),attribute.getDescription());
				}
			});
			popupMenu.add(menuItem);
        }	
		
		if (!attributeViewNames.isEmpty()) { 
			addAttributeViewOptions(attributeViewNames, attribute.toString(), popupMenu);
		}
        popupMenu.show(this.diagramView, (int) screenPosition.getX(), (int) screenPosition.getY());
    }

    private void addAttributeViewOptions(List<String> attributeViewNames, final String attribute, JPopupMenu popupMenu) {
        JMenuItem menuItem;
        Iterator<String> it = attributeViewNames.iterator();
        while (it.hasNext()) {
            final String attributeViewName = it.next();
            menuItem = new JMenuItem(attributeViewName);
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	try {
                    	DatabaseViewerManager.showAttribute(attributeViewName, attribute);
                	} catch(Exception exc) { // we catch any exception and show it to the user
                		ErrorDialog.showError(AttributeLabelViewPopupMenuHandler.this.diagramView, exc, "Database View Failed", "The database view could not be shown,\n" +
                										    							"possibly due to a misconfiguration.");
                	}
                }
            });
            popupMenu.add(menuItem);
        }
    }
}
