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
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagramNode;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.NodeView;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.canvas.events.CanvasItemDroppedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventListener;

import java.awt.geom.Point2D;
import java.util.Iterator;

public class NDimNodeMovementEventListener implements EventListener {
    public void processEvent(Event e) {
        CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        NodeView nodeView = (NodeView) dragEvent.getSubject();
        DiagramView diagramView = nodeView.getDiagramView();
        Diagram2D diagram = diagramView.getDiagram();
        DiagramNode node = nodeView.getDiagramNode();
        if (!(node instanceof NDimDiagramNode)) {
            throw new RuntimeException("NDimNodeMovementEventListener usable only for NDimDiagramNodes");
        }
        NDimDiagramNode ndimNode = (NDimDiagramNode) node;
        Point2D toPos = dragEvent.getCanvasToPosition();
        Point2D curPos = ndimNode.getPosition();
        double diffX = toPos.getX() - curPos.getX();
        double diffY = toPos.getY() - curPos.getY();
        double[] diffUpperNeighbours = findUpperNeighbourDiff(diagram, ndimNode);
        double sumCoord = 0;
        for (int i = 0; i < diffUpperNeighbours.length; i++) {
            sumCoord += diffUpperNeighbours[i];
        }
        Iterator baseIt = ndimNode.getBase().iterator();
        for (int i = 0; i < diffUpperNeighbours.length; i++) {
            double v = diffUpperNeighbours[i];
            Point2D baseVec = (Point2D) baseIt.next();
            baseVec.setLocation(baseVec.getX() + diffX * v / (sumCoord + ndimNode.getNdimVector()[i]),
                    baseVec.getY() + diffY * v / (sumCoord + ndimNode.getNdimVector()[i]));
        }
        if (dragEvent instanceof CanvasItemDroppedEvent) {
            diagramView.requestScreenTransformUpdate();
        }
        diagramView.repaint();
    }

    private double[] findUpperNeighbourDiff(Diagram2D diagram, NDimDiagramNode node) {
        double[] nodeVec = node.getNdimVector();
        double[] retVal = new double[nodeVec.length];
        Iterator it = diagram.getLines();
        while (it.hasNext()) {
            DiagramLine line = (DiagramLine) it.next();
            if (line.getToNode() == node) {
                NDimDiagramNode upperNeighbour = (NDimDiagramNode) line.getFromNode();
                double[] upperVec = upperNeighbour.getNdimVector();
                for (int i = 0; i < upperVec.length; i++) {
                    double v = upperVec[i];
                    if (v < nodeVec[i]) {
                        retVal[i] = 1;
                    }
                }
            }
        }
        return retVal;
    }
}
