package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.view.diagram.LabelObserver;

import java.awt.geom.Point2D;

/**
 * Interface for attaching LabelObserver instances.
 */
public interface LabelObservable{

    /**
     * Adds an observer.
     */
    public void addObserver(LabelObserver diagramObserver);
}