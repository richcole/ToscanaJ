/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.events.tests;

public class TestEventType3 implements TestEventInterface {
    private Object source;

    public TestEventType3(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
