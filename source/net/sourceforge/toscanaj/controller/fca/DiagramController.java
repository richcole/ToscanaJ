/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.NestedLineDiagram;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.observer.ChangeObservable;
import net.sourceforge.toscanaj.observer.ChangeObserver;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class encapsulates all code for handling the diagrams and filtering the
 * objects left after zooming.
 *
 * This is implemented as singleton, use getController() to access the only
 * instance. The inner class DiagramHistory can be used to be inserted into
 * a JList for displaying purposes.
 */
public class DiagramController implements ChangeObservable {
    /**
     * Stores the only instance of this class.
     */
    static private DiagramController singleton = new DiagramController();

    /**
     * Stores the diagram history.
     */
    private DiagramHistory history = new DiagramHistory();

    /**
     * Returns the only instance of this class.
     */
    static public DiagramController getController() {
        return singleton;
    }

    /**
     * Sets the number of diagrams used for nesting.
     *
     * This starts with zero (flat, non-nested diagram).
     */
    public void setNestingLevel(int level) {
        history.setNestingLevel(level);
    }

    /**
     * Returns the history as list model.
     *
     * This can be used directly in a JList for displaying purposes.
     */
    public DiagramHistory getDiagramHistory() {
        return history;
    }

    /**
     * Returns true if an undo step can be made.
     *
     * This is only true if we have past diagrams, if we have nesting we can
     * call back() but the user should not be allowed to, since this changes
     * nesting levels but is not an undo.
     *
     * @see #back()
     */
    public boolean undoIsPossible() {
        return history.canMoveUp();
    }

    /**
     * Adds a diagram to the history.
     *
     * If no diagram is open yet it will be used as current diagram, else it
     * will be added to the list of future diagrams.
     */
    public void addDiagram(Diagram2D diagram) {
        history.addDiagram(diagram);
    }

    /**
     * Removes the last diagram from the list.
     *
     * If this is a currently visible diagram this will include an undo operation.
     *
     * If no diagram is left a NoSuchElementException will be raised.
     */
    public void removeLastDiagram() {
        history.removeLastDiagram();
    }

    /**
     * Removes all diagrams: past, current and future from the history.
     */
    public void reset() {
        history.reset();
    }

    /**
     * Returns true if there a still diagrams to visit.
     */
    public boolean hasFutureDiagrams() {
        return history.hasFutureDiagrams();
        //return ;
    }

    /**
     * Moves forward to the next diagram.
     *
     * This is done by putting the outermost current one into the history and
     * getting a new one out of the list of future diagrams.
     *
     * @see #back()
     */
    public void next(Concept zoomedConcept) {
        history.next(zoomedConcept);

    }

    /**
     * Goes one step back in the history.
     *
     * This is the undo operation for next(). If we have multiple current diagrams
     * but no past diagrams left this will reduce the nesting level by one until
     * new diagrams are added.
     *
     * @see #next(Concept)
     * @see #undoIsPossible()
     */
    public void back() {
        history.back();
    }

    /**
     * Returns the current diagram to be displayed.
     *
     * This is currently an instance of SimpleLineDiagram which is filtered
     * by the extent of the zoomed concepts in the past diagrams. If there is
     * no diagram selected this will return null.
     */
    public Diagram2D getCurrentDiagram() {
        if (history.getNumberOfCurrentDiagrams() == 0) {
            // we don't have a diagram to display
            return null;
        }
        return getNestedDiagram(history.getNumberOfCurrentDiagrams() - 1);
    }

    /**
     * Returns a simple (flat) diagram created from the position in the list
     * of current diagrams.
     */
    protected Diagram2D getSimpleDiagram(int pos) {
        return history.getCurrentDiagram(pos);
    }

    /**
     * Returns a nested diagram using the list of current diagrams.
     *
     * The parameter pos determines the position in the list of current diagrams.
     */
    protected Diagram2D getNestedDiagram(int pos) {
        if (pos == 0) {
            // we have only a flat diagram left
            return getSimpleDiagram(0);
        }
        // else created nested diagram recursively
        return new NestedLineDiagram(getNestedDiagram(pos - 1), history.getCurrentDiagram(pos));
    }

    /**
     * Stores the observers of the controller.
     */
    private List observers = new LinkedList();

    /**
     * Implements ChangeObservable.addObserver(ChangeObserver).
     */
    public void addObserver(ChangeObserver observer) {
        this.history.addObserver(observer);
        this.observers.add(observer);
    }

    /**
     * Implements ChangeObservable.removeObserver(ChangeObserver).
     */
    public void removeObserver(ChangeObserver observer) {
        this.history.removeObserver(observer);
        this.observers.remove(observer);
    }

    /**
     * Notifies all observers of an update that changes the current diagram.
     *
     * This should not be called if the changes affect only future diagrams.
     */
    protected void notifyObservers() {
        Iterator it = this.observers.iterator();
        while (it.hasNext()) {
            ChangeObserver observer = (ChangeObserver) it.next();
            observer.update(this);
        }
    }
}
