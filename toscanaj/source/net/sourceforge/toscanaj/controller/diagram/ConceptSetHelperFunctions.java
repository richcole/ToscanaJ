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

    static Set<Concept> getMeetIrreduciblesInUpset(final Concept concept) {
        final Collection<Object> upset = concept.getUpset();
        final Set<Concept> meetIrr = new HashSet<Concept>();
        for (final Object object : upset) {
            final Concept upper = (Concept) object;
            if (upper.isMeetIrreducible()) {
                meetIrr.add(upper);
            }
        }
        return meetIrr;
    }

    static void removeNonMinimals(final Set<Concept> meetIrr) {
        final Set<Concept> removable = new HashSet<Concept>();
        for (final Concept meetIrreducible : meetIrr) {
            for (final Concept superCandidate : meetIrr) {
                if (meetIrreducible != superCandidate
                        && meetIrreducible.hasSuperConcept(superCandidate)) {
                    removable.add(superCandidate);
                }
            }
        }
        meetIrr.removeAll(removable);
    }

    static void applyDragToDiagram(final Point2D canvasFromPosition,
            final Point2D canvasToPosition, final DiagramView diagramView,
            final Set<Concept> conceptsToMove, final int distributionFactor) {
        // calculate partial vector
        double dx = canvasToPosition.getX() - canvasFromPosition.getX();
        double dy = canvasToPosition.getY() - canvasFromPosition.getY();
        dx /= distributionFactor;
        dy /= distributionFactor;

        // apply it to all downsets of meet-irreducibles
        for (final Concept conceptToMove : conceptsToMove) {
            for (final Iterator<Object> iter2 = conceptToMove.getDownset()
                    .iterator(); iter2.hasNext();) {
                final Concept downConcept = (Concept) iter2.next();
                final DiagramNode node = diagramView.getDiagram()
                        .getNodeForConcept(downConcept);
                node.setPosition(node.getX() + dx, node.getY() + dy);
            }
        }
    }
}
