/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.canvas.events.CanvasItemDraggedEvent;
import net.sourceforge.toscanaj.events.EventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.NodeView;

import java.awt.geom.Point2D;

public class NodeMovementEventListener implements EventListener {
    public void processEvent(Event e) {
        CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        NodeView nodeView = (NodeView) dragEvent.getSubject();
        DiagramNode node = nodeView.getDiagramNode();
        Point2D toPosition = dragEvent.getCanvasToPosition();
        node.setPosition(toPosition);
        DiagramView diagramView = nodeView.getDiagramView();
        diagramView.requestScreenTransformUpdate();
        diagramView.repaint();
    }
}
