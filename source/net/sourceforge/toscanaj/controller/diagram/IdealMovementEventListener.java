/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.canvas.events.CanvasItemDraggedEvent;
import net.sourceforge.toscanaj.view.diagram.NodeView;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;

import java.awt.geom.Point2D;
import java.util.Iterator;

public class IdealMovementEventListener extends SetMovementEventListener {

    protected boolean isPartOfSet(DiagramNode node, DiagramNode otherNode) {
        return node.getConcept().hasSubConcept(otherNode.getConcept());
    }
}
