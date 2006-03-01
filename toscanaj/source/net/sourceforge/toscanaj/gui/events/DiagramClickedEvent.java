/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.events;

import net.sourceforge.toscanaj.controller.fca.DiagramReference;

import org.tockit.events.Event;

public class DiagramClickedEvent implements Event {
	private DiagramReference diagramReference;
	
	public DiagramClickedEvent(DiagramReference diagramReference) {
		this.diagramReference = diagramReference;
	}
	
	public Object getSubject() {
		return this.diagramReference;
	}
	
	public DiagramReference getDiagramReference() {
		return this.diagramReference;
	}
}
