/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.NestedDiagramNode;
import net.sourceforge.toscanaj.model.lattice.Concept;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
     * @see #getSelectionState()
     */
    private int selectionState = NO_SELECTION;

    private ConceptInterpreter conceptInterpreter;

    /**
     * Construct a nodeView for a Node.
     *
     * The DiagramView is used for the callback when a node was selected.
     */
    public NodeView(DiagramNode diagramNode, DiagramView diagramView, ConceptInterpreter conceptInterpreter) {
        this.diagramNode = diagramNode;
        this.diagramView = diagramView;
        this.conceptInterpreter = conceptInterpreter;
    }

    public DiagramNode getDiagramNode() {
        return diagramNode;
    }

    /**
     * Draws the node as circle.
     */
    public void draw(Graphics2D graphics) {
        if (diagramNode == null) {
            return;
        }
        DiagramSchema diagramSchema = DiagramSchema.getDiagramSchema();
        Paint oldPaint = graphics.getPaint();
        Color nodeColor;
        Color circleColor = diagramSchema.getCircleColor();
        if (diagramNode instanceof NestedDiagramNode) {
            nodeColor = diagramSchema.getNestedDiagramNodeColor();
        } else {
            nodeColor = diagramSchema.getGradientColor(calculateRelativeSize(diagramSchema));
        }
        Stroke oldStroke = graphics.getStroke();
        int selectionLineWidth = diagramSchema.getSelectionLineWidth();
        if (this.selectionState != NO_SELECTION) {
            if (this.selectionState == SELECTED_DIRECTLY) {
                graphics.setStroke(new BasicStroke(selectionLineWidth));
                circleColor = diagramSchema.getCircleSelectionColor();
            } else if (this.selectionState == SELECTED_IDEAL) {
                graphics.setStroke(new BasicStroke(selectionLineWidth));
                circleColor = diagramSchema.getCircleIdealColor();
            } else if (this.selectionState == SELECTED_FILTER) {
                graphics.setStroke(new BasicStroke(selectionLineWidth));
                circleColor = diagramSchema.getCircleFilterColor();
            } else if (this.selectionState == NOT_SELECTED) {
                // lighten
                nodeColor = diagramSchema.fadeOut(nodeColor);
                circleColor = diagramSchema.fadeOut(circleColor);
            }
        }

        Ellipse2D ellipse = new Ellipse2D.Double(
                diagramNode.getPosition().getX() - diagramNode.getRadiusX(),
                diagramNode.getPosition().getY() - diagramNode.getRadiusY(),
                diagramNode.getRadiusX() * 2, diagramNode.getRadiusY() * 2);
        graphics.setPaint(nodeColor);
        graphics.fill(ellipse);
        graphics.setPaint(circleColor);
        graphics.draw(ellipse);
        graphics.setPaint(oldPaint);
        graphics.setStroke(oldStroke);
    }

    /**
     *  calculates relative size in order to calculate node color
     * */

    private double calculateRelativeSize(DiagramSchema diagramSchema) {
        double relativeSize;
        if (diagramSchema.getGradientReference() == DiagramSchema.GRADIENT_REFERENCE_DIAGRAM) {
            if (diagramSchema.getGradientType() == DiagramSchema.GRADIENT_TYPE_EXTENT) {
                relativeSize = calcRate((double) this.diagramNode.getConcept().getExtentSize(),
                        (double) DiagramController.getController().getNumberOfCurrentObjects());
            } else {
                relativeSize = calcRate((double) this.diagramNode.getConcept().getObjectContingentSize(),
                        (double) DiagramController.getController().getMaximalObjectContingentSize());
            }
        } else {
            if (diagramSchema.getGradientType() == DiagramSchema.GRADIENT_TYPE_EXTENT) {
                relativeSize = this.diagramNode.getConcept().getExtentSize();
            } else {
                /// @todo Check if this one can be avoided -- it is pretty useless
                relativeSize = this.diagramNode.getConcept().getObjectContingentSize();
            }
            relativeSize = relativeSize / DiagramController.getController().getNumberOfObjects();
        }
        return relativeSize;
    }

    private double calcRate(double extentSize, double denom) {
        if (denom == 0) {
            return 0;
        }
        return extentSize / denom;
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
        double sqDist = deltaX * deltaX + deltaY * deltaY;
        double sqRadius = diagramNode.getRadiusX() * diagramNode.getRadiusY();
        return sqDist <= sqRadius;
    }

    /**
     * Calculates the rectangle around this node.
     */
    public Rectangle2D getCanvasBounds(Graphics2D g) {
        Point2D center = this.diagramNode.getPosition();
        double x = center.getX();
        double y = center.getY();
        double rx = this.diagramNode.getRadiusX();
        double ry = this.diagramNode.getRadiusY();
        return new Rectangle2D.Double(x - rx, y - ry, 2 * rx, 2 * ry);
    }

    /**
     * Recalculates if the node is selected and how.
     *
     * @see #getSelectionState()
     */
    public void setSelectedConcepts(List concepts) {
        if ((concepts == null) || (concepts.size() == 0)) {
            this.selectionState = NO_SELECTION;
            return;
        }
        Iterator it = concepts.iterator();
        while (it.hasNext()) {
            Concept concept = (Concept) it.next();
            if (this.diagramNode.getConcept() == concept) {
                this.selectionState = SELECTED_DIRECTLY;
                return;
            }
            if (this.diagramNode.getConcept().hasSuperConcept(concept)) {
                this.selectionState = SELECTED_IDEAL;
                return;
            }
            if (this.diagramNode.getConcept().hasSubConcept(concept)) {
                this.selectionState = SELECTED_FILTER;
                return;
            }
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
     * @see #setSelectedConcept(Concept)
     */
    public int getSelectionState() {
        return this.selectionState;
    }

    /**
     * Overwritten to avoid raising nodes.
     */
    public boolean hasAutoRaise() {
        return false;
    }

    public DiagramView getDiagramView() {
        return diagramView;
    }
}