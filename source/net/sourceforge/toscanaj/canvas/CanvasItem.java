package net.sourceforge.toscanaj.canvas;

import java.awt.geom.Point2D;

import net.sourceforge.toscanaj.view.diagram.ToscanajGraphics2D;

/**
 * abstract class to draw 2D graph items. CanvasItem will have a concept of depth
 * for re-drawing, and hold the point that an item is positioned.
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
}