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
 * This event is emitted whenever an canvas item was activated (e.g. by double-clicking).
 */
public class CanvasItemActivatedEvent extends CanvasItemEventWithPosition {
    public CanvasItemActivatedEvent(CanvasItem item, Point2D canvasPosition, Point2D awtPosition) {
        super(item, canvasPosition, awtPosition);
    }
}
