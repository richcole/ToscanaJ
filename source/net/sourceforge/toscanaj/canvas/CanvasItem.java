package net.sourceforge.toscanaj.canvas;

import java.awt.geom.Point2D;

import net.sourceforge.toscanaj.view.diagram.ToscanajGraphics2D;

/**
 * Abstract class to draw 2D graph items.
 *
 * CanvasItems can be put on a DrawingCanvas where they will have a z-order and
 * can be moved.
 */
public abstract class CanvasItem {

    /**
     * draw method called to draw canvas item
     */
    public abstract void draw(ToscanajGraphics2D g);

    /**
     * Returns true when the given point is on the item.
     */
    public abstract boolean containsPoint(Point2D point);

    /**
     * Moves the label by the given distance.
     */
    public abstract void moveBy(double deltaX, double deltaY);
}