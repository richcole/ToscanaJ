/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.canvas.events.CanvasItemDraggedEvent;
import net.sourceforge.toscanaj.canvas.events.CanvasItemClickedEvent;
import net.sourceforge.toscanaj.events.*;
import net.sourceforge.toscanaj.view.diagram.*;

public class LabelClickEventHandler implements EventListener {
    public LabelClickEventHandler(EventBroker eventBroker) {
        eventBroker.subscribe(this, CanvasItemClickedEvent.class, LabelView.class);
    }

    public void processEvent(Event e) {
        CanvasItemClickedEvent clickEvent = (CanvasItemClickedEvent) e;
        LabelView labelView = (LabelView) e.getSubject();
        labelView.processClickEvent(clickEvent.getCanvasPosition());
    }
}
