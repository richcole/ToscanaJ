/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.dbviewer.DatabaseViewerException;
import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.database.DatabaseRetrievedObject;
import net.sourceforge.toscanaj.view.diagram.ObjectLabelView;

import org.tockit.canvas.events.CanvasItemActivatedEvent;
import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class ObjectLabelViewOpenDisplayHandler implements EventBrokerListener {

    public ObjectLabelViewOpenDisplayHandler(final EventBroker eventBroker) {
        eventBroker.subscribe(this, CanvasItemActivatedEvent.class,
                ObjectLabelView.class);
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
        ObjectLabelView labelView = null;
        try {
            labelView = (ObjectLabelView) itemEvent.getSubject();
        } catch (final ClassCastException e1) {
            throw new RuntimeException(
                    getClass().getName()
                            + " has to be subscribed to events from ObjectLabelViews only");
        }
        final Object object = labelView.getObjectAtPosition(itemEvent
                .getCanvasPosition());
        if (!(object instanceof DatabaseRetrievedObject)) {
            return;
        }
        final DatabaseRetrievedObject dbObject = (DatabaseRetrievedObject) labelView
                .getObjectAtPosition(itemEvent.getCanvasPosition());
        try {
            showObject(dbObject);
        } catch (final DatabaseViewerException exc) {
            ErrorDialog.showError(null, exc, "Failed to open view",
                    "The object view requested can not be shown.");
        }
    }

    public void showObject(final DatabaseRetrievedObject object)
            throws DatabaseViewerException {
        if (object == null) {
            return;
        }
        DatabaseViewerManager.showObject(0, object);
    }
}
