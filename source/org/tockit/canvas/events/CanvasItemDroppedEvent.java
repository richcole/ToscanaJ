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

public class CanvasItemDroppedEvent extends CanvasItemDraggedEvent {
    public CanvasItemDroppedEvent(CanvasItem item, int modifiers,
                 Point2D canvasFromPosition, Point2D awtFromPosition,
                 Point2D canvasToPosition, Point2D awtToPosition) {
        super(item, modifiers, canvasFromPosition, awtFromPosition, canvasToPosition, awtToPosition);
    }
}
