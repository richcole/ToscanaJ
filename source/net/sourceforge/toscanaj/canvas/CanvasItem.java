package net.sourceforge.toscanaj.canvas;

import net.sourceforge.toscanaj.observer.ChangeObservable;
import net.sourceforge.toscanaj.observer.ChangeObserver;
import net.sourceforge.toscanaj.view.diagram.ToscanajGraphics2D;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract class to draw 2D graph items.
 *
 * CanvasItems can be put on a DrawingCanvas where they will have a z-order and
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
        while(it.hasNext()) {
            ChangeObserver observer = (ChangeObserver)it.next();
            observer.update(this);
        }
    }

    /**
     * Draw method called to draw canvas item
     */
    public abstract void draw(ToscanajGraphics2D g);

    /**
     * Returns true when the given point is on the item.
     */
    public abstract boolean containsPoint(Point2D point);

    /**
     * Moves the label by the given distance.
     *
     * Unless it is overwritten in a derived class this will do nothing.
     */
    public void moveBy(double deltaX, double deltaY) {
    }

    /**
     * Callback for getting notification about single clicks.
     *
     * Unless it is overwritten in a derived class this will do nothing.
     */
    public void clicked(Point2D point) {
    }

    /**
     * Callback for getting notification about double clicks.
     *
     * Unless it is overwritten in a derived class this will do nothing.
     */
    public void doubleClicked(Point2D point) {
    }

    /**
     * Returns the rectangular bounds of the canvas item.
     */
    abstract public Rectangle2D getBounds(ToscanajGraphics2D g);
}