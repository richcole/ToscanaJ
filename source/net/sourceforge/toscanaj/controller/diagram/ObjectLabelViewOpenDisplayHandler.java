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
import net.sourceforge.toscanaj.view.diagram.ObjectLabelView;

import org.tockit.canvas.events.CanvasItemActivatedEvent;
import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class ObjectLabelViewOpenDisplayHandler implements EventBrokerListener {

	public ObjectLabelViewOpenDisplayHandler(EventBroker eventBroker) {
		eventBroker.subscribe(this, CanvasItemActivatedEvent.class,	ObjectLabelView.class);
	}
	
	
    public void processEvent(Event e) {
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
		Object object = labelView.getObjectAtPosition(itemEvent.getCanvasPosition());
		if(!(object instanceof DatabaseRetrievedObject)) {
			return;        
		}
		DatabaseRetrievedObject dbObject =
				(DatabaseRetrievedObject) labelView.getObjectAtPosition(itemEvent.getCanvasPosition());
		showObject(dbObject);
    }

    public void showObject(DatabaseRetrievedObject object) {
        if (object == null) {
            return;
        }
        DatabaseViewerManager.showObject(0, object);
    }
}
