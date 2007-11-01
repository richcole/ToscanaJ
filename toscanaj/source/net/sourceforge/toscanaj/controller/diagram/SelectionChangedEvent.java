/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.view.diagram.DiagramView;

import org.tockit.events.StandardEvent;

public class SelectionChangedEvent extends StandardEvent<DiagramView> {
    public SelectionChangedEvent(DiagramView source) {
        super(source);
    }
}
