/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.events.*;
import net.sourceforge.toscanaj.view.diagram.*;

public class LabelDragEventHandler implements EventListener {
    public LabelDragEventHandler(EventBroker eventBroker) {
        eventBroker.subscribe(this, CanvasItemDraggedEvent.class, LabelView.class);
    }

    public void processEvent(Event e) {
        CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        LabelView labelView = (LabelView) e.getSubject();
        labelView.processDragEvent(dragEvent.getCanvasFromPosition(), dragEvent.getCanvasToPosition());
    }
}
