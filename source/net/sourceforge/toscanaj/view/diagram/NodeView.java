package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.view.diagram.ToscanajGraphics2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Point2D;

/**
 * class DiagramNode holds details on node position and size
 */

public class NodeView extends CanvasItem {
    /**
     * The color used for the top of the gradient.
     */
    static protected Color topColor =  new Color(0,0,150);

    /**
     * The colort used for the bottom of the gradient;
     */
    static protected Color bottomColor = new Color(255,255,150);

    /**
     * The Color for the circles around the nodes.
     */
    static protected Color circleColor = new Color(0,0,0);

    /**
     * Store the node model for this view
     */
    private DiagramNode diagramNode = null;

    /**
     * Stores the graphic environment.
     *
     * This is done since the radius in DiagramNode is not stored in model
     * coordinates due to Toscanas weird scaling behaviour.
     *
     * @TODO: Reconsider the handling of the scaling or find a way to store
     * the radius somewhere else (global options?).
     */
    private ToscanajGraphics2D graphics;

    /**
     * Changes the top color of the gradient.
     */
    static public void setTopColor(Color topColor) {
        NodeView.topColor = topColor;
    }

    /**
     * Changes the bottom color of the gradient.
     */
    static public void setBottomColor(Color bottomColor) {
        NodeView.bottomColor = bottomColor;
    }

    /**
     * Changes the color of the circles around the nodes.
     */
    static public void setCircleColor(Color circleColor) {
        NodeView.circleColor = circleColor;
    }

    /**
     * Returns the top color of the gradient.
     */
    static public Color getTopColor() {
        return NodeView.topColor;
    }

    /**
     * Returns the bottom color of the gradient.
     */
    static public Color getBottomColor() {
        return NodeView.bottomColor;
    }

    /**
     * Returns the color of the circles around the nodes.
     */
    static public Color getCircleColor() {
        return NodeView.circleColor;
    }

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
        if(diagramNode != null) {
            Paint oldPaint = g.getGraphics2D().getPaint();
            float rel = (float) this.diagramNode.getConcept().getExtentSizeRelative();
            Color nodeColor = new Color( (int)(topColor.getRed()*rel + bottomColor.getRed()*(1-rel)),
                                         (int)(topColor.getGreen()*rel + bottomColor.getGreen()*(1-rel)),
                                         (int)(topColor.getBlue()*rel + bottomColor.getBlue()*(1-rel)),
                                         (int)(topColor.getAlpha()*rel + bottomColor.getAlpha()*(1-rel)) );
            g.drawFilledEllipse( diagramNode.getPosition(), diagramNode.getRadiusX(), diagramNode.getRadiusY(),
                                 nodeColor, circleColor );
            g.getGraphics2D().setPaint(oldPaint);
            graphics = g;
        }
    }

    /**
     * Implements CanvasItem.containsPoint(Point2D).
     *
     * This is currently not exact if the node is not a circle, the test is if a
     * circle with the geometric average of the two radii is hit.
     */
    public boolean containsPoint(Point2D point) {
        double deltaX = graphics.scaleX(point.getX() - diagramNode.getPosition().getX());
        double deltaY = graphics.scaleY(point.getY() - diagramNode.getPosition().getY());
        double sqDist = deltaX*deltaX + deltaY*deltaY;
        double sqRadius = diagramNode.getRadiusX()*diagramNode.getRadiusY();
        return sqDist <= sqRadius;
    }

    /**
     * Implements CanvasItem.doubleClicked(Point2D) and starts a zooming operation.
     */
    public void doubleClicked(Point2D point) {
        DiagramController.getController().next(diagramNode.getConcept());
    }
}