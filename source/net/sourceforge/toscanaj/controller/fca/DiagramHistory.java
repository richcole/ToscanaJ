package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.lattice.Concept;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.swing.AbstractListModel;

/**
 * A list of all diagrams that have been visited, are viewed at the moment or
 * are enlisted to be used later.
 *
 * This is implemented as singleton, use getDiagramHistory() to access the
 * instance.
 */
public class DiagramHistory extends AbstractListModel {
    /**
     * Stores the only instance of this class.
     */
    static private DiagramHistory singleton = new DiagramHistory();

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
    private DiagramHistory() {
    }

    /**
     * Returns the only instance of this class.
     */
    static public DiagramHistory getDiagramHistory() {
        return singleton;
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
        int lastPos = pastDiagrams.size() + currentDiagrams.size() + futureDiagrams.size() - 1;
        this.fireIntervalAdded(this,lastPos,lastPos);
    }

    /**
     * Removes a diagram from the model.
     *
     * Throws NoSuchElementException if there is no such diagram. If it is tried
     * to remove the current diagram nothing will happen.
     *
     * @TODO Find somthing useful when removing the current diagram.
     */
    public void removeDiagram(int position) {
        if(position < this.pastDiagrams.size()) {
            this.pastDiagrams.remove(position);
            this.fireIntervalAdded(this,position,position);
            return;
        }
        int pos = position - this.pastDiagrams.size();
        if(pos < this.currentDiagrams.size()) {
            return;
        }
        pos = pos - this.currentDiagrams.size();
        if( pos < this.futureDiagrams.size()) {
            this.futureDiagrams.remove(pos);
            this.fireIntervalAdded(this,position,position);
            return;
        }
        throw new NoSuchElementException("Tried to remove diagram beyond range");
    }

    /**
     * Removes all diagrams: past, current and future from the history.
     */
    public void clear() {
        int last = this.getSize()-1;
        if(last == -1) {
            return;
        }
        this.pastDiagrams.clear();
        this.currentDiagrams.clear();
        this.futureDiagrams.clear();
        this.fireIntervalRemoved(this,0,last);
    }

    /**
     * Returns true if there a still diagrams to visit.
     */
    public boolean hasFutureDiagrams() {
        return this.futureDiagrams.size() != 0;
    }

    /**
     * Moves forward to the next diagram.
     *
     * This is done by putting the outermost current one into the history and
     * getting a new one out of the list of future diagrams.
     */
    public void next(Concept zoomedConcept)
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