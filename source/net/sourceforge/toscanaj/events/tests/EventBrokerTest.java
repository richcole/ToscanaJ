/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.events.tests;

import com.mockobjects.ExpectationCounter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.events.EventBroker;

public class EventBrokerTest extends TestCase {
    public EventBrokerTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(EventBrokerTest.class);
    }

    static class TestListener implements BrokerEventListener {
        public ExpectationCounter eventCounter = new ExpectationCounter("processEvents()");

        public TestListener() {
        }

        public void processEvent(Event e) {
            eventCounter.inc();
        }
    }

    public void testEventBrokerFiltering() {
        try {
            TestListener testListener1 = new TestListener();
            TestListener testListener2 = new TestListener();
            TestListener testListener3 = new TestListener();
            TestListener testListener4 = new TestListener();
            TestListener testListener5 = new TestListener();

            EventBroker mainBroker = new EventBroker();
            String packageName = getClass().getPackage().getName();
            mainBroker.subscribe(testListener1, Class.forName(packageName + ".TestEventType1"), Object.class);
            mainBroker.subscribe(testListener2, Class.forName(packageName + ".TestEventType1"), Integer.class );
            mainBroker.subscribe(testListener3, Class.forName(packageName + ".TestEventType2"), Object.class );
            mainBroker.subscribe(testListener4, Class.forName(packageName + ".TestEventType2"), Integer.class );

            // create a chain: another broker listens to the main one, catching only 1b events. A listener listens
            // to all type 1 events from Integer sources, i.e. it accepts more events but should get only the 1b ones
            EventBroker eventType1BBroker = new EventBroker();
            mainBroker.subscribe(eventType1BBroker, Class.forName(packageName + ".TestEventType1b"), Object.class);
            eventType1BBroker.subscribe(testListener5, Class.forName(packageName + ".TestEventType1"), Integer.class );

            testListener1.eventCounter.setExpected(4);
            testListener2.eventCounter.setExpected(2);
            testListener3.eventCounter.setExpected(2);
            testListener4.eventCounter.setExpected(1);
            testListener5.eventCounter.setExpected(1);

            mainBroker.processEvent(new TestEventType1(new Object()));
            mainBroker.processEvent(new TestEventType1(new Integer(0)));
            mainBroker.processEvent(new TestEventType1b(new Object()));
            mainBroker.processEvent(new TestEventType1b(new Integer(0)));
            mainBroker.processEvent(new TestEventType2(new Object()));
            mainBroker.processEvent(new TestEventType2(new Integer(0)));

            testListener1.eventCounter.verify();
            testListener2.eventCounter.verify();
            testListener3.eventCounter.verify();
            testListener4.eventCounter.verify();
            testListener5.eventCounter.verify();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}