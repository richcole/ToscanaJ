/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
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
    private Point2D canvasPosition;
    private Point2D awtPosition;

    private EventBroker context;

    /**
     * Creates a new task for sending a message.
     */
    public CanvasItemSingleClickTask(CanvasItem target,
                                     Point2D canvasPosition, Point2D awtPosition,
                                     EventBroker context) {
        this.target = target;
        this.canvasPosition = canvasPosition;
        this.awtPosition = awtPosition;
        this.context = context;
    }

    /**
     * Sends the message.
     */
    public void run() {
        target.singleClicked(canvasPosition);
        context.processEvent(new CanvasItemSelectedEvent(target, this.canvasPosition, this.awtPosition));
    }
}
