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
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

import java.awt.geom.Point2D;

public abstract class SetMovementEventListener implements EventBrokerListener {
    public void processEvent(Event e) {
        CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        NodeView nodeView = (NodeView) dragEvent.getSubject();
        DiagramNode node = nodeView.getDiagramNode();
        Point2D toPosition = dragEvent.getCanvasToPosition();
        Point2D fromPosition = dragEvent.getCanvasFromPosition();
        double diffX = toPosition.getX() - fromPosition.getX();
        double diffY = toPosition.getY() - fromPosition.getY();
        DiagramView diagramView = nodeView.getDiagramView();
        Diagram2D diagram = diagramView.getDiagram();
        moveSet(diagram, node, diffX, diffY);
        if(!diagram.isHasseDiagram()) {
			moveSet(diagram,node, -diffX, -diffY);        	
        }
        if (dragEvent instanceof CanvasItemDroppedEvent) {
            diagramView.requestScreenTransformUpdate();
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
}
