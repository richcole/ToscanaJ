package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.view.diagram.ToscanajGraphics2D;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 * class DiagramNode holds details on node position and size
 */

public class NodeView extends CanvasItem {

    /**
     * Store the node model for this view
     */
    private DiagramNode diagramNode = null;

    /**
     * Construct a nodeView for a Node
     */
    public NodeView(DiagramNode diagramNode){
        this.diagramNode = diagramNode;
    }

    public void draw(ToscanajGraphics2D g) {
        ///@TODO Probably should throw a NodeNotFoundException
        if(diagramNode != null) {
            g.drawEllipse2D(diagramNode.getPosition(), diagramNode.getRadius());
        }
    }

    /**
     * Returns always false at the moment.
     *
     * @TODO: implement correct behaviour.
     */
    public boolean containsPoint(Point2D point) {
        return false;
    }

    /**
     * A node is not currently moveable.
     */
    public void moveBy(double deltaX, double deltaY) {
    }
}