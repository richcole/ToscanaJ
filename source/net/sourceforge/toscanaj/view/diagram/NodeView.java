/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import org.tockit.canvas.CanvasItem;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.NestedDiagramNode;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagramNode;

import java.awt.*;
import java.awt.geom.*;
import java.util.Iterator;
import java.util.List;

/**
 * class DiagramNode holds details on node position and size
 */

public class NodeView extends CanvasItem {
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

    private ConceptInterpretationContext conceptInterpretationContext;
    private boolean isRealized;
    private boolean isRealizedCalculated = false;

    /**
     * Construct a nodeView for a Node.
     *
     * The DiagramView is used for the callback when a node was selected.
     */
    public NodeView(DiagramNode diagramNode, DiagramView diagramView, ConceptInterpretationContext context) {
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
    public void draw(Graphics2D graphics) {
        if (diagramNode == null) {
            return;
        }
        DiagramSchema diagramSchema = diagramView.getDiagramSchema();
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

        Ellipse2D ellipse = new Ellipse2D.Double(
                diagramNode.getPosition().getX() - getRadiusX(),
                diagramNode.getPosition().getY() - getRadiusY(),
                getRadiusX() * 2, getRadiusY() * 2);
        graphics.setPaint(nodeColor);
        graphics.fill(ellipse);
        graphics.setPaint(circleColor);
        graphics.draw(ellipse);
        if(ConfigurationManager.fetchInt("NodeView","displayVectors",0) != 0) {
            String vector;
            if (diagramNode instanceof NDimDiagramNode) {
                NDimDiagramNode node = (NDimDiagramNode) diagramNode;
                double[] ndimVec = node.getNdimVector();
                vector = "(";
                for (int i = 0; i < ndimVec.length; i++) {
                    double v = ndimVec[i];
                    vector += (int)v;
                    if(i != ndimVec.length-1) {
                        vector += ",";
                    }
                }
                vector += ")";
            } else {
                vector = "(" + (int)diagramNode.getPosition().getX() + "/" + (int)diagramNode.getPosition().getY() + ")";
            }
            graphics.drawString(vector, (float) (diagramNode.getPosition().getX() + getRadiusX()),
                                         (float) diagramNode.getPosition().getY());
        }
        graphics.setPaint(oldPaint);
        graphics.setStroke(oldStroke);
    }

    public double getRadiusY() {
        if (this.isRealized()) {
            return diagramNode.getRadiusY();
        } else {
            double reductionFactor = this.diagramView.getDiagramSchema().getNotRealizedNodeSizeReductionFactor();
            return diagramNode.getRadiusY() / reductionFactor;
        }
    }

    public double getRadiusX() {
        if (this.isRealized()) {
            return diagramNode.getRadiusX();
        } else {
            double reductionFactor = this.diagramView.getDiagramSchema().getNotRealizedNodeSizeReductionFactor();
            return diagramNode.getRadiusX() / reductionFactor;
        }
    }

    private boolean isRealized() {
        if(!isRealizedCalculated) {
            ConceptInterpreter interpreter = diagramView.getConceptInterpreter();
            Concept concept = this.diagramNode.getConcept();
            isRealized = interpreter.isRealized(concept, conceptInterpretationContext);
            isRealizedCalculated = true;
        }
        return isRealized;
    }

    /**
     *  calculates relative size in order to calculate node color
     */
    private double calculateRelativeSize(DiagramSchema diagramSchema) {
        if (diagramSchema.getGradientReference() == DiagramSchema.GRADIENT_REFERENCE_DIAGRAM) {
            if (diagramSchema.getGradientType() == DiagramSchema.GRADIENT_TYPE_EXTENT) {
                return diagramView.getConceptInterpreter().getRelativeExtentSize(
                        this.diagramNode.getConcept(),
                        conceptInterpretationContext,
                        ConceptInterpreter.REFERENCE_DIAGRAM
                );
            } else {
                return diagramView.getConceptInterpreter().getRelativeObjectContingentSize(
                        this.diagramNode.getConcept(),
                        conceptInterpretationContext,
                        ConceptInterpreter.REFERENCE_DIAGRAM
                );
            }
        } else {
            if (diagramSchema.getGradientType() == DiagramSchema.GRADIENT_TYPE_EXTENT) {
                return diagramView.getConceptInterpreter().getRelativeExtentSize(
                        this.diagramNode.getConcept(),
                        conceptInterpretationContext,
                        ConceptInterpreter.REFERENCE_SCHEMA
                );
            } else {
                /// @todo Check if this one can be avoided -- it is pretty useless
                return diagramView.getConceptInterpreter().getRelativeObjectContingentSize(
                        this.diagramNode.getConcept(),
                        conceptInterpretationContext,
                        ConceptInterpreter.REFERENCE_SCHEMA
                );
            }
        }
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
        double sqRadius = getRadiusX() * getRadiusY();
        return sqDist <= sqRadius;
    }

    /**
     * Calculates the rectangle around this node.
     */
    public Rectangle2D getCanvasBounds(Graphics2D g) {
        Point2D center = this.diagramNode.getPosition();
        double x = center.getX();
        double y = center.getY();
        double rx = getRadiusX();
        double ry = getRadiusY();
        return new Rectangle2D.Double(x - rx, y - ry, 2 * rx, 2 * ry);
    }

    public Point2D getPosition() {
        return this.diagramNode.getPosition();
    }

    /**
     * Recalculates if the node is selected and how.
     *
     * @see #getSelectionState()
     */
    public void setSelectedConcepts(List selectedConcepts) {
        if ((selectedConcepts == null) || (selectedConcepts.size() == 0)) {
            this.selectionState = DiagramView.NO_SELECTION;
            return;
        }
        this.selectionState = DiagramView.NOT_SELECTED;
        List ourConcepts = getDiagramNode().getConceptNestingList();
        Iterator it = selectedConcepts.iterator();
        Iterator it2 = ourConcepts.iterator();
        boolean onOurLevel = false;
        while (it.hasNext()) {
            Concept selectedConcept = (Concept) it.next();
            Concept ourConcept = (Concept) it2.next();
            if( !onOurLevel && ourConcept != this.getDiagramNode().getConcept()) {
                continue;
            }
            else {
                onOurLevel = true;
            }
            if (ourConcept == selectedConcept) {
                if (this.selectionState == DiagramView.NOT_SELECTED) {
                    this.selectionState = DiagramView.SELECTED_DIRECTLY;
                }
            } else if (ourConcept.hasSuperConcept(selectedConcept)) {
                if (this.selectionState == DiagramView.NOT_SELECTED) {
                    this.selectionState = DiagramView.SELECTED_IDEAL;
                }
                else if (this.selectionState == DiagramView.SELECTED_FILTER) {
                    this.selectionState = DiagramView.NOT_SELECTED;
                    return;
                }
            } else if (ourConcept.hasSubConcept(selectedConcept)) {
                if (this.selectionState == DiagramView.NOT_SELECTED) {
                    this.selectionState = DiagramView.SELECTED_FILTER;
                }
                else if (this.selectionState == DiagramView.SELECTED_IDEAL) {
                    this.selectionState = DiagramView.NOT_SELECTED;
                    return;
                }
            } else {
                this.selectionState = DiagramView.NOT_SELECTED;
                return;
            }
        }
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
     * @see #setSelectedConcepts(List)
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