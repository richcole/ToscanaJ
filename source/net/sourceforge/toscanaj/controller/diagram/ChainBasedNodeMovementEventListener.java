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

import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.canvas.events.CanvasItemDroppedEvent;
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
		
        // find meet-irreducibles in the upset 
        Collection upset = nodeView.getDiagramNode().getConcept().getUpset();
        Set meetIrr = new HashSet();
        for (Iterator iter = upset.iterator(); iter.hasNext();) {
			Concept upper = (Concept) iter.next();
			if(upper.isMeetIrreducible()) {
				meetIrr.add(upper);
			}
		}
        
        // calculate partial vector based on MIs in the upset -- they all affect movement
        // of the dragged node, the ones in the downset don't
        double dx = dragEvent.getCanvasToPosition().getX() - dragEvent.getCanvasFromPosition().getX();
        double dy = dragEvent.getCanvasToPosition().getY() - dragEvent.getCanvasFromPosition().getY();
        int numUpperMeetIrr = meetIrr.size();
        dx /= numUpperMeetIrr;
        dy /= numUpperMeetIrr;
        
        // add the concepts in the downset 
        Collection downset = nodeView.getDiagramNode().getConcept().getDownset();
        for (Iterator iter = downset.iterator(); iter.hasNext();) {
            Concept lower = (Concept) iter.next();
            if(lower.isMeetIrreducible()) {
                meetIrr.add(lower);
            }
        }
        
        // apply it to all downsets of meet-irreducibles
        for (Iterator iter = meetIrr.iterator(); iter.hasNext();) {
			Concept concept = (Concept) iter.next();
			for (Iterator iter2 = concept.getDownset().iterator(); iter2.hasNext();) {
				Concept downConcept = (Concept) iter2.next();
				DiagramNode node = diagramView.getDiagram().getNodeForConcept(downConcept);
				node.setPosition(node.getX() + dx, node.getY() + dy);
			}
		}
        
        if (dragEvent instanceof CanvasItemDroppedEvent) {
			// on drop we update the screen transform ...
			diagramView.requestScreenTransformUpdate();
        }
        diagramView.repaint();
	}
}
