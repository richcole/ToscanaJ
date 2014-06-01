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
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.canvas.events.CanvasItemDroppedEvent;
import org.tockit.canvas.events.CanvasItemPickupEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

/**
 * Implements node movement in a way that tries to keep chains.
 * 
 * The way this manipulator works is to find all meet-irreducible concepts in
 * the upset and downset of the dragged node's concept. All of them are moved
 * including their downsets.
 */
public class ChainBasedNodeMovementEventListener implements EventBrokerListener {

    private Point2D startPosition;

    public void processEvent(final Event e) {
        CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        NodeView nodeView = (NodeView) dragEvent.getSubject();

        final DiagramView diagramView = nodeView.getDiagramView();
        DiagramNode node = nodeView.getDiagramNode();

        Point2D fromPosition = dragEvent.getCanvasFromPosition();
        if (e instanceof CanvasItemPickupEvent) {
            this.startPosition = node.getPosition();
            fromPosition = node.getPosition(); // can differ from event position if grid is used
        }

        Concept concept = node.getConcept();

        final Set<Concept> meetIrr = ConceptSetHelperFunctions
                .getMeetIrreduciblesInUpset(concept);

        final int numUpperMeetIrr = meetIrr.size();

        // add the meet-irreducible concepts in the downsets of all the
        // meet-irreducibles in the upset
        Set<Concept> newMeetIrr = new HashSet<>();
        for (Concept superConcept : meetIrr) {
            Collection<Object> downset = superConcept.getDownset();
            for (Object object : downset) {
                Concept lower = (Concept) object;
                if (lower.isMeetIrreducible()) {
                    newMeetIrr.add(lower);
                }
            }
        }
        meetIrr.addAll(newMeetIrr);

        ConceptSetHelperFunctions.applyDragToDiagram(
                fromPosition, dragEvent.getCanvasToPosition(),
                diagramView, meetIrr, numUpperMeetIrr);

        if (!diagramView.getDiagram().isHasseDiagram()) {
            ConceptSetHelperFunctions.applyDragToDiagram(
                    fromPosition, dragEvent.getCanvasFromPosition(),
                    diagramView, meetIrr, numUpperMeetIrr);
        }

        if (dragEvent instanceof CanvasItemDroppedEvent) {
            // on drop we update the screen transform ...
            diagramView.requestScreenTransformUpdate();

            // ... and add an edit to the undo manager if we find one.
            final UndoManager undoManager = diagramView.getUndoManager();
            if (undoManager != null) {
                // make a copy of the current start position
                final Point2D undoPosition = this.startPosition;
                // check the actual position of the node -- it might differ from
                // the
                // requested one due to the Hasse diagram limitations
                final Point2D toPosition = nodeView.getPosition();
                undoManager.addEdit(new AbstractUndoableEdit() {
                    @Override
                    public void undo() throws CannotUndoException {
                        ConceptSetHelperFunctions.applyDragToDiagram(
                                toPosition, undoPosition, diagramView, meetIrr,
                                numUpperMeetIrr);
                        diagramView.requestScreenTransformUpdate();
                        diagramView.repaint();
                        super.undo();
                    }

                    @Override
                    public void redo() throws CannotRedoException {
                        ConceptSetHelperFunctions.applyDragToDiagram(
                                undoPosition, toPosition, diagramView, meetIrr,
                                numUpperMeetIrr);
                        diagramView.requestScreenTransformUpdate();
                        diagramView.repaint();
                        super.redo();
                    }

                    @Override
                    public String getPresentationName() {
                        return "Chain movement";
                    }
                });
            }
        }
        diagramView.repaint();
    }
}
