/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.canvas.events.CanvasItemEvent;
import net.sourceforge.toscanaj.events.EventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.view.diagram.DiagramView;

public class HighlightRemovalOperationEventListener implements EventListener {
    private DiagramView diagramView;

    public HighlightRemovalOperationEventListener(DiagramView diagramView) {
        this.diagramView = diagramView;
    }

    public void processEvent(Event e) {
        CanvasItemEvent itemEvent = null;
        try {
            itemEvent = (CanvasItemEvent) e;
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to CanvasItemEvents only");
        }
        CanvasItem item = null;
        try {
            item = (CanvasItem) itemEvent.getItem();
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to events from CanvasItems only");
        }

        this.diagramView.setSelectedConcepts(null);
    }
}
