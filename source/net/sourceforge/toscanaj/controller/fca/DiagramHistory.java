/*
 * Date: 13.04.2002
 * Time: 22:59:12
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.observer.ChangeObservable;
import net.sourceforge.toscanaj.observer.ChangeObserver;

import javax.swing.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This stores the diagram references for visited, shown and forthcoming
 * diagrams and can be used as a model for JList components.
 */
public class DiagramHistory extends AbstractListModel implements  ChangeObservable  {

    /**
     * Used to store references to diagrams, including the concept used for
     * zooming in past diagrams.
     *
     * Beneath adding the concept reference (which is null for current and future
     * diagrams) this gives the references identity, otherwise we would get problems
     * in adding a diagram twice to the history.
     */
    public static class DiagramReference {
        /**
         * The diagram we refer to.
         */
        private Diagram2D diagram;

        /**
         * The concept the user zoomed into (null for current and future diagrams).
         */
        private Concept zoomedConcept;

        /**
         * Initialises a new reference.
         *
         * This is private so it can be called only by the outer class. Other
         * classes are allowed to use this class (and have to) but they can not
         * create instances.
         */
        private DiagramReference(Diagram2D diagram, Concept zoomedConcept) {
            this.diagram = diagram;
            this.zoomedConcept = zoomedConcept;
        }

        /**
         * Returns the diagram we refer to.
         */
        public Diagram2D getDiagram() {
            return this.diagram;
        }

        /**
         * Returns the concept the user zoomed into (null for current and future diagrams).
         */
        public Concept getZoomedConcept() {
            return this.zoomedConcept;
        }

        /**
         * Returns the diagram title for usage in a view.
         */
        public String toString() {
            return diagram.getTitle();
        }
    }


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
     * Stores the number of levels we nest diagrams.
     */
    private int nestingLevel = 0;

    public void setNestingLevel(int level) {
        this.nestingLevel = level;
        int lastPos = currentDiagrams.size() - 1;
        while( lastPos < level ) {
            if( futureDiagrams.isEmpty() ) {
                break; // nothing more to get
            }
            currentDiagrams.add(futureDiagrams.get(0));
            futureDiagrams.remove(0);
            lastPos++;
        }
        while( lastPos > level ) {
            futureDiagrams.add(0, currentDiagrams.get(lastPos));
            currentDiagrams.remove(lastPos);
            lastPos--;
        }
        fireContentsChanged(0,getSize()-1);
        notifyObservers();
    }

    public boolean hasPastDiagrams(){
        return !pastDiagrams.isEmpty();
    }

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
     * Returns true if the diagram is in the list of visited diagrams.
     */
    public boolean isInPast(DiagramReference diagram) {
        return this.pastDiagrams.contains(diagram);
    }

    /**
     * Returns true if the diagram is in the list of displayed diagrams.
     */
    public boolean isInCurrent(DiagramReference diagram) {
        return this.currentDiagrams.contains(diagram);
    }

    /**
     * Returns true if the diagram is in the list of diagrams still to be visited.
     */
    public boolean isInFuture(DiagramReference diagram) {
        return this.futureDiagrams.contains(diagram);
    }

    /**
     * Redirects to the AbstractListModel method.
     */
    void fireContentsChanged(int from, int to) {
        this.fireContentsChanged(this, from, to);
    }

    /**
     * Redirects to the AbstractListModel method.
     */
    void fireIntervalAdded(int from, int to) {
         this.fireIntervalAdded(this, from, to);
    }

    /**
     * Redirects to the AbstractListModel method.
     */
    void fireIntervalRemoved(int from, int to) {
        this.fireIntervalRemoved(this, from, to);
    }

    /**
     * Debug output.
     */
    public String toString() {
        String retVal = "Past Diagrams:\n";
        Iterator it = this.pastDiagrams.iterator();
        while(it.hasNext()) {
            retVal += it.next().toString() + "\n";
        }
        retVal += "Current Diagrams:\n";
        it = this.currentDiagrams.iterator();
        while(it.hasNext()) {
            retVal += it.next().toString() + "\n";
        }
        retVal += "Future Diagrams:\n";
        it = this.futureDiagrams.iterator();
        while(it.hasNext()) {
            retVal += it.next().toString() + "\n";
        }
        return retVal;
    }

    public void addDiagram(Diagram2D diagram) {
        if(currentDiagrams.size() <= this.nestingLevel) {
            currentDiagrams.add(new DiagramReference(diagram,null));
            notifyObservers();
        }
        else {
            futureDiagrams.add(new DiagramReference(diagram,null));
        }
        int lastPos = pastDiagrams.size() +
                      currentDiagrams.size() +
                      futureDiagrams.size() - 1;
        fireIntervalAdded(lastPos,lastPos);

    }

        /**
     * Stores the observers of the controller.
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
     * Notifies all observers of an update that changes the current diagram.
     * This should not be called if the changes affect only future diagrams.
     */
    protected void notifyObservers() {
        Iterator it = this.observers.iterator();
        while(it.hasNext()) {
            ChangeObserver observer = (ChangeObserver)it.next();
            observer.update(this);
        }
    }

    public void removeDiagram(int position) {
        if(position < pastDiagrams.size()) {
            pastDiagrams.remove(position);
            fireIntervalAdded(position,position);
            notifyObservers();
            return;
        }
        int pos = position - pastDiagrams.size();
        if(pos < currentDiagrams.size()) {
            return;
        }
        pos = pos - currentDiagrams.size();
        if( pos < futureDiagrams.size()) {
            futureDiagrams.remove(pos);
            fireIntervalAdded(position,position);
            return;
        }
        throw new NoSuchElementException("Tried to remove diagram beyond range");
    }

    public void removeLastDiagram() {
        if( !futureDiagrams.isEmpty() ) {
            futureDiagrams.remove(futureDiagrams.size()-1);
            fireIntervalRemoved(getSize(),getSize());
            return;
        }
        // no future diagrams, check if we can undo
        if( pastDiagrams.size() + currentDiagrams.size() > 1 ) {
            back();
            futureDiagrams.remove(0);
            fireIntervalRemoved(getSize(),getSize());
            return;
        }
        // no future diagrams, no undo -- do we have at least one diagram?
        if( !currentDiagrams.isEmpty() ) {
            currentDiagrams.clear();
            fireIntervalRemoved(getSize(),getSize());
            notifyObservers();
            return;
        }
        throw new NoSuchElementException("The list of diagrams is already empty.");
    }

    public void reset() {
        int last = getSize()-1;
        if(last == -1) {
            return;
        }
        pastDiagrams.clear();
        currentDiagrams.clear();
        futureDiagrams.clear();
        fireIntervalRemoved(0,last);
        notifyObservers();
    }

    public boolean hasFutureDiagrams() {
        return !futureDiagrams.isEmpty();
    }

    public void next(Concept zoomedConcept) {
        if(futureDiagrams.isEmpty()) {
            if(currentDiagrams.size() == 1) {
                // nothing to go to
                ///@todo Give feedback, when know how
                return;
            }
            DiagramReference oldRef = (DiagramReference) currentDiagrams.get(0);
            pastDiagrams.add(new DiagramReference( oldRef.getDiagram(), zoomedConcept ) );
            currentDiagrams.remove(0);
        }
        else {
            // move one of the future diagrams in
            DiagramReference oldRef = (DiagramReference) currentDiagrams.get(0);
            pastDiagrams.add(new DiagramReference( oldRef.getDiagram(), zoomedConcept ) );
            currentDiagrams.remove(0);
            currentDiagrams.add(futureDiagrams.get(0));
            futureDiagrams.remove(0);
        }
        fireContentsChanged(0,getSize()-1);
        notifyObservers();
    }

    public void back() {

        if(pastDiagrams.size() + currentDiagrams.size() < 2) {
            throw new NoSuchElementException("No diagram left to go back to.");
        }
        int lastPos = currentDiagrams.size() - 1;
        if( lastPos == this.nestingLevel ) { // we have our nesting level
            futureDiagrams.add(0,currentDiagrams.get(lastPos));
            currentDiagrams.remove(lastPos);
        }
        if( !pastDiagrams.isEmpty() ) {
            // we have something to go back to, otherwise we just lose nesting
            currentDiagrams.add(0,pastDiagrams.get(
                                        pastDiagrams.size()-1));
            pastDiagrams.remove(pastDiagrams.size()-1);
        }
        fireContentsChanged(0,getSize()-1);
        notifyObservers();
    }

    public int getNumberOfCurrentDiagrams(){
          return currentDiagrams.size();
    }

    public Diagram2D getCurrentDiagram(int pos){
        DiagramReference ref = (DiagramReference) currentDiagrams.get(pos);
        return ref.getDiagram();
    }

    public interface ConceptVisitor{
        void visitConcept(Concept concept);
    }

    public void visitZoomedConcepts(ConceptVisitor visitor){
        Iterator it = pastDiagrams.iterator();
        while(it.hasNext()) {
            DiagramReference curRef = (DiagramReference) it.next();
            Concept currentZoomedConcept = curRef.getZoomedConcept();
            visitor.visitConcept(currentZoomedConcept);
        }
    }

}
