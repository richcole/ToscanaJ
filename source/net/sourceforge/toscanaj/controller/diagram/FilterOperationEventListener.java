/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.gui.CanvasFeedbackMessage;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import org.tockit.canvas.Canvas;
import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

public class FilterOperationEventListener implements EventBrokerListener {
    private DiagramController controller;

    public FilterOperationEventListener(DiagramController controller) {
        this.controller = controller;
    }

    public void processEvent(Event e) {
        CanvasItemEventWithPosition itemEvent = null;
        try {
            itemEvent = (CanvasItemEventWithPosition) e;
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to CanvasItemEventWithPositions only");
        }
        NodeView nodeView = null;
        try {
            nodeView = (NodeView) itemEvent.getItem();
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to events from NodeViews only");
        }
        Concept filterConcept = nodeView.getDiagramNode().getFilterConcept();
        if(filterConcept.getExtentSize() != 0) {
        	controller.next(filterConcept);
        } else {
        	Canvas canvas = nodeView.getDiagramView();
        	new CanvasFeedbackMessage("No objects would be left", canvas, itemEvent.getCanvasPosition());
        }
    }
}
