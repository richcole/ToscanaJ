/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.canvas;

import net.sourceforge.toscanaj.observer.ChangeObservable;
import net.sourceforge.toscanaj.observer.ChangeObserver;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

/**
 * Abstract class to draw 2D graph items.
 *
 * CanvasItems can be put on a Canvas where they will have a z-order and
 * can be moved.
 */
public abstract class CanvasItem implements ChangeObservable {
    /**
     * Stores the observers of the item.
     */
    private List observers = new LinkedList();

    /**
     * Implements ChangeObservable.addObserver(ChangeObserver).
     */
    public void addObserver(ChangeObserver observer) {
        this.observers.add(observer);
    }

    /**
     * Implements ChangeObservable.removeObserver(ChangeObserver).
     */
    public void removeObserver(ChangeObserver observer) {
        this.observers.remove(observer);
    }

    /**
     * Notifies all observers of an update.
     */
    protected void notifyObservers() {
        Iterator it = this.observers.iterator();
        while (it.hasNext()) {
            ChangeObserver observer = (ChangeObserver) it.next();
            observer.update(this);
        }
    }

    /**
     * Draw method called to draw canvas item
     */
    public abstract void draw(Graphics2D g);

    /**
     * Returns true when the given point is on the item.
     */
    public abstract boolean containsPoint(Point2D point);

    /**
     * Returns the rectangular bounds of the canvas item.
     */
    abstract public Rectangle2D getCanvasBounds(Graphics2D g);

    /**
     * Returns true if the item should be raised on clicks.
     *
     * This is true per default, if a subclass should not be raised this method
     * can be overwritten returning false and Canvas will not raise it automatically.
     */
    public boolean hasAutoRaise() {
        return true;
    }
}