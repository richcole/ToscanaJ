/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.events;

/**
 * This interface has to be implemented to listen to events.
 *
 * Each object implementing this interface can subscribe to the EventBroker
 * and will get called on processEvent(Event) whenever an event matching the
 * subscription criteria passes the broker.
 *
 * @see EventBroker.subscribe(EventListener, Class, Class)
 */
public interface EventListener {
    /**
     * The callback for receiving events.
     */
    void processEvent(Event e);
}
