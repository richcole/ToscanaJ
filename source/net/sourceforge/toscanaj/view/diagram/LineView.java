/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.lattice.Concept;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

/**
 * class DiagramLine draws a line between two points
 */
public class LineView extends CanvasItem {
    /**
     * Store the line in the model for this view.
     */
    private DiagramLine diagramLine = null;

    /**
     * The current state of selection.
     *
     * @see setSelectedConcept(Concept)
     */
    private int selectionState = NodeView.NO_SELECTION;

    /**
     * Creates a view for the given DiagramLine.
     */
    public LineView(DiagramLine diagramLine) {
        this.diagramLine = diagramLine;
    }

    /**
     * Draws the line.
     */
    public void draw(Graphics2D graphics) {
        DiagramSchema diagramSchema = DiagramSchema.getDiagramSchema();
        Point2D from = diagramLine.getFromPosition();
        Point2D to = diagramLine.getToPosition();
        Paint oldPaint = graphics.getPaint();
        Stroke oldStroke = graphics.getStroke();
        int selectionLineWidth = diagramSchema.getSelectionLineWidth();
        if (this.diagramLine.getFromNode().getY() > this.diagramLine.getToNode().getY()) {
            graphics.setPaint(Color.red);
            graphics.setStroke(new BasicStroke(3));
        } else if (this.selectionState == NodeView.NO_SELECTION) {
            graphics.setPaint(diagramSchema.getLineColor());
            graphics.setStroke(new BasicStroke(1));
        } else if (this.selectionState == NodeView.SELECTED_IDEAL) {
            graphics.setPaint(diagramSchema.getCircleIdealColor());
            graphics.setStroke(new BasicStroke(selectionLineWidth));
        } else if (this.selectionState == NodeView.SELECTED_FILTER) {
            graphics.setPaint(diagramSchema.getCircleFilterColor());
            graphics.setStroke(new BasicStroke(selectionLineWidth));
        } else if (this.selectionState == NodeView.NOT_SELECTED) {
            graphics.setPaint(diagramSchema.fadeOut(diagramSchema.getLineColor()));
        }
        graphics.draw(new Line2D.Double(from, to));
        graphics.setPaint(oldPaint);
        graphics.setStroke(oldStroke);
    }

    /**
     * Returns always false since we assume the line to have no width.
     */
    public boolean containsPoint(Point2D point) {
        return false;
    }

    /**
     * Calculates the rectangle around this line.
     */
    public Rectangle2D getCanvasBounds(Graphics2D graphics) {
        Point2D from = diagramLine.getFromPosition();
        Point2D to = diagramLine.getToPosition();
        double x,y,w,h;
        if (from.getX() < to.getX()) {
            x = from.getX();
            w = to.getX() - x;
        } else {
            x = to.getX();
            w = from.getX() - x;
        }
        if (from.getY() < to.getY()) {
            y = from.getY();
            h = to.getY() - y;
        } else {
            y = to.getY();
            h = from.getY() - y;
        }
        return new Rectangle2D.Double(x, y, w, h);
    }

    /**
     * Checks if the line belongs to filter or ideal of the selected concepts
     * and marks it appropriately.
     *
     * @see NodeView.setSelectedConcept(Concept)
     */
    public void setSelectedConcepts(List concepts) {
        if ((concepts == null) || (concepts.size() == 0)) {
            this.selectionState = NodeView.NO_SELECTION;
            return;
        }
        Iterator it = concepts.iterator();
        while (it.hasNext()) {
            Concept concept = (Concept) it.next();
            Concept from = this.diagramLine.getFromNode().getConcept();
            if ((from == concept) || (from.hasSuperConcept(concept))) {
                this.selectionState = NodeView.SELECTED_IDEAL;
                return;
            }
            Concept to = this.diagramLine.getToNode().getConcept();
            if ((to == concept) || (to.hasSubConcept(concept))) {
                this.selectionState = NodeView.SELECTED_FILTER;
                return;
            }
        }
        this.selectionState = NodeView.NOT_SELECTED;
        return;
    }

    /**
     * Returns the information if and how the node is selected.
     *
     * @see NodeView.getSelectionState()
     */
    public int getSelectionState() {
        return this.selectionState;
    }
}
