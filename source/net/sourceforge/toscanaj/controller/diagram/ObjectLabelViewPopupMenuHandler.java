/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.canvas.events.CanvasItemEventWithPosition;
import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.events.*;
import net.sourceforge.toscanaj.model.database.*;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.ObjectLabelView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

public class ObjectLabelViewPopupMenuHandler implements BrokerEventListener {
    private DiagramView diagramView;
    private List queries;

    public ObjectLabelViewPopupMenuHandler(DiagramView diagramView, EventBroker schemaBroker) {
        this.diagramView = diagramView;
        this.queries = null;
        schemaBroker.subscribe(this,ConceptualSchemaChangeEvent.class,Object.class);
    }

    public void processEvent(Event e) {
        if(e instanceof ConceptualSchemaChangeEvent) {
            ConceptualSchemaChangeEvent csce = (ConceptualSchemaChangeEvent) e;
            this.queries = csce.getConceptualSchema().getQueries();
            return;
        }
        CanvasItemEventWithPosition itemEvent = null;
        try {
            itemEvent = (CanvasItemEventWithPosition) e;
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to CanvasItemEventWithPositions only");
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
        if (object == null) {
            return;
        }
        int numberOfQueries = 0;
        if(this.queries != null) {
            numberOfQueries = this.queries.size();
        }
        List objectViewNames = DatabaseViewerManager.getObjectViewNames(object);
        List objectListViewNames = DatabaseViewerManager.getObjectListViewNames(object);
        if (numberOfQueries + objectViewNames.size() + objectListViewNames.size() == 0) { // nothing to display
            return;
        }
        // create the menu
        JPopupMenu popupMenu = new JPopupMenu();
        if (numberOfQueries != 0) {
            addQueryOptions(queries, labelView, popupMenu);
        }
        if (objectViewNames.size() != 0) {
            addObjectViewOptions(objectViewNames, object, popupMenu);
        }
        if (objectListViewNames.size() != 0) {
            addObjectListViewOptions(objectListViewNames, object, popupMenu);
        }
        popupMenu.show(this.diagramView, (int) screenPosition.getX(), (int) screenPosition.getY());
    }

    private void addObjectListViewOptions(List objectListViewNames, final DatabaseRetrievedObject object, JPopupMenu popupMenu) {
        JMenuItem menuItem;
        JMenu objectListViewMenu = new JMenu("View all objects");
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
        JMenu objectViewMenu = new JMenu("View selected");
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
