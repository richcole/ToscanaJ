/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.canvas.controller;

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.canvas.events.CanvasItemSelectedEvent;
import net.sourceforge.toscanaj.events.EventBroker;

import java.util.TimerTask;
import java.awt.geom.Point2D;

class CanvasItemSingleClickTask extends TimerTask {
    /**
     * The message recipient.
     */
    private CanvasItem target;
    /**
     * The position transmitted with the message.
     */
    private Point2D point;

    private EventBroker context;

    /**
     * Creates a new task for sending a message.
     */
    public CanvasItemSingleClickTask(CanvasItem target, Point2D point, EventBroker context) {
        this.target = target;
        this.point = point;
        this.context = context;
    }

    /**
     * Sends the message.
     */
    public void run() {
        target.singleClicked(point);
        context.processEvent(new CanvasItemSelectedEvent(target));
    }
}
