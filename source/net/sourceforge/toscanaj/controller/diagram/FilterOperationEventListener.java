/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.gui.CanvasFeedbackMessage;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import org.tockit.canvas.Canvas;
import org.tockit.canvas.events.CanvasItemActivatedEvent;
import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class FilterOperationEventListener implements EventBrokerListener {
    private DiagramController controller;

    public FilterOperationEventListener(DiagramController controller, EventBroker eventBroker) {
        this.controller = controller;
        eventBroker.subscribe(this, CanvasItemActivatedEvent.class,	NodeView.class);
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
        ConceptInterpreter interpreter = nodeView.getDiagramView().getConceptInterpreter();
        ConceptInterpretationContext context = nodeView.getConceptInterpretationContext();
        int extent = interpreter.getExtentSize(filterConcept, context);
        if(extent != 0) {
        	try {
				controller.next(filterConcept);
        	} catch (RuntimeException exc) {
        		/// @todo create a proper exception for this case
				Canvas canvas = nodeView.getDiagramView();
				new CanvasFeedbackMessage("No further diagram selected", canvas, itemEvent.getCanvasPosition());
        	}
        } else {
        	Canvas canvas = nodeView.getDiagramView();
        	new CanvasFeedbackMessage("No objects would be left", canvas, itemEvent.getCanvasPosition());
        }
    }
}
