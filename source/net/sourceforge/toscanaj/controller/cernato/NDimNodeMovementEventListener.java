/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.cernato;

import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagram;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagramNode;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.NodeView;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.canvas.events.CanvasItemDroppedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

import java.awt.geom.Point2D;
import java.util.Iterator;

public class NDimNodeMovementEventListener implements EventBrokerListener {
    public void processEvent(Event e) {
        CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        NodeView nodeView = (NodeView) dragEvent.getSubject();
        DiagramView diagramView = nodeView.getDiagramView();
        NDimDiagram diagram = (NDimDiagram) diagramView.getDiagram();
        DiagramNode node = nodeView.getDiagramNode();
        if (!(node instanceof NDimDiagramNode)) {
            throw new RuntimeException("NDimNodeMovementEventListener usable only for NDimDiagramNodes");
        }
        if (node.getConcept().getUpset().size() == 1) {
        	return; // we don't move the top node
        }
        NDimDiagramNode ndimNode = (NDimDiagramNode) node;
        Point2D toPos = dragEvent.getCanvasToPosition();
        Point2D curPos = ndimNode.getPosition();
        double diffX = toPos.getX() - curPos.getX();
        double diffY = toPos.getY() - curPos.getY();
        moveNodes(diagram, ndimNode, diffX, diffY);
        if(!diagram.isHasseDiagram()) {
            moveNodes(diagram, ndimNode, -diffX, -diffY);
        }
        if (dragEvent instanceof CanvasItemDroppedEvent) {
            diagramView.requestScreenTransformUpdate();
        }
        diagramView.repaint();
    }

    public void moveNodes(
        NDimDiagram diagram,
        NDimDiagramNode ndimNode,
        double diffX,
        double diffY) {
        int[] diffUpperNeighbours = findUpperNeighbourDiff(diagram, ndimNode);
        int numDiffs = 0;
        for (int i = 0; i < diffUpperNeighbours.length; i++) {
            numDiffs += diffUpperNeighbours[i];
        }
        Iterator baseIt = diagram.getBase().iterator();
        for (int i = 0; i < diffUpperNeighbours.length; i++) {
			Point2D baseVec = (Point2D) baseIt.next();
			if(ndimNode.getNdimVector()[i] == 0) {
				continue;
			}
            double relDiffI = diffUpperNeighbours[i] / (double) numDiffs;
            baseVec.setLocation(baseVec.getX() + diffX * relDiffI / ndimNode.getNdimVector()[i],
                    baseVec.getY() + diffY * relDiffI / ndimNode.getNdimVector()[i]);
        }
    }

    private int[] findUpperNeighbourDiff(Diagram2D diagram, NDimDiagramNode node) {
        double[] nodeVec = node.getNdimVector();
        int[] retVal = new int[nodeVec.length];
        Iterator it = diagram.getLines();
        while (it.hasNext()) {
            DiagramLine line = (DiagramLine) it.next();
            if (line.getToNode() == node) {
                NDimDiagramNode upperNeighbour = (NDimDiagramNode) line.getFromNode();
                double[] upperVec = upperNeighbour.getNdimVector();
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
