/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.canvas.events;

import net.sourceforge.toscanaj.canvas.CanvasItem;

import java.awt.geom.Point2D;

public class CanvasItemDraggedEvent extends CanvasItemEvent {
    private Point2D canvasFromPosition = null;
    private Point2D awtFromPosition = null;
    private Point2D canvasToPosition = null;
    private Point2D awtToPosition = null;

    public CanvasItemDraggedEvent(CanvasItem item) {
        super(item);
    }

    public CanvasItemDraggedEvent(CanvasItem item, Point2D canvasFromPosition, Point2D awtFromPosition,
                                                   Point2D canvasToPosition, Point2D awtToPosition) {
        super(item);
        this.canvasFromPosition = canvasFromPosition;
        this.awtFromPosition = awtFromPosition;
        this.canvasToPosition = canvasToPosition;
        this.awtToPosition = awtToPosition;
    }

    public boolean hasPositionsAttached() {
        return (this.canvasFromPosition != null) && (this.canvasToPosition != null);
    }

    public Point2D getCanvasFromPosition() {
        return canvasFromPosition;
    }

    public Point2D getAwtFromPosition() {
        return awtFromPosition;
    }

    public Point2D getCanvasToPosition() {
        return canvasToPosition;
    }

    public Point2D getAwtToPosition() {
        return awtToPosition;
    }
}
