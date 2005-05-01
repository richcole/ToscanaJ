/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import java.util.Set;

import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

/**
 * Implements node movement in a way that ensures attribute-additivity.
 * 
 * Here is the basic idea:
 * - identify all meet-irreducibles in the upset of the dragged node's concept
 * - find the minimal elements in this
 * - distribute the movement along the nodes of these concepts, moving 
 *   all downsets with them
 * 
 * The trick is that this way the movement is restricted to the interval of the
 * dragged node and the join of the upper neighbours, which is in some way the
 * smallest change possible. Most noticable the trivial case (only one upper
 * neighbour) breaks down to moving just the dragged node.
 */
public class AttributeAdditiveNodeMovementEventListener implements EventBrokerListener {
	public void processEvent(Event e) {
        CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        NodeView nodeView = (NodeView) dragEvent.getSubject();
        Concept concept = nodeView.getDiagramNode().getConcept();
		
        Set meetIrr = ConceptSetHelperFunctions.getMeetIrreduciblesInUpset(concept);
        ConceptSetHelperFunctions.removeNonMinimals(meetIrr);
        ConceptSetHelperFunctions.applyDragToDiagram(dragEvent, 
                                                     nodeView.getDiagramView(), 
                                                     meetIrr, 
                                                     meetIrr.size());
	}
}
