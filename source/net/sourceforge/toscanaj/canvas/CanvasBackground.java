/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.canvas;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Remaps the CanvasItem callbacks onto the callbacks on the Canvas.
 */
public class CanvasBackground extends CanvasItem {
    private Paint paint = null;
    private Canvas canvas;

    public CanvasBackground(Canvas canvas) {
        this.canvas = canvas;
    }

    public void draw(Graphics2D g) {
        g.setPaint(paint);
        g.fill(g.getClipBounds());
    }

    public boolean containsPoint(Point2D point) {
        return true;
    }

    public Rectangle2D getCanvasBounds(Graphics2D g) {
        return new Rectangle2D.Double(0,0,0,0);
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Paint getPaint() {
        return paint;
    }
}
