/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import org.tockit.events.Event;

public class DisplayedDiagramChangedEvent implements Event<DiagramView> {
    private final DiagramView subject;

    public DisplayedDiagramChangedEvent(final DiagramView subject) {
        this.subject = subject;
    }

    public DiagramView getSubject() {
        return subject;
    }
}
