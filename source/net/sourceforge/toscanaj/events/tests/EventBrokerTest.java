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

            EventBroker mainBroker = new EventBroker();
            String packageName = getClass().getPackage().getName();
            mainBroker.subscribe(testListener1, Class.forName(packageName + ".TestEventType1"), Object.class);
            mainBroker.subscribe(testListener2, Class.forName(packageName + ".TestEventType2"), Integer.class );

            testListener1.eventCounter.setExpected(3);
            testListener2.eventCounter.setExpected(1);

            mainBroker.processEvent(new TestEventType1(new Object()));
            mainBroker.processEvent(new TestEventType1(new Integer(0)));
            mainBroker.processEvent(new TestEventType1b(new Object()));
            mainBroker.processEvent(new TestEventType2(new Object()));
            mainBroker.processEvent(new TestEventType2(new Integer(0)));

            testListener1.eventCounter.verify();
            testListener2.eventCounter.verify();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
