/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.events.tests;

import net.sourceforge.toscanaj.events.Event;

public class TestEventType1 implements Event {
    private Object source;

    public TestEventType1(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
