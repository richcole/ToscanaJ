/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.events;

/**
 * This interface has to be implemented to listen to events.
 *
 * Each object implementing this interface can subscribe to the EventBroker
 * and will get called on processEvent(Event) whenever an event matching the
 * subscription criteria passes the broker.
 *
 * @see EventBroker.subscribe(BrokerEventListener, Class, Class)
 */
public interface BrokerEventListener {
    /**
     * The callback for receiving events.
     */
    void processEvent(Event e);
}
