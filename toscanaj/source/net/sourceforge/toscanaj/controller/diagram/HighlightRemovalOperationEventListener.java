/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.view.diagram.DiagramView;

import org.tockit.canvas.CanvasBackground;
import org.tockit.canvas.events.CanvasItemSelectedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class HighlightRemovalOperationEventListener implements
        EventBrokerListener {
    private final DiagramView diagramView;

    public HighlightRemovalOperationEventListener(
            final DiagramView diagramView, final EventBroker eventBroker) {
        this.diagramView = diagramView;
        eventBroker.subscribe(this, CanvasItemSelectedEvent.class,
                CanvasBackground.class);
    }

    public void processEvent(final Event e) {
        this.diagramView.setSelectedConcepts(null);
    }
}
