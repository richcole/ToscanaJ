/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.events;

/**
 * A generic event interface.
 *
 * This is used in the EventBroker.
 */
public interface Event {
    /**
     * The source of the event, it must not be null.
     */
    Object getSource();
}
