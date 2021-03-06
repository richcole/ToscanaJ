/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.NormedIntervalSource;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.NestedDiagramNode;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagramNode;

import org.tockit.canvas.CanvasItem;
import org.tockit.swing.preferences.ExtendedPreferences;

/**
 * class DiagramNode holds details on node position and size
 */

public class NodeView extends CanvasItem {
    private static final ExtendedPreferences preferences = ExtendedPreferences
            .userNodeForClass(NodeView.class);
    private static final Color WARNING_COLOR = Color.RED;
    private static final BasicStroke WARNING_STROKE = new BasicStroke(2.5f);

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
    private int selectionState = DiagramView.NO_SELECTION;

    private final ConceptInterpretationContext conceptInterpretationContext;
    private boolean isRealized;
    private boolean isRealizedCalculated = false;

    /**
     * Construct a nodeView for a Node.
     * 
     * The DiagramView is used for the callback when a node was selected.
     */
    public NodeView(final DiagramNode diagramNode,
            final DiagramView diagramView,
            final ConceptInterpretationContext context) {
        this.diagramNode = diagramNode;
        this.diagramView = diagramView;
        this.conceptInterpretationContext = context;
    }

    public DiagramNode getDiagramNode() {
        return diagramNode;
    }

    public ConceptInterpretationContext getConceptInterpretationContext() {
        return conceptInterpretationContext;
    }

    /**
     * Draws the node as circle.
     */
    @Override
    public void draw(final Graphics2D graphics) {
        if (diagramNode == null) {
            return;
        }
        final DiagramSchema diagramSchema = diagramView.getDiagramSchema();
        final Paint oldPaint = graphics.getPaint();
        Color nodeColor;
        Color circleColor = diagramSchema.getCircleColor();
        if (diagramNode instanceof NestedDiagramNode) {
            nodeColor = diagramSchema.getNestedDiagramNodeColor();
        } else {
            nodeColor = diagramSchema.getGradient().getColor(
                    calculateRelativeSize(diagramSchema.getGradientType()));
            if (!isRealized()) {
                nodeColor = diagramSchema.getNotRealisedNodeColor(nodeColor);
            }
        }
        final Stroke oldStroke = graphics.getStroke();
        graphics.setStroke(new BasicStroke(diagramSchema.getNodeStrokeWidth()));
        final float selectionLineWidth = diagramSchema.getSelectionLineWidth();
        if (this.selectionState != DiagramView.NO_SELECTION) {
            if (this.selectionState == DiagramView.SELECTED_DIRECTLY) {
                graphics.setStroke(new BasicStroke(selectionLineWidth));
                circleColor = diagramSchema.getCircleSelectionColor();
            } else if (this.selectionState == DiagramView.SELECTED_IDEAL) {
                graphics.setStroke(new BasicStroke(selectionLineWidth));
                circleColor = diagramSchema.getCircleIdealColor();
            } else if (this.selectionState == DiagramView.SELECTED_FILTER) {
                graphics.setStroke(new BasicStroke(selectionLineWidth));
                circleColor = diagramSchema.getCircleFilterColor();
            } else if (this.selectionState == DiagramView.NOT_SELECTED) {
                // lighten
                nodeColor = diagramSchema.fadeOut(nodeColor);
                circleColor = diagramSchema.fadeOut(circleColor);
            }
        }

        if (this.diagramNode.hasCollision()) {
            nodeColor = WARNING_COLOR;
            graphics.setStroke(WARNING_STROKE);
        }

        final Ellipse2D ellipse = new Ellipse2D.Double(diagramNode
                .getPosition().getX()
                - getRadiusX(),
                diagramNode.getPosition().getY() - getRadiusY(),
                getRadiusX() * 2, getRadiusY() * 2);
        graphics.setPaint(nodeColor);
        graphics.fill(ellipse);
        graphics.setPaint(circleColor);
        graphics.draw(ellipse);
        if (preferences.getBoolean("displayCoordinates", false)) {
            String vector;
            if (diagramNode instanceof NDimDiagramNode) {
                final NDimDiagramNode node = (NDimDiagramNode) diagramNode;
                final double[] ndimVec = node.getNdimVector();
                vector = "(";
                for (int i = 0; i < ndimVec.length; i++) {
                    final double v = ndimVec[i];
                    vector += (int) v;
                    if (i != ndimVec.length - 1) {
                        vector += ",";
                    }
                }
                vector += ")";
            } else {
                vector = "(" + (int) diagramNode.getPosition().getX() + "/"
                        + (int) diagramNode.getPosition().getY() + ")";
            }
            graphics.drawString(vector, (float) (diagramNode.getPosition()
                    .getX() + getRadiusX()), (float) diagramNode.getPosition()
                    .getY());
        }
        graphics.setPaint(oldPaint);
        graphics.setStroke(oldStroke);
    }

    public double getRadiusY() {
        return getRadius(diagramNode.getRadiusY());
    }

    public double getRadiusX() {
        return getRadius(diagramNode.getRadiusX());
    }

    protected double getRadius(final double maxRadius) {
        final double reductionFactor = this.diagramView.getDiagramSchema()
                .getNotRealizedNodeSizeReductionFactor();
        final double minRadius = maxRadius / reductionFactor;
        if (this.isRealized()) {
            if (this.diagramNode instanceof NestedDiagramNode) {
                return maxRadius;
            }
            final ConceptInterpreter.IntervalType nodeSizeScalingType = this.diagramView
                    .getDiagramSchema().getNodeSizeScalingType();
            final double relativeSize = calculateRelativeSize(nodeSizeScalingType);
            return maxRadius * relativeSize + minRadius * (1 - relativeSize);
        } else {
            return minRadius;
        }
    }

    private boolean isRealized() {
        if (!isRealizedCalculated) {
            final ConceptInterpreter interpreter = diagramView
                    .getConceptInterpreter();
            final Concept concept = this.diagramNode.getConcept();
            isRealized = interpreter.isRealized(concept,
                    conceptInterpretationContext);
            isRealizedCalculated = true;
        }
        return isRealized;
    }

    /**
     * calculates relative size in order to calculate node color
     */
    private double calculateRelativeSize(
            final ConceptInterpreter.IntervalType intervalType) {
        final NormedIntervalSource intervalSource = diagramView
                .getConceptInterpreter().getIntervalSource(intervalType);
        return intervalSource.getValue(this.diagramNode.getConcept(),
                conceptInterpretationContext);
    }

    /**
     * Implements CanvasItem.containsPoint(Point2D).
     * 
     * This is currently not exact if the node is not a circle, the test is if a
     * circle with the geometric average of the two radii is hit.
     */
    @Override
    public boolean containsPoint(final Point2D point) {
        final double deltaX = point.getX() - diagramNode.getPosition().getX();
        final double deltaY = point.getY() - diagramNode.getPosition().getY();
        final double sqDist = deltaX * deltaX + deltaY * deltaY;
        final double sqRadius = getRadiusX() * getRadiusY();
        return sqDist <= sqRadius;
    }

    /**
     * Calculates the rectangle around this node.
     */
    @Override
    public Rectangle2D getCanvasBounds(final Graphics2D g) {
        final Point2D center = this.diagramNode.getPosition();
        final double x = center.getX();
        final double y = center.getY();
        final double rx = getRadiusX();
        final double ry = getRadiusY();
        final Stroke stroke = g.getStroke();
        if (stroke instanceof BasicStroke) {
            final double w = ((BasicStroke) stroke).getLineWidth();
            return new Rectangle2D.Double(x - rx - w / 2, y - ry - w / 2, 2
                    * rx + w, 2 * ry + w);
        }
        return new Rectangle2D.Double(x - rx, y - ry, 2 * rx, 2 * ry);
    }

    @Override
    public Point2D getPosition() {
        return this.diagramNode.getPosition();
    }

    /**
     * Recalculates if the node is selected and how.
     * 
     * @see #getSelectionState()
     */
    public void setSelectedConcepts(final Concept[] selectedConcepts) {
        if ((selectedConcepts == null) || (selectedConcepts.length == 0)) {
            this.selectionState = DiagramView.NO_SELECTION;
            return;
        }
        this.selectionState = DiagramView.NOT_SELECTED;
        // we are comparing nodes from the inside out, assuming both lists are
        // the same lengths
        // (as they always should be)
        final Concept[] ourConcepts = getDiagramNode().getConceptNestingList();
        boolean onOurLevel = false;
        for (int i = 0; i < selectedConcepts.length; i++) {
            final Concept selectedConcept = selectedConcepts[i];
            final Concept ourConcept = ourConcepts[i];
            // we don't care about anything nested deeper than we are
            if (!onOurLevel && ourConcept != this.getDiagramNode().getConcept()) {
                continue;
            } else {
                onOurLevel = true;
            }
            if (ourConcept == selectedConcept) {
                // direct hit, but if we had another hit before it doesn't
                // count, if
                // it was another direct hit we keep it, otherwise we are still
                // in
                // filter or ideal
                if (this.selectionState == DiagramView.NOT_SELECTED) {
                    this.selectionState = DiagramView.SELECTED_DIRECTLY;
                }
            } else if (ourConcept.hasSuperConcept(selectedConcept)) {
                // we are in the ideal on this level, if this is the first
                // comparison
                // or we had a direct hit or an ideal hit before, we are in the
                // ideal,
                // otherwise we are out
                if (this.selectionState == DiagramView.SELECTED_FILTER) {
                    this.selectionState = DiagramView.NOT_SELECTED;
                    return;
                } else {
                    this.selectionState = DiagramView.SELECTED_IDEAL;
                }
            } else if (ourConcept.hasSubConcept(selectedConcept)) {
                // dual to the last one
                if (this.selectionState == DiagramView.SELECTED_IDEAL) {
                    this.selectionState = DiagramView.NOT_SELECTED;
                    return;
                } else {
                    this.selectionState = DiagramView.SELECTED_FILTER;

                }
            } else {
                // we fail to hit anything on this level
                this.selectionState = DiagramView.NOT_SELECTED;
                return;
            }
        }
        return;
    }

    /**
     * Returns the information if and how the node is selected.
     * 
     * The return values are: - NO_SELECTION: we don't have a selection at the
     * moment - NOT_SELECTED: the node displays a concept which is neither in
     * filter nor ideal of the selected concept - SELECTED_DIRECTLY: the node
     * displays the selected concept - SELECTED_IN_FILTER: the node displays a
     * concept which is in the filter of the selected concept -
     * SELECTED_IN_IDEAL: the node displays a concept which is in the ideal of
     * the selected concept
     * 
     * @see DiagramView#setSelectedConcepts(Concept[])
     */
    public int getSelectionState() {
        return this.selectionState;
    }

    /**
     * Overwritten to avoid raising nodes.
     */
    @Override
    public boolean hasAutoRaise() {
        return false;
    }

    public DiagramView getDiagramView() {
        return diagramView;
    }
}
