/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.diagram;

import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.canvas.events.CanvasItemEvent;
import net.sourceforge.toscanaj.view.diagram.NodeView;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.NestedDiagramNode;

import java.util.List;
import java.util.ArrayList;

public class HighlightingOperationEventListener implements BrokerEventListener {
    private DiagramView diagramView;

    public HighlightingOperationEventListener(DiagramView diagramView) {
        this.diagramView = diagramView;
    }

    public void processEvent(Event e) {
        CanvasItemEvent itemEvent = null;
        try {
            itemEvent = (CanvasItemEvent) e;
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to CanvasItemEvents only");
        }
        NodeView nodeView = null;
        try {
            nodeView = (NodeView) itemEvent.getItem();
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to events from NodeViews only");
        }

        List conceptList = new ArrayList();
        DiagramNode node = nodeView.getDiagramNode();
        if (node instanceof NestedDiagramNode) {
            NestedDiagramNode ndNode = (NestedDiagramNode) node;
            node = ndNode.getInnerDiagram().getNode(0);
        }
        while (node != null) {
            conceptList.add(node.getConcept());
            node = node.getOuterNode();
        }
        this.diagramView.setSelectedConcepts(conceptList);
    }
}
