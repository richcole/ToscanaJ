/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.canvas.events.CanvasItemDraggedEvent;
import net.sourceforge.toscanaj.view.diagram.NodeView;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;

import java.awt.geom.Point2D;

public class NodeMovementEventListener implements BrokerEventListener {
    public void processEvent(Event e) {
        CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        NodeView nodeView = (NodeView) dragEvent.getSource();
        DiagramNode node = nodeView.getDiagramNode();
        Point2D toPosition = dragEvent.getCanvasToPosition();
        node.setPosition(toPosition);
        nodeView.getDiagramView().repaint();
    }
}
