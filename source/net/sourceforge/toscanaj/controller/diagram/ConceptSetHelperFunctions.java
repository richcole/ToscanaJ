/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.view.diagram.DiagramView;


class ConceptSetHelperFunctions {

    static Set getMeetIrreduciblesInUpset(Concept concept) {
        Collection upset = concept.getUpset();
        Set meetIrr = new HashSet();
        for (Iterator iter = upset.iterator(); iter.hasNext();) {
    		Concept upper = (Concept) iter.next();
    		if(upper.isMeetIrreducible()) {
    			meetIrr.add(upper);
    		}
    	}
        return meetIrr;
    }

    static void removeNonMinimals(Set meetIrr) {
        Set removable = new HashSet();
        for (Iterator it1 = meetIrr.iterator(); it1.hasNext();) {
            Concept meetIrreducible = (Concept) it1.next();
            for (Iterator it2 = meetIrr.iterator(); it2.hasNext();) {
                Concept superCandidate = (Concept) it2.next();
                if (meetIrreducible != superCandidate && meetIrreducible.hasSuperConcept(superCandidate)) {
                    removable.add(superCandidate);
                }
            }
        }
        meetIrr.removeAll(removable);
    }

    static void applyDragToDiagram(Point2D canvasFromPosition, Point2D canvasToPosition, DiagramView diagramView, Set conceptsToMove, int distributionFactor) {
        // calculate partial vector
        double dx = canvasToPosition.getX() - canvasFromPosition.getX();
        double dy = canvasToPosition.getY() - canvasFromPosition.getY();
        dx /= distributionFactor;
        dy /= distributionFactor;
        
        // apply it to all downsets of meet-irreducibles
        for (Iterator iter = conceptsToMove.iterator(); iter.hasNext();) {
    		Concept conceptToMove = (Concept) iter.next();
    		for (Iterator iter2 = conceptToMove.getDownset().iterator(); iter2.hasNext();) {
    			Concept downConcept = (Concept) iter2.next();
    			DiagramNode node = diagramView.getDiagram().getNodeForConcept(downConcept);
    			node.setPosition(node.getX() + dx, node.getY() + dy);
    		}
    	}
    }
}
