package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.NestedDiagramNode;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.view.diagram.ToscanajGraphics2D;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * class DiagramNode holds details on node position and size
 */

public class NodeView extends CanvasItem {
    /**
     * Node displays nothing selected
     */
    static final public int NOT_SELECTED = 0;

    /**
     * Node displays the currently selected concept.
     */
    static final public int SELECTED_DIRECTLY = 1;

    /**
     * Node displays a concept in the filter of the currently selected concept.
     */
    static final public int SELECTED_FILTER = 2;

    /**
     * Node displays a concept in the ideal of the currently selected concept.
     */
    static final public int SELECTED_IDEAL = 4;

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
     * The Color for the circles around the node with the selected concept.
     */
    static public Color circleSelectionColor = new Color(80,80,80);

    /**
     * The Color for the circles around the nodes in the ideal of the selected
     * concept.
     */
    static public Color circleIdealColor = new Color(255,255,0);

    /**
     * The Color for the circles around the nodes in the filter of the selected
     * concept.
     */
    static public Color circleFilterColor = new Color(255,0,0);

    /**
     * The size of the circles around selected nodes.
     */
    static public int selectionSize = 3;

    /**
     * Store the node model for this view
     */
    private DiagramNode diagramNode = null;

    /**
     * Store the diagram view for callbacks on selected concepts.
     */
    private DiagramView diagramView = null;

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
     * Stores the selection state.
     */
    private int selectionState;

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
     * Construct a nodeView for a Node.
     *
     * The DiagramView is used for the callback when a node was selected.
     */
    public NodeView(DiagramNode diagramNode, DiagramView diagramView){
        this.diagramNode = diagramNode;
        this.diagramView = diagramView;
    }

    /**
     * Draws the node as circle.
     */
    public void draw(ToscanajGraphics2D g) {
        if(diagramNode != null) {
            Paint oldPaint = g.getGraphics2D().getPaint();
            Color nodeColor;
            Color circleColor = this.circleColor;
            if(diagramNode instanceof NestedDiagramNode) {
                nodeColor = Color.white;
            }
            else {
                float rel = (float) this.diagramNode.getConcept().getExtentSizeRelative();
                nodeColor = new Color( (int)(topColor.getRed()*rel + bottomColor.getRed()*(1-rel)),
                                       (int)(topColor.getGreen()*rel + bottomColor.getGreen()*(1-rel)),
                                       (int)(topColor.getBlue()*rel + bottomColor.getBlue()*(1-rel)),
                                       (int)(topColor.getAlpha()*rel + bottomColor.getAlpha()*(1-rel)) );
            }
            Stroke oldStroke = g.getGraphics2D().getStroke();
            if(this.selectionState != NOT_SELECTED) {
                g.getGraphics2D().setStroke(new BasicStroke(this.selectionSize));
                if(this.selectionState == SELECTED_DIRECTLY) {
                    circleColor = this.circleSelectionColor;
                }
                else if(this.selectionState == SELECTED_IDEAL) {
                    circleColor = this.circleIdealColor;
                }
                else if(this.selectionState == SELECTED_FILTER) {
                    circleColor = this.circleFilterColor;
                }
            }
            g.drawFilledEllipse( diagramNode.getPosition(), diagramNode.getRadiusX(), diagramNode.getRadiusY(),
                                 nodeColor, circleColor );
            g.getGraphics2D().setPaint(oldPaint);
            g.getGraphics2D().setStroke(oldStroke);
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
     * Selects the diagam view of the selected concept.
     */
    public void clicked(Point2D point) {
        this.diagramView.setSelectedConcept(this.diagramNode.getConcept());
    }

    /**
     * Implements CanvasItem.doubleClicked(Point2D) and starts a zooming operation.
     */
    public void doubleClicked(Point2D point) {
        DiagramController.getController().next(diagramNode.getConcept());
    }

    /**
     * Calculates the rectangle around this node.
     */
    public Rectangle2D getBounds(ToscanajGraphics2D g) {
        Point2D center = g.project(this.diagramNode.getPosition());
        double x = center.getX();
        double y = center.getY();
        double rx = this.diagramNode.getRadiusX();
        double ry = this.diagramNode.getRadiusY();
        return new Rectangle2D.Double(x-rx, y-ry, 2*rx, 2*ry);
    }

    /**
     * Recalculates if the node is selected and how.
     *
     * @see getSelectionState()
     */
    public void setSelectedConcept(Concept concept) {
        if(concept == null) {
            this.selectionState = NOT_SELECTED;
            return;
        }
        if(this.diagramNode.getConcept() == concept) {
            this.selectionState = SELECTED_DIRECTLY;
            return;
        }
        if(this.diagramNode.getConcept().hasSuperConcept(concept)) {
            this.selectionState = SELECTED_IDEAL;
            return;
        }
        if(this.diagramNode.getConcept().hasSubConcept(concept)) {
            this.selectionState = SELECTED_FILTER;
            return;
        }
        this.selectionState = NOT_SELECTED;
        return;
    }

    /**
     * Returns the information if and how the node is selected.
     *
     * The return values are:
     * - NOT_SELECTED: the node dispalys a concept which is neither in filter
     *   nor ideal of the selected concept
     * - SELECTED_DIRECTLY: the node dispalys the selected concept
     * - SELECTED_IN_FILTER: the node dispalys a concept which is in the filter
     *   of the selected concept
     * - SELECTED_IN_IDEAL: the node dispalys a concept which is in the ideal
     *   of the selected concept
     *
     * @see setSelectedConcept(Concept)
     */
    public int getSelectionState() {
        return this.selectionState;
    }
}