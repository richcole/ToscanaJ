/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.NodeView;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.canvas.events.CanvasItemDroppedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

import java.awt.geom.Point2D;

public class NodeMovementEventListener implements EventBrokerListener {
    public void processEvent(Event e) {
        CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        NodeView nodeView = (NodeView) dragEvent.getSubject();
        DiagramView diagramView = nodeView.getDiagramView();

        DiagramNode node = nodeView.getDiagramNode();
        Point2D oldPosition = node.getPosition();
        Point2D toPosition = dragEvent.getCanvasToPosition();
        node.setPosition(toPosition);
        
        if(!diagramView.getDiagram().isHasseDiagram()) {
        	node.setPosition(oldPosition);
        }

        if (dragEvent instanceof CanvasItemDroppedEvent) {
            diagramView.requestScreenTransformUpdate();
        }
        diagramView.repaint();
    }
}
