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

    static Set<Concept> getMeetIrreduciblesInUpset(Concept concept) {
        Collection<Object> upset = concept.getUpset();
        Set<Concept> meetIrr = new HashSet<Concept>();
        for (Iterator<Object> iter = upset.iterator(); iter.hasNext();) {
    		Concept upper = (Concept) iter.next();
    		if(upper.isMeetIrreducible()) {
    			meetIrr.add(upper);
    		}
    	}
        return meetIrr;
    }

    static void removeNonMinimals(Set<Concept> meetIrr) {
        Set<Concept> removable = new HashSet<Concept>();
        for (Iterator<Concept> it1 = meetIrr.iterator(); it1.hasNext();) {
            Concept meetIrreducible = it1.next();
            for (Iterator<Concept> it2 = meetIrr.iterator(); it2.hasNext();) {
                Concept superCandidate = it2.next();
                if (meetIrreducible != superCandidate && meetIrreducible.hasSuperConcept(superCandidate)) {
                    removable.add(superCandidate);
                }
            }
        }
        meetIrr.removeAll(removable);
    }

    static void applyDragToDiagram(Point2D canvasFromPosition, Point2D canvasToPosition, DiagramView diagramView, Set<Concept> conceptsToMove, int distributionFactor) {
        // calculate partial vector
        double dx = canvasToPosition.getX() - canvasFromPosition.getX();
        double dy = canvasToPosition.getY() - canvasFromPosition.getY();
        dx /= distributionFactor;
        dy /= distributionFactor;
        
        // apply it to all downsets of meet-irreducibles
        for (Iterator<Concept> iter = conceptsToMove.iterator(); iter.hasNext();) {
    		Concept conceptToMove = iter.next();
    		for (Iterator<Object> iter2 = conceptToMove.getDownset().iterator(); iter2.hasNext();) {
    			Concept downConcept = (Concept) iter2.next();
    			DiagramNode node = diagramView.getDiagram().getNodeForConcept(downConcept);
    			node.setPosition(node.getX() + dx, node.getY() + dy);
    		}
    	}
    }
}
