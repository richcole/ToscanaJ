/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.events;

/**
 * A subscription class for the event broker.
 *
 * This class is used by the EventBroker class to remember which listener
 * is interested in which types of events.
 */
class EventSubscription {
    /**
     * The listener interested in events.
     */
    private BrokerEventListener listener;
    /**
     * The type of event that should be passed.
     */
    private Class eventType;
    /**
     * The filter on sources.
     */
    private Class sourceType;

    /**
     * Creates a new subscription object with the given parameters.
     */
    public EventSubscription(BrokerEventListener listener, Class eventType, Class sourceType) {
        this.listener = listener;
        this.eventType = eventType;
        this.sourceType = sourceType;
    }

    /**
     * Returns the listener that shall receive the events.
     */
    public BrokerEventListener getListener() {
        return listener;
    }

    /**
     * Determines which type of events should be passed to the listener.
     */
    public Class getEventType() {
        return eventType;
    }

    /**
     * Gives the type of sources from which the listener wants to get events.
     */
    public Class getSourceType() {
        return sourceType;
    }
}
