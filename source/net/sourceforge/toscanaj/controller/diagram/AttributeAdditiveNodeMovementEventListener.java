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
        
        // filter down to the minimal elements
        Set removable = new HashSet();
        for (Iterator it1 = meetIrr.iterator(); it1.hasNext();) {
            Concept concept = (Concept) it1.next();
            for (Iterator it2 = meetIrr.iterator(); it2.hasNext();) {
                Concept superCandidate = (Concept) it2.next();
                if (concept != superCandidate && concept.hasSuperConcept(superCandidate)) {
                    removable.add(superCandidate);
                }
            }
        }
        meetIrr.removeAll(removable);
        
        // calculate partial vector
        double dx = dragEvent.getCanvasToPosition().getX() - dragEvent.getCanvasFromPosition().getX();
        double dy = dragEvent.getCanvasToPosition().getY() - dragEvent.getCanvasFromPosition().getY();
        int numMeetIrr = meetIrr.size();
        dx /= numMeetIrr;
        dy /= numMeetIrr;
        
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
