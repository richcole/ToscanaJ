package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.NestedDiagramNode;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.view.diagram.DiagramSchema;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * class DiagramNode holds details on node position and size
 */

public class NodeView extends CanvasItem {
    /**
     * Currently we don't use selection.
     */
    static final public int NO_SELECTION = -1;

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
     * The amount of fade out for unselected nodes.
     */
    static public float fadeOut = 0.7F;

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
     * Stores the selection state.
     *
     * @see getSelectionState()
     */
    private int selectionState = NO_SELECTION;

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
    public void draw(Graphics2D graphics) {
        if(diagramNode == null) {
            return;
        }
        DiagramSchema diagramSchema = DiagramSchema.getDiagramSchema();
        Paint oldPaint = graphics.getPaint();
        Color nodeColor;
        Color circleColor = diagramSchema.getCircleColor();
        if(diagramNode instanceof NestedDiagramNode) {
            nodeColor = diagramSchema.getNestedDiagramNodeColor();
        }
        else {
            Color topColor = diagramSchema.getTopColor();
            Color bottomColor = diagramSchema.getBottomColor();
            float rel = (float) this.diagramNode.getConcept().getExtentSizeRelative();
            nodeColor = new Color( (int)(topColor.getRed()*rel + bottomColor.getRed()*(1-rel)),
                                   (int)(topColor.getGreen()*rel + bottomColor.getGreen()*(1-rel)),
                                   (int)(topColor.getBlue()*rel + bottomColor.getBlue()*(1-rel)),
                                   (int)(topColor.getAlpha()*rel + bottomColor.getAlpha()*(1-rel)) );
        }
        Stroke oldStroke = graphics.getStroke();
        if(this.selectionState != NO_SELECTION) {
            if(this.selectionState == SELECTED_DIRECTLY) {
                graphics.setStroke(new BasicStroke(this.selectionSize));
                circleColor = diagramSchema.getCircleSelectionColor();
            }
            else if(this.selectionState == SELECTED_IDEAL) {
                graphics.setStroke(new BasicStroke(this.selectionSize));
                circleColor = diagramSchema.getCircleIdealColor();
            }
            else if(this.selectionState == SELECTED_FILTER) {
                graphics.setStroke(new BasicStroke(this.selectionSize));
                circleColor = diagramSchema.getCircleFilterColor();
            }
            else if(this.selectionState == NOT_SELECTED) {
                // lighten
                float rel = fadeOut;
                nodeColor = new Color( (int)(nodeColor.getRed()*(1-rel) + 255*rel),
                                       (int)(nodeColor.getGreen()*(1-rel) + 255*rel),
                                       (int)(nodeColor.getBlue()*(1-rel) + 255*rel),
                                       (int)(nodeColor.getAlpha()*(1-rel) + 255*rel) );
                circleColor = new Color( (int)(circleColor.getRed()*(1-rel) + 255*rel),
                                         (int)(circleColor.getGreen()*(1-rel) + 255*rel),
                                         (int)(circleColor.getBlue()*(1-rel) + 255*rel),
                                         (int)(circleColor.getAlpha()*(1-rel) + 255*rel) );
            }
        }

        Ellipse2D ellipse = new Ellipse2D.Double(
                                    diagramNode.getPosition().getX() - diagramNode.getRadiusX(),
                                    diagramNode.getPosition().getY() - diagramNode.getRadiusY(),
                                    diagramNode.getRadiusX() * 2, diagramNode.getRadiusY() * 2 );
        graphics.setPaint(nodeColor);
        graphics.fill(ellipse);
        graphics.setPaint(circleColor);
        graphics.draw(ellipse);
        graphics.setPaint(oldPaint);
        graphics.setStroke(oldStroke);
    }

    /**
     * Implements CanvasItem.containsPoint(Point2D).
     *
     * This is currently not exact if the node is not a circle, the test is if a
     * circle with the geometric average of the two radii is hit.
     */
    public boolean containsPoint(Point2D point) {
        double deltaX = point.getX() - diagramNode.getPosition().getX();
        double deltaY = point.getY() - diagramNode.getPosition().getY();
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
    public Rectangle2D getBounds(Graphics2D g) {
        Point2D center = this.diagramNode.getPosition();
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
            this.selectionState = NO_SELECTION;
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
     * - NO_SELECTION: we don't have a selection at the moment
     * - NOT_SELECTED: the node displays a concept which is neither in filter
     *   nor ideal of the selected concept
     * - SELECTED_DIRECTLY: the node displays the selected concept
     * - SELECTED_IN_FILTER: the node displays a concept which is in the filter
     *   of the selected concept
     * - SELECTED_IN_IDEAL: the node displays a concept which is in the ideal
     *   of the selected concept
     *
     * @see setSelectedConcept(Concept)
     */
    public int getSelectionState() {
        return this.selectionState;
    }

    /**
     * Overwritten to avoid raising nodes with diagrams in them/
     */
    public boolean hasAutoRaise() {
        return !(this.diagramNode instanceof NestedDiagramNode);
    }
}