/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.canvas.controller;

import net.sourceforge.toscanaj.canvas.Canvas;

import java.util.TimerTask;
import java.awt.geom.Point2D;

/**
 * Sends a background click event to a canvas.
 *
 * @see Canvas.mouseReleaseEvent(MouseEvent)
 */
class BackgroundSingleClickTask extends TimerTask {
    /**
     * The message recipient.
     */
    private Canvas target;
    /**
     * The position transmitted with the message.
     */
    private Point2D point;

    /**
     * Creates a new task for sending a message.
     */
    public BackgroundSingleClickTask(Canvas target, Point2D point) {
        this.target = target;
        this.point = point;
    }

    /**
     * Sends the message.
     */
    public void run() {
        target.backgroundSingleClicked(point);
    }
}
