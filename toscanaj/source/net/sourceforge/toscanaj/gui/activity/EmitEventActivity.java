/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.activity;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;

public class EmitEventActivity implements SimpleActivity {
    private EventBroker broker;
    private Event event;

    public EmitEventActivity(EventBroker broker, Event event) {
        this.broker = broker;
        this.event = event;
    }

    public boolean doActivity() throws Exception {
    	this.broker.processEvent(this.event);
        return true;
    }
}