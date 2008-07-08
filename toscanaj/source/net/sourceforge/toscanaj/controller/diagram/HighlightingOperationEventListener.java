/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import org.tockit.canvas.events.CanvasItemEvent;
import org.tockit.canvas.events.CanvasItemSelectedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class HighlightingOperationEventListener implements EventBrokerListener {
    private final DiagramView diagramView;

    public HighlightingOperationEventListener(final DiagramView diagramView,
            final EventBroker eventBroker) {
        this.diagramView = diagramView;
        eventBroker.subscribe(this, CanvasItemSelectedEvent.class,
                NodeView.class);
    }

    public void processEvent(final Event e) {
        CanvasItemEvent itemEvent = null;
        try {
            itemEvent = (CanvasItemEvent) e;
        } catch (final ClassCastException e1) {
            throw new RuntimeException(getClass().getName()
                    + " has to be subscribed to CanvasItemEvents only");
        }
        NodeView nodeView = null;
        try {
            nodeView = (NodeView) itemEvent.getItem();
        } catch (final ClassCastException e1) {
            throw new RuntimeException(getClass().getName()
                    + " has to be subscribed to events from NodeViews only");
        }

        this.diagramView.setSelectedConcepts(nodeView.getDiagramNode()
                .getConceptNestingList());
    }
}
