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

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.database.DatabaseRetrievedObject;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.ObjectLabelView;

import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class ObjectLabelViewPopupMenuHandler implements EventBrokerListener {
    private final DiagramView diagramView;
    private List<Query> queries;

    public ObjectLabelViewPopupMenuHandler(final DiagramView diagramView,
            final EventBroker schemaBroker) {
        this.diagramView = diagramView;
        this.queries = null;
        schemaBroker.subscribe(this, ConceptualSchemaChangeEvent.class,
                Object.class);
    }

    public void processEvent(final Event e) {
        if (e instanceof ConceptualSchemaChangeEvent) {
            final ConceptualSchemaChangeEvent csce = (ConceptualSchemaChangeEvent) e;
            this.queries = csce.getConceptualSchema().getQueries();
            return;
        }
        CanvasItemEventWithPosition itemEvent = null;
        try {
            itemEvent = (CanvasItemEventWithPosition) e;
        } catch (final ClassCastException e1) {
            throw new RuntimeException(
                    getClass().getName()
                            + " has to be subscribed to CanvasItemEventWithPositions only");
        }
        ObjectLabelView labelView = null;
        try {
            labelView = (ObjectLabelView) itemEvent.getItem();
        } catch (final ClassCastException e1) {
            throw new RuntimeException(
                    getClass().getName()
                            + " has to be subscribed to events from ObjectLabelViews only");
        }
        openPopupMenu(labelView, itemEvent.getCanvasPosition(), itemEvent
                .getAWTPosition());
    }

    public void openPopupMenu(final ObjectLabelView labelView,
            final Point2D canvasPosition, final Point2D screenPosition) {
        final Object object = labelView.getObjectAtPosition(canvasPosition);
        if (object == null) {
            return;
        }

        int numberOfQueries = 0;
        if (this.queries != null) {
            numberOfQueries = this.queries.size();
        }

        final int numberOfViews = 0;
        List<String> objectViewNames = null;
        List<String> objectListViewNames = null;
        if (object instanceof DatabaseRetrievedObject) {
            final DatabaseRetrievedObject dbObject = (DatabaseRetrievedObject) object;
            objectViewNames = DatabaseViewerManager
                    .getObjectViewNames(dbObject);
            objectListViewNames = DatabaseViewerManager
                    .getObjectListViewNames();
        }

        if (numberOfQueries + numberOfViews == 0) { // nothing to display
            return;
        }
        // create the menu
        final JPopupMenu popupMenu = new JPopupMenu();
        if (numberOfQueries != 0) {
            addQueryOptions(labelView, popupMenu);
        }
        if (object instanceof DatabaseRetrievedObject) {
            assert objectViewNames != null; // should be initialized in
                                            // equivalent if above
            assert objectListViewNames != null; // should be initialized in
                                                // equivalent if above
            final DatabaseRetrievedObject dbObject = (DatabaseRetrievedObject) object;
            if (objectViewNames.size() != 0) {
                addObjectViewOptions(objectViewNames, dbObject, popupMenu);
            }
            if (objectListViewNames.size() != 0) {
                addObjectListViewOptions(objectListViewNames, dbObject,
                        popupMenu);
            }
        }
        popupMenu.show(this.diagramView, (int) screenPosition.getX(),
                (int) screenPosition.getY());
    }

    private void addObjectListViewOptions(
            final List<String> objectListViewNames,
            final DatabaseRetrievedObject object, final JPopupMenu popupMenu) {
        JMenuItem menuItem;
        final JMenu objectListViewMenu = new JMenu("View all objects");
        final Iterator<String> it = objectListViewNames.iterator();
        while (it.hasNext()) {
            final String objectListViewName = it.next();
            menuItem = new JMenuItem(objectListViewName);
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    try {
                        DatabaseViewerManager.showObjectList(
                                objectListViewName, object);
                    } catch (final Throwable t) {
                        ErrorDialog
                                .showError(
                                        ObjectLabelViewPopupMenuHandler.this.diagramView,
                                        t, "Database View Failed",
                                        "Opening the database view failed.");
                    }
                }
            });
            objectListViewMenu.add(menuItem);
        }
        popupMenu.add(objectListViewMenu);
    }

    private void addObjectViewOptions(final List<String> objectViewNames,
            final DatabaseRetrievedObject object, final JPopupMenu popupMenu) {
        JMenuItem menuItem;
        final JMenu objectViewMenu = new JMenu("View selected");
        final Iterator<String> it = objectViewNames.iterator();
        while (it.hasNext()) {
            final String objectViewName = it.next();
            menuItem = new JMenuItem(objectViewName);
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    try {
                        DatabaseViewerManager
                                .showObject(objectViewName, object);
                    } catch (final Throwable t) {
                        ErrorDialog
                                .showError(
                                        ObjectLabelViewPopupMenuHandler.this.diagramView,
                                        t, "Database View Failed",
                                        "Opening the database view failed.");
                    }
                }
            });
            objectViewMenu.add(menuItem);
        }
        popupMenu.add(objectViewMenu);
    }

    private void addQueryOptions(final ObjectLabelView labelView,
            final JPopupMenu popupMenu) {
        JRadioButtonMenuItem menuItem;
        final JMenu queryMenu = new JMenu("Change label");
        final Iterator<Query> it = this.queries.iterator();
        while (it.hasNext()) {
            final Query query = it.next();
            menuItem = new JRadioButtonMenuItem(query.getName(), query
                    .equals(labelView.getQuery()));
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    labelView.setQuery(query);
                }
            });
            queryMenu.add(menuItem);
        }
        popupMenu.add(queryMenu);
    }
}
