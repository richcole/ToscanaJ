package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.view.diagram.ToscanajGraphics2D;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * class DiagramLine draws a line between two points
 */

import java.awt.Graphics2D;

public class LineView extends CanvasItem {
    /**
     * Store the line in the model for this view.
     */
    private DiagramLine diagramLine = null;

    /**
     * Creates a view for the given DiagramLine.
     */
    public LineView(DiagramLine diagramLine) {
        this.diagramLine = diagramLine;
    }

    /**
     * Draws the line.
     */
    public void draw(ToscanajGraphics2D g) {
        Point2D from = diagramLine.getFromPosition();
        Point2D to = diagramLine.getToPosition();
        g.drawLine(from, to);
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
    public Rectangle2D getBounds(ToscanajGraphics2D g) {
        Point2D from = g.project(diagramLine.getFromPosition());
        Point2D to = g.project(diagramLine.getToPosition());
        double x,y,w,h;
        if( from.getX() < to.getX() ) {
            x = from.getX();
            w = to.getX() - x;
        }
        else {
            x = to.getX();
            w = from.getX() - x;
        }
        if( from.getY() < to.getY() ) {
            y = from.getY();
            h = to.getY() - y;
        }
        else {
            y = to.getY();
            h = from.getY() - y;
        }
        return new Rectangle2D.Double(x,y,w,h);
    }
}