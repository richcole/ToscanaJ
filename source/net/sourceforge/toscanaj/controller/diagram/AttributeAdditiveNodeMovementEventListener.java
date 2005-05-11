/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import java.awt.geom.Point2D;
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
 * smallest change possible. Most noticably the trivial case (only one upper
 * neighbour) breaks down to moving just the dragged node.
 */
public class AttributeAdditiveNodeMovementEventListener implements EventBrokerListener {
	private Point2D startPosition;

    public void processEvent(Event e) {
        CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        NodeView nodeView = (NodeView) dragEvent.getSubject();

        final DiagramView diagramView = nodeView.getDiagramView();
        DiagramNode node = nodeView.getDiagramNode();
        if(e instanceof CanvasItemPickupEvent) {
            this.startPosition = nodeView.getPosition();
        }
        
        Concept concept = node.getConcept();
		
        final Set meetIrr = ConceptSetHelperFunctions.getMeetIrreduciblesInUpset(concept);
        ConceptSetHelperFunctions.removeNonMinimals(meetIrr);
        ConceptSetHelperFunctions.applyDragToDiagram(dragEvent.getCanvasFromPosition(), 
                                                     dragEvent.getCanvasToPosition(),
                                                     diagramView,
                                                     meetIrr, 
                                                     meetIrr.size());
        
        if(!diagramView.getDiagram().isHasseDiagram()) {
            ConceptSetHelperFunctions.applyDragToDiagram(dragEvent.getCanvasToPosition(), 
                                                         dragEvent.getCanvasFromPosition(),
                                                         diagramView,
                                                         meetIrr, 
                                                         meetIrr.size());
        }

        if (dragEvent instanceof CanvasItemDroppedEvent) {
            // on drop we update the screen transform ...
            diagramView.requestScreenTransformUpdate();

            // ... and add an edit to the undo manager if we find one.
            UndoManager undoManager = diagramView.getUndoManager();
            if (undoManager != null) {
                // make a copy of the current start position
                final Point2D undoPosition = this.startPosition;
                // check the actual position of the node -- it might differ from the
                // requested one due to the Hasse diagram limitations
                final Point2D toPosition = nodeView.getPosition();
                undoManager.addEdit(new AbstractUndoableEdit() {
                    public void undo() throws CannotUndoException {
                        ConceptSetHelperFunctions.applyDragToDiagram(toPosition, 
                                                                     undoPosition,
                                                                     diagramView,
                                                                     meetIrr, 
                                                                     meetIrr.size());
                        diagramView.requestScreenTransformUpdate();
                        diagramView.repaint();
                        super.undo();
                    }

                    public void redo() throws CannotRedoException {
                        ConceptSetHelperFunctions.applyDragToDiagram(undoPosition, 
                                                                     toPosition,
                                                                     diagramView,
                                                                     meetIrr, 
                                                                     meetIrr.size());
                        diagramView.requestScreenTransformUpdate();
                        diagramView.repaint();
                        super.redo();
                    }
                    
                    public String getPresentationName() {
                        return "Attribute additive movement";
                    }
                });
            }
        }
        diagramView.repaint();      
	}
}
