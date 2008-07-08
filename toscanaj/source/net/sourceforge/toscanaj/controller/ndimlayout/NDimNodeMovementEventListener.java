/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.ndimlayout;

import java.awt.geom.Point2D;
import java.util.Iterator;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagram;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagramNode;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.canvas.events.CanvasItemDroppedEvent;
import org.tockit.canvas.events.CanvasItemPickupEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

public class NDimNodeMovementEventListener implements EventBrokerListener {
    private Point2D startPosition;

    public void processEvent(final Event e) {
        final CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        final NodeView nodeView = (NodeView) dragEvent.getSubject();
        final DiagramView diagramView = nodeView.getDiagramView();
        final DiagramNode node = nodeView.getDiagramNode();
        if (!(node instanceof NDimDiagramNode)) {
            throw new RuntimeException(
                    "NDimNodeMovementEventListener usable only for NDimDiagramNodes");
        }
        if (node.getConcept().getUpset().size() == 1) {
            return; // we don't move the top node
        }
        final NDimDiagramNode ndimNode = (NDimDiagramNode) node;
        final NDimDiagram diagram = (NDimDiagram) diagramView.getDiagram();
        final Point2D toPosition = dragEvent.getCanvasToPosition();
        final Point2D curPosition = ndimNode.getPosition();

        if (e instanceof CanvasItemPickupEvent) {
            this.startPosition = curPosition;
        }

        final double diffX = toPosition.getX() - curPosition.getX();
        final double diffY = toPosition.getY() - curPosition.getY();
        moveNodes(diagram, ndimNode, diffX, diffY);
        if (!diagram.isHasseDiagram()) {
            moveNodes(diagram, ndimNode, -diffX, -diffY);
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
                        final double undoDiffX = undoPosition.getX()
                                - toPosition.getX();
                        final double undoDiffY = undoPosition.getY()
                                - toPosition.getY();
                        moveNodes(diagram, ndimNode, undoDiffX, undoDiffY);
                        diagramView.requestScreenTransformUpdate();
                        diagramView.repaint();
                        super.undo();
                    }

                    @Override
                    public void redo() throws CannotRedoException {
                        final double undoDiffX = toPosition.getX()
                                - undoPosition.getX();
                        final double undoDiffY = toPosition.getY()
                                - undoPosition.getY();
                        moveNodes(diagram, ndimNode, undoDiffX, undoDiffY);
                        diagramView.requestScreenTransformUpdate();
                        diagramView.repaint();
                        super.redo();
                    }

                    @Override
                    public String getPresentationName() {
                        return "Attribute Additive Movement";
                    }
                });
            }
        }
        diagramView.repaint();
    }

    public void moveNodes(final NDimDiagram diagram,
            final NDimDiagramNode ndimNode, final double diffX,
            final double diffY) {
        final int[] diffUpperNeighbours = findUpperNeighbourDiff(diagram,
                ndimNode);
        int numDiffs = 0;
        for (final int diffUpperNeighbour : diffUpperNeighbours) {
            numDiffs += diffUpperNeighbour;
        }
        final Iterator<Point2D> baseIt = diagram.getBase().iterator();
        for (int i = 0; i < diffUpperNeighbours.length; i++) {
            final Point2D baseVec = baseIt.next();
            if (ndimNode.getNdimVector()[i] == 0) {
                continue;
            }
            final double relDiffI = diffUpperNeighbours[i] / (double) numDiffs;
            baseVec.setLocation(baseVec.getX() + diffX * relDiffI
                    / ndimNode.getNdimVector()[i], baseVec.getY() + diffY
                    * relDiffI / ndimNode.getNdimVector()[i]);
        }
    }

    private int[] findUpperNeighbourDiff(final Diagram2D diagram,
            final NDimDiagramNode node) {
        final double[] nodeVec = node.getNdimVector();
        final int[] retVal = new int[nodeVec.length];
        final Iterator<DiagramLine> it = diagram.getLines();
        while (it.hasNext()) {
            final DiagramLine line = it.next();
            if (line.getToNode() == node) {
                final NDimDiagramNode upperNeighbour = (NDimDiagramNode) line
                        .getFromNode();
                final double[] upperVec = upperNeighbour.getNdimVector();
                for (int i = 0; i < upperVec.length; i++) {
                    if (upperVec[i] < nodeVec[i]) {
                        retVal[i] = 1;
                    }
                }
            }
        }
        return retVal;
    }
}
