/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.manipulators;

import org.tockit.events.*;
import org.tockit.canvas.events.*;
import org.tockit.canvas.*;

import java.awt.geom.Point2D;

public class ItemMovementManipulator implements EventListener {
    protected Canvas canvas;

    public ItemMovementManipulator(Canvas canvas, EventBroker eventBroker) {
        this(canvas, MovableCanvasItem.class, eventBroker);
    }

    public ItemMovementManipulator(Canvas canvas, Class itemType, EventBroker eventBroker) {
        if(! eventBroker.extendsOrImplements(itemType, MovableCanvasItem.class) ){
            throw new RuntimeException("ItemMovementManipulator can only be subscribed to MovableCanvasItem or subtypes");
        }
        eventBroker.subscribe(this, CanvasItemDraggedEvent.class, itemType);
        this.canvas = canvas;
    }

    public void processEvent(Event e) {
        if( e instanceof CanvasItemPickupEvent ) {
            dragStart((CanvasItemPickupEvent) e);
        } else if( e instanceof CanvasItemDroppedEvent ) {
            dragEnd((CanvasItemDroppedEvent) e);
        } else {
            moveItem((CanvasItemDraggedEvent) e);
        }
        canvas.repaint();
    }

    protected void moveItem(CanvasItemDraggedEvent dragEvent) {
        MovableCanvasItem item = (MovableCanvasItem) dragEvent.getSubject();
        Point2D fromPosition = dragEvent.getCanvasFromPosition();
        Point2D toPosition = dragEvent.getCanvasToPosition();
        item.moveBy(toPosition.getX() - fromPosition.getX(),
                    toPosition.getY() - fromPosition.getY());
    }

    protected void dragStart(CanvasItemPickupEvent dragEvent) {
        moveItem(dragEvent);
    }

    protected void dragEnd(CanvasItemDroppedEvent dragEvent) {
        moveItem(dragEvent);
    }
}
