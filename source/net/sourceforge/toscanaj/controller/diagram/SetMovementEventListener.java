/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.canvas.events.CanvasItemDraggedEvent;
import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import java.awt.geom.Point2D;

public abstract class SetMovementEventListener implements BrokerEventListener {
    public void processEvent(Event e) {
        CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        NodeView nodeView = (NodeView) dragEvent.getSource();
        DiagramNode node = nodeView.getDiagramNode();
        Point2D toPosition = dragEvent.getCanvasToPosition();
        Point2D fromPosition = dragEvent.getCanvasFromPosition();
        double diffX = toPosition.getX() - fromPosition.getX();
        double diffY = toPosition.getY() - fromPosition.getY();
        DiagramView diagramView = nodeView.getDiagramView();
        Diagram2D diagram = diagramView.getDiagram();
        for (int i = 0; i < diagram.getNumberOfNodes(); i++) {
            DiagramNode otherNode = diagram.getNode(i);
            if (isPartOfSet(node, otherNode)) {
                Point2D oldPosition = otherNode.getPosition();
                otherNode.setPosition(new Point2D.Double(oldPosition.getX() + diffX, oldPosition.getY() + diffY));
            }
        }
        diagramView.requestScreenTransformUpdate();
        diagramView.repaint();
    }

    protected abstract boolean isPartOfSet(DiagramNode node, DiagramNode otherNode);
}
