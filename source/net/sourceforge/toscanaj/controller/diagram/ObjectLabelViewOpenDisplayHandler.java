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
import net.sourceforge.toscanaj.canvas.events.CanvasItemEventWithPosition;
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

public class ObjectLabelViewOpenDisplayHandler implements BrokerEventListener {
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
        DatabaseRetrievedObject object = labelView.getObjectAtPosition(itemEvent.getCanvasPosition());
        showObject(object);
    }

    public void showObject(DatabaseRetrievedObject object) {
        if (object == null) {
            return;
        }
        DatabaseViewerManager.showObject(0, object);
    }
}
