/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca.events;

import net.sourceforge.toscanaj.events.StandardEvent;

public class ConceptInterpretationContextChangedEvent extends StandardEvent {
    public ConceptInterpretationContextChangedEvent(Object source) {
        super(source);
    }
}
