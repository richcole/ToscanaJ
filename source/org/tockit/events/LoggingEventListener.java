/*
 * Created by IntelliJ IDEA.
 * User: johang
 * Date: 1/08/2002
 * Time: 15:35:40
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.tockit.events;

import java.io.PrintStream;

public class LoggingEventListener implements EventListener {
    private PrintStream printStream;

    public LoggingEventListener(EventBroker eventBroker, Class eventType, Class subjectType, PrintStream printStream) {
        this.printStream = printStream;
        eventBroker.subscribe(this, eventType, subjectType);
    }

    public void processEvent(Event e) {
        printStream.println("Event: " + e.getClass() + "  Subject: " + e.getSubject().getClass());
    }
}
