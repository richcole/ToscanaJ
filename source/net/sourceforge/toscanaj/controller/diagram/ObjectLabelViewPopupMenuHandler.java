/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.canvas.events.CanvasItemEvent;
import net.sourceforge.toscanaj.canvas.events.CanvasItemContextMenuRequestEvent;
import net.sourceforge.toscanaj.view.diagram.NodeView;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.ObjectLabelView;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.NestedDiagramNode;
import net.sourceforge.toscanaj.model.database.DatabaseRetrievedObject;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.database.DatabaseQuery;
import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.geom.Point2D;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ObjectLabelViewPopupMenuHandler implements BrokerEventListener {
    private DiagramView diagramView;

    public ObjectLabelViewPopupMenuHandler(DiagramView diagramView) {
        this.diagramView = diagramView;
    }

    public void processEvent(Event e) {
        CanvasItemContextMenuRequestEvent itemEvent = null;
        try {
            itemEvent = (CanvasItemContextMenuRequestEvent) e;
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to CanvasItemContextMenuRequestEvents only");
        }
        ObjectLabelView labelView = null;
        try {
            labelView = (ObjectLabelView) itemEvent.getItem();
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to events from ObjectLabelViews only");
        }
        openPopupMenu(labelView, itemEvent.getCanvasPosition(), itemEvent.getAWTPosition());
    }

    public void openPopupMenu(final ObjectLabelView labelView, Point2D canvasPosition, Point2D screenPosition) {
        final DatabaseRetrievedObject object = labelView.getObjectAtPosition(canvasPosition);
        if( object == null ) {
            return;
        }
        List queries = Query.getQueries();
        List objectViewNames = DatabaseViewerManager.getObjectViewNames(object);
        List objectListViewNames = DatabaseViewerManager.getObjectListViewNames(object);
        if (queries.size() + objectViewNames.size() + objectListViewNames.size() == 0) { // nothing to display
            return;
        }
        // create the menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem;
        if (queries.size() != 0) {
            addQueryOptions(queries, labelView, popupMenu);
        }
        if (objectViewNames.size() != 0) {
            addObjectViewOptions(objectViewNames, object, popupMenu);
        }
        if (objectListViewNames.size() != 0) {
            addObjectListViewOptions(objectListViewNames, object, popupMenu);
        }
        popupMenu.show(this.diagramView, (int)screenPosition.getX(), (int)screenPosition.getY());
    }

    private void addObjectListViewOptions(List objectListViewNames, final DatabaseRetrievedObject object, JPopupMenu popupMenu) {
        JMenuItem menuItem;
        JMenu objectListViewMenu = new JMenu("View summary");
        Iterator it = objectListViewNames.iterator();
        while (it.hasNext()) {
            final String objectListViewName = (String) it.next();
            menuItem = new JMenuItem(objectListViewName);
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    DatabaseViewerManager.showObjectList(objectListViewName, object);
                }
            });
            objectListViewMenu.add(menuItem);
        }
        popupMenu.add(objectListViewMenu);
    }

    private void addObjectViewOptions(List objectViewNames, final DatabaseRetrievedObject object, JPopupMenu popupMenu) {
        JMenuItem menuItem;
        JMenu objectViewMenu = new JMenu("View object");
        Iterator it = objectViewNames.iterator();
        while (it.hasNext()) {
            final String objectViewName = (String) it.next();
            menuItem = new JMenuItem(objectViewName);
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    DatabaseViewerManager.showObject(objectViewName, object);
                }
            });
            objectViewMenu.add(menuItem);
        }
        popupMenu.add(objectViewMenu);
    }

    private void addQueryOptions(List queries, final ObjectLabelView labelView, JPopupMenu popupMenu) {
        JMenuItem menuItem;
        JMenu queryMenu = new JMenu("Change label");
        Iterator it = queries.iterator();
        while (it.hasNext()) {
            final DatabaseQuery query = (DatabaseQuery) it.next();
            menuItem = new JMenuItem(query.getName());
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    labelView.setQuery(query);
                }
            });
            queryMenu.add(menuItem);
        }
        popupMenu.add(queryMenu);
    }
}
