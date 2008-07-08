/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.view.diagram.LabelView;

import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.canvas.events.CanvasItemDroppedEvent;
import org.tockit.canvas.events.CanvasItemPickupEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class LabelDragEventHandler implements EventBrokerListener {
    public LabelDragEventHandler(final EventBroker eventBroker) {
        eventBroker.subscribe(this, CanvasItemDraggedEvent.class,
                LabelView.class);
    }

    public void processEvent(final Event e) {
        final CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        final LabelView labelView = (LabelView) e.getSubject();
        if (e instanceof CanvasItemPickupEvent) {
            labelView.startDrag(dragEvent.getCanvasFromPosition(), dragEvent
                    .getCanvasToPosition());
        } else {
            labelView.processDragEvent(dragEvent.getCanvasFromPosition(),
                    dragEvent.getCanvasToPosition(),
                    dragEvent instanceof CanvasItemDroppedEvent);
        }
    }
}
