/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.events;

import org.tockit.canvas.CanvasItem;

import java.awt.geom.Point2D;

/**
 * An event indicating a drag action of the user.
 *
 * The from and to positions in both coordinate models (canvas and screen)
 * can be queried from this class.
 */
public class CanvasItemDraggedEvent extends CanvasItemEvent {
    private Point2D canvasFromPosition = null;
    private Point2D awtFromPosition = null;
    private Point2D canvasToPosition = null;
    private Point2D awtToPosition = null;

    /**
     * Creates a new event.
     */
    public CanvasItemDraggedEvent(CanvasItem item, int modifiers,
                                  Point2D canvasFromPosition, Point2D awtFromPosition,
                                  Point2D canvasToPosition, Point2D awtToPosition) {
        super(item, modifiers);
        this.canvasFromPosition = canvasFromPosition;
        this.awtFromPosition = awtFromPosition;
        this.canvasToPosition = canvasToPosition;
        this.awtToPosition = awtToPosition;
    }

    /**
     * Returns the starting position in the canvas model coordinates.
     */
    public Point2D getCanvasFromPosition() {
        return canvasFromPosition;
    }

    /**
     * Returns the starting position in the screen model coordinates.
     */
    public Point2D getAwtFromPosition() {
        return awtFromPosition;
    }

    /**
     * Returns the end position in the canvas model coordinates.
     */
    public Point2D getCanvasToPosition() {
        return canvasToPosition;
    }

    /**
     * Returns the end position in the screen model coordinates.
     */
    public Point2D getAwtToPosition() {
        return awtToPosition;
    }
}
