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
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
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
     * Reacts on a mouse movement between the two positions while the left button
     * is pressed.
     *
     * Unless it is overwritten in a derived class this will do nothing.
     */
    public void dragged(Point2D from, Point2D to) {
    }

    /**
     * Callback for getting notification about any click.
     *
     * Unless it is overwritten in a derived class this will do nothing.
     * Use this method only if you don't distinguish between single and
     * double clicks, otherwise you should use the more specific methods
     * singleClicked(Point2D) and doubleClicked(Point2D).
     */
    public void clicked(Point2D point) {
    }

    /**
     * Callback for getting notification about single clicks.
     *
     * Unless it is overwritten in a derived class this will do nothing.
     */
    public void singleClicked(Point2D point) {
    }

    /**
     * Callback for getting notification about double clicks.
     *
     * Unless it is overwritten in a derived class this will do nothing.
     */
    public void doubleClicked(Point2D point) {
    }

    /**
     * Callback for opening context menus.
     *
     * Unless it is overwritten in a derived class this will do nothing. It
     * will be called whenever a popup was requested on that item (e.g. by
     * clicking with the second mouse button on Windows).
     */
    public void openPopupMenu(Point2D canvasPosition, Point2D screenPosition) {
    }

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