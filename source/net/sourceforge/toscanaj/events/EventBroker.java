/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A class distributing events to listeners.
 *
 * This is the central class of the Tockit event processing model. It takes
 * new events on the processEvent(Event) method and distributes them to
 * event listeners that are subscribed to this broker using
 * subscribe(EventListener, Class, Class).
 *
 * On subscription one can give two possible filter options, one to specify
 * which type of events one is interested in, the other specifies the type
 * of object one cares about. An example of usage would be subscribing to
 * only selection events on rectangles on a canvas.
 *
 * Brokers can subscribe to other brokers since they implement the listener
 * interface. This way a set of event handling contexts can be created where
 * events are passed around. Different components in a system can have their
 * own broker while one could add an additional one on the meta level
 * subscribing to the specific one and thus giving a meta-context where events
 * from different components can be observed.
 *
 * To cause processing of an event one just calls processEvent(Event) with
 * a new object implementing the Event interface. This will put the event at
 * the end of an event queue which will instantly processed. This means events
 * are processed synchronously if they are not enqueued while other events are
 * processed. If you enqueue a new event while another is still being processed
 * (i.e. as reaction on the first event), the new event will be processed after
 * the processing of the first one has finished.
 */
public class EventBroker implements EventListener {
    private class SubscriptionEvent extends StandardEvent {
        public SubscriptionEvent(Object subject) {
            super(subject);
        }
    }

    private class SubscriptionRemovalEvent extends StandardEvent {
        public SubscriptionRemovalEvent(Object subject) {
            super(subject);
        }
    }

    /**
     * Stores the list of subscriptions.
     */
    private List subscriptions = new ArrayList();

    /**
     * The event queue.
     */
    private List eventQueue = new LinkedList();

    /**
     * True if we are already processing some events.
     */
    private boolean processingEvents = false;

    /**
     * We store the package of this class as constant since Class.getPackage()
     * does not always work.
     */
    private static final String PACKAGE_NAME = "net.sourceforge.toscanaj.events";

    /**
     * Creates a new broker.
     */
    public EventBroker() {
    }

    /**
     * Subscribes the listener to a set of specific events.
     *
     * After subscription the listener will receive every event
     * extending or implementing the given eventType (which can
     * be a class or an interface) which involves a subject that
     * extends or implements the given subject type (given as class
     * or interface).
     */
    public void subscribe(EventListener listener, Class eventType, Class subjectType) {
        try {
            Class eventInterface = Class.forName(PACKAGE_NAME + ".Event");
            if (!eventInterface.isAssignableFrom(eventType)) {
                throw new RuntimeException("Subscription to class not implementing Event impossible");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Internal error in EventBroker, class Event not found");
        }
        this.eventQueue.add(
                new SubscriptionEvent(
                        new EventSubscription(listener, eventType, subjectType)));
        processEvents();
    }

    /**
     * Removes all subscriptions the listener has.
     *
     * Afterwards the listener will not receive any events anymore.
     */
    public void removeSubscriptions(EventListener listener) {
        for (Iterator iterator = subscriptions.iterator(); iterator.hasNext();) {
            EventSubscription subscription = (EventSubscription) iterator.next();
            if (subscription.getListener().equals(listener)) {
                this.eventQueue.add(new SubscriptionRemovalEvent(subscription));
            }
        }
        processEvents();
    }

    /**
     * Distributes a new event to the listeners.
     *
     * The given event will be sent to all listeners that are interested in this
     * type of event from the given subject.
     *
     * If the subject of the event (Event.getSource()) is not defined (i.e. null) a
     * RuntimeException will be thrown.
     *
     * @todo check if we really want this limitation, a null subject could just be sent
     *       to all listeners of the event type. This is consistent if one sees null as
     *       universally typed.
     */
    public void processEvent(Event event) {
        if (event.getSubject() == null) {
            throw new RuntimeException("Event needs subject to be processed, null not allowed.");
        }
        this.eventQueue.add(event);
        processEvents();
    }


    /**
     * Processes the current event queue until it is empty.
     */
    private void processEvents() {
        if (processingEvents) {
            return;
        }
        processingEvents = true;
        while (!eventQueue.isEmpty()) {
            Event event = (Event) eventQueue.remove(0);
            if (event instanceof SubscriptionEvent) {
                this.subscriptions.add(event.getSubject());
            } else if (event instanceof SubscriptionRemovalEvent) {
                this.subscriptions.remove(event.getSubject());
            }
            processExternalEvent(event);
        }
        processingEvents = false;
    }

    private void processExternalEvent(Event event) {
        for (Iterator iterator = subscriptions.iterator(); iterator.hasNext();) {
            EventSubscription subscription = (EventSubscription) iterator.next();
            if (subscription.getEventType().isAssignableFrom(event.getClass()) &&
                    subscription.getSubjectType().isAssignableFrom(event.getSubject().getClass())) {
                subscription.getListener().processEvent(event);
            }
        }
    }
}
