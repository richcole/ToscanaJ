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
     * Stores the grpahic environment.
     *
     * This is done since the radius in DiagramNode is not stored in model
     * coordinates due to Toscanas weird scaling behaviour.
     *
     * @TODO: Reconsider the handling of the scaling or find a way to store
     * the radius somewhere else (global options?).
     */
    private ToscanajGraphics2D graphics;

    /**
     * Construct a nodeView for a Node
     */
    public NodeView(DiagramNode diagramNode){
        this.diagramNode = diagramNode;
    }

    /**
     * Draws the node as circle.
     */
    public void draw(ToscanajGraphics2D g) {
        ///@TODO Probably should throw a NodeNotFoundException
        if(diagramNode != null) {
            g.drawEllipse2D(diagramNode.getPosition(), diagramNode.getRadius());
            graphics = g;
        }
    }

    /**
     * Returns always false at the moment.
     */
    public boolean containsPoint(Point2D point) {
        double deltaX = graphics.scaleX(point.getX() - diagramNode.getPosition().getX());
        double deltaY = graphics.scaleY(point.getY() - diagramNode.getPosition().getY());
        double sqDist = deltaX*deltaX + deltaY*deltaY;
        double sqRadius = diagramNode.getRadius()*diagramNode.getRadius();
        return sqDist <= sqRadius;
    }

    /**
     * A node is not currently moveable.
     */
    public void moveBy(double deltaX, double deltaY) {
    }
}