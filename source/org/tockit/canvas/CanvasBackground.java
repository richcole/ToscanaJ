/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Implements a virtual canvas item that represents the background.
 *
 * This class maps the background of the canvas onto a CanvasItem,
 * thus unifying background events with the rest of the model.
 */
public class CanvasBackground extends CanvasItem {
    /**
     * The paint used to fill the background.
     */
    private Paint paint = null;

    /**
     * Creates a new background object.
     *
     * The background will not paint itself until a paint is given
     * via setPaint(Paint).
     */
    public CanvasBackground() {
    }

    /**
     * Fills the whole graphic context with the paint given.
     */
    public void draw(Graphics2D g) {
        if (paint != null) {
            g.setPaint(paint);
            Rectangle clipBounds = g.getClipBounds();
            if (clipBounds != null) {
                g.fill(clipBounds);
            }
        }
    }

    /**
     * This returns always true.
     *
     * The background is assumed to cover the whole canvas, so it
     * is always hit.
     */
    public boolean containsPoint(Point2D point) {
        return true;
    }

    /**
     * Returns an empty rectangle.
     *
     * The background has no extension itself. The rectangle will be
     * placed on the origin of the coordinate system, which means the
     * origin will always be on the canvas.
     */
    public Rectangle2D getCanvasBounds(Graphics2D g) {
        return new Rectangle2D.Double(0, 0, 0, 0);
    }

    /**
     * Changes the content of the background.
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    /**
     * Returns the paint used for the background.
     */
    public Paint getPaint() {
        return paint;
    }

    public Point2D getPosition() {
        return new Point2D.Double(0,0);
    }

    public boolean hasAutoRaise() {
        return false;
    }
}
