/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.events;

import net.sourceforge.toscanaj.model.diagram.Diagram2D;

import org.tockit.events.Event;

public class DiagramChangedEvent implements Event {
    private Diagram2D diagram;
    private Object source;

    public DiagramChangedEvent(Object source, Diagram2D diagram) {
        this.diagram = diagram;
        this.source = source;
    }

    public Diagram2D getDiagram() {
        return diagram;
    }

    public Object getSubject() {
        return source;
    }
}
