/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import java.awt.geom.Point2D;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.canvas.events.CanvasItemDroppedEvent;
import org.tockit.canvas.events.CanvasItemPickupEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

public class NodeMovementEventListener implements EventBrokerListener {
    private Point2D startPosition;

    public void processEvent(final Event e) {
        final CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        final NodeView nodeView = (NodeView) dragEvent.getSubject();
        final DiagramView diagramView = nodeView.getDiagramView();
        final Point2D canvasToPosition = dragEvent.getCanvasToPosition();
        final DiagramNode node = nodeView.getDiagramNode();
        final Point2D oldPosition = node.getPosition();

        if (e instanceof CanvasItemPickupEvent) {
            this.startPosition = oldPosition;
        }

        node.setPosition(canvasToPosition);

        if (!diagramView.getDiagram().isHasseDiagram()) {
            node.setPosition(oldPosition);
        }

        if (dragEvent instanceof CanvasItemDroppedEvent) {
            // on drop we update the screen transform ...
            diagramView.requestScreenTransformUpdate();

            // ... and add an edit to the undo manager if we find one.
            final UndoManager undoManager = diagramView.getUndoManager();
            if (undoManager != null) {
                // make a copy of the current start position
                final Point2D undoPosition = this.startPosition;
                undoManager.addEdit(new AbstractUndoableEdit() {
                    @Override
                    public void undo() throws CannotUndoException {
                        node.setPosition(undoPosition);
                        diagramView.requestScreenTransformUpdate();
                        diagramView.repaint();
                        super.undo();
                    }

                    @Override
                    public void redo() throws CannotRedoException {
                        node.setPosition(canvasToPosition);
                        diagramView.requestScreenTransformUpdate();
                        diagramView.repaint();
                        super.redo();
                    }

                    @Override
                    public String getPresentationName() {
                        return "Node movement";
                    }
                });
            }
        }
        diagramView.repaint();
    }
}
