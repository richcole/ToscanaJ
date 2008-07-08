/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.view.diagram.LabelView;

import org.tockit.canvas.events.CanvasItemClickedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class LabelClickEventHandler implements EventBrokerListener {
    public LabelClickEventHandler(final EventBroker eventBroker) {
        eventBroker.subscribe(this, CanvasItemClickedEvent.class,
                LabelView.class);
    }

    public void processEvent(final Event e) {
        final CanvasItemClickedEvent clickEvent = (CanvasItemClickedEvent) e;
        final LabelView labelView = (LabelView) e.getSubject();
        labelView.processClickEvent(clickEvent.getCanvasPosition());
    }
}
