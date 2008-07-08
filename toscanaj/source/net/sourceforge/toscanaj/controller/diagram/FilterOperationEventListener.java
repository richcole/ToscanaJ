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
    private final DiagramController controller;

    public FilterOperationEventListener(final DiagramController controller,
            final EventBroker eventBroker) {
        this.controller = controller;
        eventBroker.subscribe(this, CanvasItemActivatedEvent.class,
                NodeView.class);
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
        NodeView nodeView = null;
        try {
            nodeView = (NodeView) itemEvent.getSubject();
        } catch (final ClassCastException e1) {
            throw new RuntimeException(getClass().getName()
                    + " has to be subscribed to events from NodeViews only");
        }
        final Concept filterConcept = nodeView.getDiagramNode()
        .getFilterConcept();
        final ConceptInterpreter interpreter = nodeView.getDiagramView()
        .getConceptInterpreter();
        final ConceptInterpretationContext context = nodeView
        .getConceptInterpretationContext();
        final int extent = interpreter.getExtentSize(filterConcept, context);
        if (extent != 0) {
            try {
                this.controller.next(filterConcept);
            } catch (final RuntimeException exc) {
                // / @todo create a proper exception for this case
                final Canvas canvas = nodeView.getDiagramView();
                new CanvasFeedbackMessage("No further diagram selected",
                        canvas, itemEvent.getCanvasPosition());
            }
        } else {
            final Canvas canvas = nodeView.getDiagramView();
            new CanvasFeedbackMessage("No objects would be left", canvas,
                    itemEvent.getCanvasPosition());
        }
    }
}
