/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.canvas.events;

import net.sourceforge.toscanaj.canvas.CanvasItem;

import java.awt.geom.Point2D;

public class CanvasItemSelectedEvent extends CanvasItemEvent {
    private Point2D canvasPosition = null;
    private Point2D awtPosition = null;

    public CanvasItemSelectedEvent(CanvasItem item) {
        super(item);
    }

    public CanvasItemSelectedEvent(CanvasItem item, Point2D canvasPosition, Point2D awtPosition) {
        super(item);
        this.canvasPosition = canvasPosition;
        this.awtPosition = awtPosition;
    }

    public boolean hasPositionAttached() {
        return this.canvasPosition != null;
    }

    public Point2D getCanvasPosition() {
        return canvasPosition;
    }

    public Point2D getAWTPosition() {
        return awtPosition;
    }
}
