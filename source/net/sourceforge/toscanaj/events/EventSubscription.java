/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.events;


class EventSubscription {
    private BrokerEventListener listener;
    private Class eventType;
    private Class sourceType;

    public EventSubscription(BrokerEventListener listener, Class eventType, Class sourceType) {
        this.listener = listener;
        this.eventType = eventType;
        this.sourceType = sourceType;
    }

    public BrokerEventListener getListener() {
        return listener;
    }

    public Class getEventType() {
        return eventType;
    }

    public Class getSourceType() {
        return sourceType;
    }
}
