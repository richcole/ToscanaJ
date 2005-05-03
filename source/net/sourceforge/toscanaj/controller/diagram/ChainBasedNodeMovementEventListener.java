/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

/**
 * Implements node movement in a way that tries to keep chains.
 * 
 * The way this manipulator works is to find all meet-irreducible concepts
 * in the upset and downset of the dragged node's concept. All of them are
 * moved including their downsets.
 * 
 * @todo it might be better if the behaviour is different when dragging
 * nodes of not meet-irreducibles nodes. In this case one might try applying
 * the algorithm to a set similar to the one calculated for the attribute-
 * additive manipulator (minimal elements in the set of meet-irreducibles
 * in the upset).
 */
public class ChainBasedNodeMovementEventListener implements EventBrokerListener {

	public void processEvent(Event e) {
        CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        NodeView nodeView = (NodeView) dragEvent.getSubject();
		DiagramView diagramView = nodeView.getDiagramView();
        Concept concept = nodeView.getDiagramNode().getConcept();
		
        Set meetIrr = ConceptSetHelperFunctions.getMeetIrreduciblesInUpset(concept);
        
        int numUpperMeetIrr = meetIrr.size();
        
        // add the meet-irreducible concepts in the downsets of all the meet-irreducibles
        // in the upset
        Set newMeetIrr = new HashSet();
        for (Iterator it1 = meetIrr.iterator(); it1.hasNext();) {
            Concept superConcept = (Concept) it1.next();
            Collection downset = superConcept.getDownset();
            for (Iterator it2 = downset.iterator(); it2.hasNext();) {
                Concept lower = (Concept) it2.next();
                if(lower.isMeetIrreducible()) {
                    newMeetIrr.add(lower);
                }
            }
        }
        meetIrr.addAll(newMeetIrr);

        ConceptSetHelperFunctions.applyDragToDiagram(dragEvent, 
                nodeView.getDiagramView(), 
                meetIrr, 
                numUpperMeetIrr);
	}
}
