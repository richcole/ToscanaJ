/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.canvas.events;

import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.canvas.CanvasItem;

public class CanvasItemEvent implements Event {
    private CanvasItem item;

    public CanvasItemEvent(CanvasItem item) {
        this.item = item;
    }

    public CanvasItem getItem() {
        return item;
    }

    public Object getSource() {
        return item;
    }
}
