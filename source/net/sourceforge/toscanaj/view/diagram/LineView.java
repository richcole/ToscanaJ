/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;

import java.awt.*;
import java.awt.geom.*;

/**
 * class DiagramLine drawsdsfdfs a line between two points
 */
public class LineView extends CanvasItem {
    /**
     * Store the line in the model for this view.
     */
    private DiagramLine diagramLine = null;

    private NodeView fromView;
    private NodeView toView;

    /**
     * Creates a view for the given DiagramLine.
     */
    public LineView(DiagramLine diagramLine, NodeView fromView, NodeView toView) {
        this.diagramLine = diagramLine;
        this.fromView = fromView;
        this.toView = toView;
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
        } else if (this.getSelectionState() == DiagramView.NO_SELECTION) {
            graphics.setPaint(diagramSchema.getLineColor());
            graphics.setStroke(new BasicStroke(1));
        } else if (this.getSelectionState() == DiagramView.SELECTED_IDEAL) {
            graphics.setPaint(diagramSchema.getCircleIdealColor());
            graphics.setStroke(new BasicStroke(selectionLineWidth));
        } else if (this.getSelectionState() == DiagramView.SELECTED_FILTER) {
            graphics.setPaint(diagramSchema.getCircleFilterColor());
            graphics.setStroke(new BasicStroke(selectionLineWidth));
        } else if (this.getSelectionState() == DiagramView.NOT_SELECTED) {
            graphics.setPaint(diagramSchema.fadeOut(diagramSchema.getLineColor()));
            graphics.setStroke(new BasicStroke(1));
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

    private int getSelectionState() {
        if (this.fromView.getSelectionState() == DiagramView.NO_SELECTION) {
            return DiagramView.NO_SELECTION;
        }
        if (this.fromView.getSelectionState() == DiagramView.NOT_SELECTED) {
            return DiagramView.NOT_SELECTED;
        }
        if (this.toView.getSelectionState() == DiagramView.NOT_SELECTED) {
            return DiagramView.NOT_SELECTED;
        }
        if (this.fromView.getSelectionState() == DiagramView.SELECTED_FILTER) {
            return DiagramView.SELECTED_FILTER;
        }
        if (this.toView.getSelectionState() == DiagramView.SELECTED_IDEAL) {
            return DiagramView.SELECTED_IDEAL;
        }
        return DiagramView.NOT_SELECTED;
    }
}
