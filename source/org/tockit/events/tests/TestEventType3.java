/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.events.tests;

public class TestEventType3 implements TestEventInterface {
    private Object source;

    public TestEventType3(Object source) {
        this.source = source;
    }

    public Object getSubject() {
        return source;
    }
}
