/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.model.diagram.DiagramNode;

public class FilterMovementEventListener extends SetMovementEventListener {
    protected boolean isPartOfSet(DiagramNode node, DiagramNode otherNode) {
        return node.getConcept().hasSuperConcept(otherNode.getConcept());
    }

	protected String getPresentationName() {
		return "Filter movement";
	}
}
