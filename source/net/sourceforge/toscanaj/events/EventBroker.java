/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class EventBroker implements BrokerEventListener {
    private List subscriptions = new ArrayList();

    public EventBroker() {
    }

    public void subscribe(BrokerEventListener listener, Class eventType, Class sourceType) {
        try {
            String packageName = getClass().getPackage().getName();
            Class eventClass = Class.forName(packageName + ".Event");
            if( !implementsInterface(eventType, eventClass) ) {
                throw new RuntimeException("Subscription to class not implementing Event impossible");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Internal error in EventBroker, class Event not found");
        }
        subscriptions.add(new EventSubscription(listener, eventType, sourceType));
    }

    public void removeSubscriptions(BrokerEventListener listener) {
        for (Iterator iterator = subscriptions.iterator(); iterator.hasNext();) {
            EventSubscription subscription = (EventSubscription) iterator.next();
            if( subscription.getListener().equals(listener) ) {
                iterator.remove();
            }
        }
    }

    public void processEvent(Event event) {
        for (Iterator iterator = subscriptions.iterator(); iterator.hasNext();) {
            EventSubscription subscription = (EventSubscription) iterator.next();
            if( extendsOrImplements(event.getClass(), subscription.getEventType()) &&
                    extendsOrImplements(event.getSource().getClass(), subscription.getSourceType()) ) {
                subscription.getListener().processEvent(event);
            }
        }
    }

    private boolean extendsOrImplements(Class subClass, Class superClass) {
        return implementsInterface(subClass, superClass) || extendsClass(subClass, superClass);
    }

    private boolean implementsInterface(Class classType, Class interfaceType) {
        Class curClass = classType;
        while( curClass != null ) {
            if( implementsInterfaceDirectly(curClass, interfaceType) ) {
                return true;
            }
            curClass = curClass.getSuperclass();
        }
        return false;
    }

    private boolean implementsInterfaceDirectly(Class classType, Class interfaceType) {
        Class[] interfaces = classType.getInterfaces();
        for(int i = 0; i < interfaces.length; i++) {
            Class curInterface = interfaces[i];
            if( extendsInterface(curInterface, interfaceType) ) {
                return true;
            }
        }
        return false;
    }

    private boolean extendsInterface(Class subInterface, Class superInterface) {
        if( subInterface.equals(superInterface) ) {
            return true;
        }
        // this gets the super interfaces if we have an interface
        Class[] interfaces = subInterface.getInterfaces();
        for(int i = 0; i < interfaces.length; i++) {
            Class curInterface = interfaces[i];
            if( extendsInterface(curInterface, superInterface) ) {
                return true;
            }
        }
        return false;
    }

    private boolean extendsClass(Class subClass, Class superClass) {
        Class curClass = subClass;
        while( curClass != null ) {
            if( curClass.equals(superClass) ) {
                return true;
            }
            curClass = curClass.getSuperclass();
        }
        return false;
    }
}
