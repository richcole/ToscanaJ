/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.events;

import java.util.*;

/**
 * A class distributing events to listeners.
 *
 * This is the central class of the Tockit event processing model. It takes
 * new events on the processEvent(Event) method and distributes them to
 * event listeners that are subscribed to this broker using
 * subscribe(BrokerEventListener, Class, Class).
 *
 * On subscription one can give two possible filter options, one to specify
 * which type of events one is interested in, the other specifies the type
 * of source. An example of usage would be subscribing to only selection
 * events on rectangles on a canvas.
 *
 * Brokers can subscribe to other brokers since they implement the listener
 * interface. This way a set of event handling contexts can be created where
 * events are passed around. Different components in a system can have their
 * own broker while one could add an additional one on the meta level
 * subscribing to the specific one and thus giving a meta-context where events
 * from different components can be observed.
 *
 * To cause processing of an event one just calls processEvent(Event) with
 * a new object implementing the Event interface. Currently this still happens
 * synchronously, i.e. all listeners to this type of event will be called
 * before the method returns.
 *
 * Note: if subscription changes are requested while events are still processed,
 * the changes will only occur after the current event processing has been
 * finished, but before the next event after that will be processed.
 *
 * @todo probably it would be a better idea to process events one by one, i.e.
 * caching new events until the old ones have been processed, which means adding
 * an event queue to this class.
 */
public class EventBroker implements BrokerEventListener {
    /**
     * Stores the list of subscriptions.
     */
    private List subscriptions = new ArrayList();

    /**
     * Stores new subscriptions which shall be added to the subsription list.
     *
     * This is needed if a new subscription is made while an event is processed,
     * otherwise a ConcurrentModificationException will be thrown.
     */
    private List newSubscriptions = new ArrayList();

    /**
     * Stores subscriptions which shall be removed from the subsription list.
     *
     * This is needed if a new subscription is removed while an event is processed,
     * otherwise a ConcurrentModificationException will be thrown.
     */
    private List subscriptionsToRemove = new ArrayList();

    /**
     * This counts the number of iterators accessing the subscriptions list.
     *
     * This is used to avoid accessing the subscription list while someone is still iterating.
     */
    private int numberOfSubscriptionIterators = 0;

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
     * be a class or an interface) which comes from a source that
     * extends or implements the given source type (given as class
     * or interface).
     */
    public void subscribe(BrokerEventListener listener, Class eventType, Class sourceType) {
        try {
            Class eventClass = Class.forName(PACKAGE_NAME + ".Event");
            if (!implementsInterface(eventType, eventClass)) {
                throw new RuntimeException("Subscription to class not implementing Event impossible");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Internal error in EventBroker, class Event not found");
        }
        newSubscriptions.add(new EventSubscription(listener, eventType, sourceType));
    }

    /**
     * Removes all subscriptions the listener has.
     *
     * Afterwards the listener will not receive any events anymore.
     */
    public void removeSubscriptions(BrokerEventListener listener) {
        updateSubscriptions();
        numberOfSubscriptionIterators++;
        for (Iterator iterator = subscriptions.iterator(); iterator.hasNext();) {
            EventSubscription subscription = (EventSubscription) iterator.next();
            if (subscription.getListener().equals(listener)) {
                subscriptionsToRemove.add(subscription);
            }
        }
        numberOfSubscriptionIterators--;
    }

    /**
     * Adds the new subscriptions to the main list.
     */
    private void updateSubscriptions() {
        if(numberOfSubscriptionIterators == 0) {
            subscriptions.addAll(newSubscriptions);
            newSubscriptions.clear();
            subscriptions.removeAll(subscriptionsToRemove);
            subscriptionsToRemove.clear();
        }
    }

    /**
     * Distributes a new event to the listeners.
     *
     * The given event will be sent to all listeners that are interested in this
     * type of event from the given source.
     *
     * If the source of the event (Event.getSource()) is not defined (i.e. null) a
     * RuntimeException will be thrown.
     */
    public void processEvent(Event event) {
        updateSubscriptions();
        if (event.getSource() == null) {
            throw new RuntimeException("Event needs source to be processed, null not allowed.");
        }
        numberOfSubscriptionIterators++;
        for (Iterator iterator = subscriptions.iterator(); iterator.hasNext();) {
            EventSubscription subscription = (EventSubscription) iterator.next();
            if (extendsOrImplements(event.getClass(), subscription.getEventType()) &&
                    extendsOrImplements(event.getSource().getClass(), subscription.getSourceType())) {
                subscription.getListener().processEvent(event);
            }
        }
        numberOfSubscriptionIterators--;
    }

    /**
     * Checks if a class or interface does derive from another.
     *
     * Returns true in three situations:
     * * the first parameter is a class extending the class given as second
     *   parameter (directly or indirectly)
     * * the first paramater is a class implementing the interface given by
     *   the second parameter (directly or indirectly)
     * * the first parameter is an interface which extends the interface
     *   given by the second parameter (directly or indirectly)
     *
     * In all other cases the return value is false.
     */
    private boolean extendsOrImplements(Class subClass, Class superClass) {
        return implementsInterface(subClass, superClass) || extendsClass(subClass, superClass);
    }

    /**
     * Returns true if the second parameter is an interface implemented by
     * the former.
     *
     * This is true in any of these cases:
     * * the first paramater is a class implementing the interface given by
     *   the second parameter (directly or indirectly)
     * * the first parameter is an interface which extends the interface
     *   given by the second parameter (directly or indirectly)
     */
    private boolean implementsInterface(Class classType, Class interfaceType) {
        Class curClass = classType;
        while (curClass != null) {
            if (implementsInterfaceDirectly(curClass, interfaceType)) {
                return true;
            }
            curClass = curClass.getSuperclass();
        }
        return false;
    }

    /**
     * Returns true if an interface is implemented directly.
     *
     * This means either the first parameter is a class which is declared to have
     * the second parameter itself as interface (and not a subtype) or if the
     * first parameter is an interface deriving directly from the latter.
     */
    private boolean implementsInterfaceDirectly(Class classType, Class interfaceType) {
        Class[] interfaces = classType.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            Class curInterface = interfaces[i];
            if (extendsInterface(curInterface, interfaceType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true iff the first parameter is an interface extending the second.
     *
     * This is treated as transitive (i.e. subinterfaces of subinterfaces are accepted)
     * and reflexive (an interface is considered to extend itself).
     */
    private boolean extendsInterface(Class subInterface, Class superInterface) {
        if (subInterface.equals(superInterface)) {
            return true;
        }
        // this gets the super interfaces if we have an interface
        Class[] interfaces = subInterface.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            Class curInterface = interfaces[i];
            if (extendsInterface(curInterface, superInterface)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true iff the first parameter is a class extending the second.
     *
     * This is treated as transitive (i.e. subclasses of subclasses are accepted)
     * and reflexive (a class is considered to extend itself).
     */
    private boolean extendsClass(Class subClass, Class superClass) {
        Class curClass = subClass;
        while (curClass != null) {
            if (curClass.equals(superClass)) {
                return true;
            }
            curClass = curClass.getSuperclass();
        }
        return false;
    }
}
