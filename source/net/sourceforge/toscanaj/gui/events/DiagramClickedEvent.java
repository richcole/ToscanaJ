/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.events;

import net.sourceforge.toscanaj.model.diagram.Diagram2D;

import org.tockit.events.Event;

public class DiagramClickedEvent implements Event {
	private Diagram2D diagram;
	
	public DiagramClickedEvent(Diagram2D diagram) {
		this.diagram = diagram;
	}
	
	public Object getSubject() {
		return this.diagram;
	}
	
	public Diagram2D getDiagram() {
		return this.diagram;
	}
}
