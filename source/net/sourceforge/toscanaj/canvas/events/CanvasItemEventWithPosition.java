/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.canvas.events;

import net.sourceforge.toscanaj.canvas.CanvasItem;

import java.awt.geom.Point2D;

/**
 * A superclass for canvas item events that store a single position.
 *
 * This class has to be subclassed to tag it as a specific event, although nothing needs
 * to be implemented.
 *
 * The event can has a canvas and a screen position attached, there are methods to
 * retrieve them.
 */
abstract public class CanvasItemEventWithPosition extends CanvasItemEvent {
    protected Point2D canvasPosition = null;
    protected Point2D awtPosition = null;

    /**
     * Creates a new event with positions.
     */
    public CanvasItemEventWithPosition(CanvasItem item, Point2D canvasPosition, Point2D awtPosition) {
        super(item);
        this.canvasPosition = canvasPosition;
        this.awtPosition = awtPosition;
    }

    /**
     * Returns true if there is a canvas position stored inside.
     */
    public boolean hasCanvasPositionAttached() {
        return this.canvasPosition != null;
    }

    /**
     * Returns true if there is a screen position stored inside.
     */
    public boolean hasAWTPositionAttached() {
        return this.awtPosition != null;
    }

    /**
     * Returns the position of the event in the canvas model.
     */
    public Point2D getCanvasPosition() {
        return canvasPosition;
    }

    /**
     * Returns the position of the event in the screen model.
     */
    public Point2D getAWTPosition() {
        return awtPosition;
    }
}
