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
        if(diagramNode != null) {
            Paint oldPaint = g.getGraphics2D().getPaint();
            float rel = (float) this.diagramNode.getConcept().getExtentSizeRelative();
            Color c1 = new Color(0,0,150);
            Color c2 = new Color(255,255,150);
            Color circleColor = new Color(0,0,0);
            Color nodeColor = new Color( (int)(c1.getRed()*rel + c2.getRed()*(1-rel)),
                                         (int)(c1.getGreen()*rel + c2.getGreen()*(1-rel)),
                                         (int)(c1.getBlue()*rel + c2.getBlue()*(1-rel)),
                                         (int)(c1.getAlpha()*rel + c2.getAlpha()*(1-rel)) );
            g.drawCircle( diagramNode.getPosition(), diagramNode.getRadius(),
                          nodeColor, circleColor );
            g.getGraphics2D().setPaint(oldPaint);
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
     * Tells the filter controller to add the nodes concept to
     */
    public void doubleClicked(Point2D point) {
        DiagramController.getController().next(diagramNode.getConcept());
    }
}