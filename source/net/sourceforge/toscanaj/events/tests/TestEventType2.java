/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.events.tests;

import net.sourceforge.toscanaj.events.Event;

public class TestEventType2 implements Event {
    private Object source;

    public TestEventType2(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
