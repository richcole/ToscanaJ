package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.model.diagram.Diagram2D;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.swing.AbstractListModel;

/**
 * A list of all diagrams that have been visited, are viewed at the moment or
 * are enlisted to be used later.
 */
public class DiagramHistory extends AbstractListModel {
    /**
     * Stores the diagrams that have already been visited.
     */
    private List pastDiagrams = new LinkedList();

    /**
     * Stores the diagrams that are in use.
     */
    private List currentDiagrams = new LinkedList();

    /**
     * Stores the diagrams that are scheduled to come.
     */
    private List futureDiagrams = new LinkedList();

    /**
     * Creates an empty list of diagrams.
     */
    public DiagramHistory() {
    }

    /**
     * Implements AbstractListModel.getSize().
     */
    public int getSize() {
        return this.pastDiagrams.size() +
               this.currentDiagrams.size() +
               this.futureDiagrams.size();
    }

    /**
     * Implements AbstractListModel.getElementAt(int).
     */
    public Object getElementAt(int position) {
        if(position < this.pastDiagrams.size()) {
            return this.pastDiagrams.get(position);
        }
        int pos = position - this.pastDiagrams.size();
        if(pos < this.currentDiagrams.size()) {
            return this.currentDiagrams.get(pos);
        }
        pos = pos - this.currentDiagrams.size();
        return this.futureDiagrams.get(pos);
    }

    /**
     * Adds a diagram to the list of forthcoming diagrams.
     */
    public void addFutureDiagram(Diagram2D diagram){
        this.futureDiagrams.add(diagram);
    }

    /**
     * Moves forward to the next diagram.
     *
     * This is done by putting the outermost current one into the history and
     * getting a new one out of the list of future diagrams.
     */
    public void next()
           throws NoSuchElementException
    {
        if(this.futureDiagrams.isEmpty()) {
            throw new NoSuchElementException("No diagram left");
        }
        if(!this.currentDiagrams.isEmpty()) {
            this.pastDiagrams.add(this.currentDiagrams.get(0));
            this.currentDiagrams.remove(0);
        }
        this.currentDiagrams.add(this.futureDiagrams.get(0));
        this.futureDiagrams.remove(0);
    }

    /**
     * Returns an iterator on the list of already visited diagrams.
     */
    public Iterator getPastDiagramsIterator() {
        return this.pastDiagrams.iterator();
    }

    /**
     * Returns an iterator on the list of currently used diagrams.
     *
     * Can be more than one if a nested view is used.
     */
    public Iterator getCurrentDiagramsIterator() {
        return this.currentDiagrams.iterator();
    }

    /**
     * Returns an iterator on the list of forthcoming diagrams (for zooming).
     */
    public Iterator getFutureDiagramsIterator() {
        return this.futureDiagrams.iterator();
    }

    /**
     * Returns true if the diagram is in the list of visited diagrams.
     */
    public boolean isInPast(Diagram2D diagram) {
        return this.pastDiagrams.contains(diagram);
    }

    /**
     * Returns true if the diagram is in the list of displayed diagrams.
     */
    public boolean isInCurrent(Diagram2D diagram) {
        return this.currentDiagrams.contains(diagram);
    }

    /**
     * Returns true if the diagram is in the list of diagrams still to be visited.
     */
    public boolean isInFuture(Diagram2D diagram) {
        return this.futureDiagrams.contains(diagram);
    }
}