/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.NodeView;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.canvas.events.CanvasItemDroppedEvent;
import org.tockit.canvas.events.CanvasItemPickupEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

import java.awt.geom.Point2D;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public abstract class SetMovementEventListener implements EventBrokerListener {
    private Point2D startPosition;

	public void processEvent(Event e) {
        CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        NodeView nodeView = (NodeView) dragEvent.getSubject();
        final DiagramNode node = nodeView.getDiagramNode();
        final Point2D toPosition = dragEvent.getCanvasToPosition();
        Point2D fromPosition = dragEvent.getCanvasFromPosition();

        if(e instanceof CanvasItemPickupEvent) {
			this.startPosition = node.getPosition();
		}
		
        double diffX;
        double diffY;
        if( e instanceof CanvasItemPickupEvent ) {
        	// jump onto the grid pos
            diffX = toPosition.getX() - node.getPosition().getX();
            diffY = toPosition.getY() - node.getPosition().getY();
        } else {
        	// move the difference
            diffX = toPosition.getX() - fromPosition.getX();
            diffY = toPosition.getY() - fromPosition.getY();
        }
        final DiagramView diagramView = nodeView.getDiagramView();
        final Diagram2D diagram = diagramView.getDiagram();
        moveSet(diagram, node, diffX, diffY);
        if(!diagram.isHasseDiagram()) {
			moveSet(diagram,node, -diffX, -diffY);        	
        }

        if (dragEvent instanceof CanvasItemDroppedEvent) {
			// on drop we update the screen transform ...
			diagramView.requestScreenTransformUpdate();

		    // ... and add an edit to the undo manager if we find one.
		    UndoManager undoManager = diagramView.getUndoManager();
			if (undoManager != null) {
				// make a copy of the current start position
				final Point2D undoPosition = this.startPosition;
				undoManager.addEdit(new AbstractUndoableEdit() {
					public void undo() throws CannotUndoException {
			            double undoDiffX = undoPosition.getX() - toPosition.getX();
			            double undoDiffY = undoPosition.getY() - toPosition.getY();
						moveSet(diagram, node, undoDiffX, undoDiffY);
						diagramView.requestScreenTransformUpdate();
						diagramView.repaint();
						super.undo();
					}

					public void redo() throws CannotRedoException {
			            double undoDiffX = toPosition.getX() - undoPosition.getX();
			            double undoDiffY = toPosition.getY() - undoPosition.getY();
						moveSet(diagram, node, undoDiffX, undoDiffY);
						diagramView.requestScreenTransformUpdate();
						diagramView.repaint();
						super.redo();
					}
					
					public String getPresentationName() {
						return SetMovementEventListener.this.getPresentationName();
					}
				});
			}
		}
        diagramView.repaint();
    }

    public void moveSet(Diagram2D diagram, DiagramNode node, double diffX, double diffY) {
        for (int i = 0; i < diagram.getNumberOfNodes(); i++) {
            DiagramNode otherNode = diagram.getNode(i);
            if (isPartOfSet(node, otherNode)) {
                Point2D oldPosition = otherNode.getPosition();
                otherNode.setPosition(new Point2D.Double(oldPosition.getX() + diffX, oldPosition.getY() + diffY));
            }
        }
    }

    protected abstract boolean isPartOfSet(DiagramNode node, DiagramNode otherNode);
    
    protected abstract String getPresentationName();
}
