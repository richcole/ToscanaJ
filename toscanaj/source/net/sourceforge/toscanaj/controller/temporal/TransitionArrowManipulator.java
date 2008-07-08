/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.temporal;

import java.awt.geom.Point2D;

import net.sourceforge.toscanaj.view.temporal.TransitionArrow;

import org.tockit.canvas.Canvas;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.canvas.events.CanvasItemDroppedEvent;
import org.tockit.canvas.events.CanvasItemPickupEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class TransitionArrowManipulator implements EventBrokerListener {
    private final Canvas canvas;

    private static final int WAITING = 0;
    private static final int DRAGGING = 1;
    private static final int MOVING_HEAD = 2;
    private static final int MOVING_TAIL = 3;

    private int mode = WAITING;

    public TransitionArrowManipulator(final Canvas canvas,
            final EventBroker eventBroker) {
        eventBroker.subscribe(this, CanvasItemDraggedEvent.class,
                TransitionArrow.class);
        this.canvas = canvas;
    }

    public void processEvent(final Event e) {
        final CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        final TransitionArrow arrow = (TransitionArrow) dragEvent.getSubject();
        final Point2D fromPosition = dragEvent.getCanvasFromPosition();
        final Point2D toPosition = dragEvent.getCanvasToPosition();
        if (e instanceof CanvasItemPickupEvent) {
            if (arrow.pointIsInHeadArea(fromPosition)) {
                this.mode = MOVING_HEAD;
            } else if (arrow.pointIsInTailArea(fromPosition)) {
                this.mode = MOVING_TAIL;
            } else {
                this.mode = DRAGGING;
            }
        } else if (e instanceof CanvasItemDroppedEvent) {
            this.mode = WAITING;
        }
        applyChange(arrow, fromPosition, toPosition);
        canvas.repaint();
    }

    public void applyChange(final TransitionArrow arrow,
            final Point2D fromPosition, final Point2D toPosition) {
        final double dx = toPosition.getX() - fromPosition.getX();
        final double dy = toPosition.getY() - fromPosition.getY();
        if (this.mode == DRAGGING) {
            arrow.shiftPosition(dx, dy);
        } else if (this.mode == MOVING_HEAD) {
            arrow.shiftEndPoint(dx, dy);
        } else if (this.mode == MOVING_TAIL) {
            arrow.shiftStartPoint(dx, dy);
        }
        canvas.repaint();
    }
}
