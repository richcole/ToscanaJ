/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.model.database.DatabaseRetrievedObject;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.ObjectLabelView;
import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

public class ObjectLabelViewPopupMenuHandler implements EventBrokerListener {
    private DiagramView diagramView;
    private List queries;

    public ObjectLabelViewPopupMenuHandler(DiagramView diagramView, EventBroker schemaBroker) {
        this.diagramView = diagramView;
        this.queries = null;
        schemaBroker.subscribe(this, ConceptualSchemaChangeEvent.class, Object.class);
    }

    public void processEvent(Event e) {
        if (e instanceof ConceptualSchemaChangeEvent) {
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
        Object object = labelView.getObjectAtPosition(canvasPosition);
        if (! (object instanceof DatabaseRetrievedObject) ) {
            return;
        }
        final DatabaseRetrievedObject dbObject =
                (DatabaseRetrievedObject) object;
        int numberOfQueries = 0;
        if (this.queries != null) {
            numberOfQueries = this.queries.size();
        }
        List objectViewNames = DatabaseViewerManager.getObjectViewNames(dbObject);
        List objectListViewNames = DatabaseViewerManager.getObjectListViewNames(dbObject);
        if (numberOfQueries + objectViewNames.size() + objectListViewNames.size() == 0) { // nothing to display
            return;
        }
        // create the menu
        JPopupMenu popupMenu = new JPopupMenu();
        if (numberOfQueries != 0) {
            addQueryOptions(queries, labelView, popupMenu);
        }
        if (objectViewNames.size() != 0) {
            addObjectViewOptions(objectViewNames, dbObject, popupMenu);
        }
        if (objectListViewNames.size() != 0) {
            addObjectListViewOptions(objectListViewNames, dbObject, popupMenu);
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
        JRadioButtonMenuItem menuItem;
        JMenu queryMenu = new JMenu("Change label");
        Iterator it = queries.iterator();
        while (it.hasNext()) {
            final Query query = (Query) it.next();
            menuItem = new JRadioButtonMenuItem(query.getName(), query.equals(labelView.getQuery()));
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
