/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.view.diagram.LabelView;
import org.tockit.canvas.events.CanvasItemMouseEnterEvent;
import org.tockit.canvas.events.CanvasItemMouseExitEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class LabelScrollEventHandler implements EventBrokerListener {
    public LabelScrollEventHandler(EventBroker eventBroker) {
		eventBroker.subscribe(this, CanvasItemMouseEnterEvent.class, LabelView.class);
		eventBroker.subscribe(this, CanvasItemMouseExitEvent.class, LabelView.class);
    }

    public void processEvent(Event e) {
    	if(e instanceof CanvasItemMouseEnterEvent) {
			CanvasItemMouseEnterEvent enterEvent = (CanvasItemMouseEnterEvent) e;
			LabelView labelView = (LabelView) e.getSubject();
			labelView.showScrollbar(true);
    	} else if (e instanceof CanvasItemMouseExitEvent) {
			CanvasItemMouseExitEvent exitEvent = (CanvasItemMouseExitEvent) e;
			LabelView labelView = (LabelView) e.getSubject();
			labelView.showScrollbar(false);
    	}
    }
}
