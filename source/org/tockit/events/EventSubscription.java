/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.events;

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
    private EventListener listener;
    /**
     * The type of event that should be passed.
     */
    private Class eventType;
    /**
     * The filter on subjects.
     */
    private Class subjectType;

    /**
     * Creates a new subscription object with the given parameters.
     */
    public EventSubscription(EventListener listener, Class eventType, Class subjectType) {
        this.listener = listener;
        this.eventType = eventType;
        this.subjectType = subjectType;
    }

    /**
     * Returns the listener that shall receive the events.
     */
    public EventListener getListener() {
        return listener;
    }

    /**
     * Determines which type of events should be passed to the listener.
     */
    public Class getEventType() {
        return eventType;
    }

    /**
     * Gives the type of subjects from which the listener wants to get events.
     */
    public Class getSubjectType() {
        return subjectType;
    }
}
