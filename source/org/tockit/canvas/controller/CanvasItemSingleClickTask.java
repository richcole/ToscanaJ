/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.controller;

import org.tockit.canvas.CanvasItem;
import org.tockit.canvas.events.CanvasItemSelectedEvent;
import org.tockit.events.EventBroker;

import java.awt.geom.Point2D;
import java.util.TimerTask;

class CanvasItemSingleClickTask extends TimerTask {
    /**
     * The message recipient.
     */
    private CanvasItem target;
    /**
     * The position transmitted with the message.
     */
    private Point2D canvasPosition;
    private Point2D awtPosition;

    private int modifiers;

    private EventBroker context;

    /**
     * Creates a new task for sending a message.
     */
    public CanvasItemSingleClickTask(CanvasItem target, int modifiers,
                                     Point2D canvasPosition, Point2D awtPosition,
                                     EventBroker context) {
        this.target = target;
        this.modifiers = modifiers;
        this.canvasPosition = canvasPosition;
        this.awtPosition = awtPosition;
        this.context = context;
    }

    /**
     * Sends the message.
     */
    public void run() {
        context.processEvent(new CanvasItemSelectedEvent(target, modifiers,
                                                         this.canvasPosition, this.awtPosition));
    }
}
