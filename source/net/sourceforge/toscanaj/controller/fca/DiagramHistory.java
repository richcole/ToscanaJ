/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.observer.ChangeObservable;
import net.sourceforge.toscanaj.observer.ChangeObserver;
import util.CollectionFactory;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This stores the diagram references for visited, shown and forthcoming
 * diagrams and can be used as a model for JList components.
 *
 * @todo Use event broker instead of being an observable.
 */
public class DiagramHistory extends AbstractListModel implements ChangeObservable {

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
         * This is private so it can be called only by the outer class. Other
         * classes are allowed to use this class (and have to) but they can not
         * change zoomed concept.
         */

        private void setZoomedConcept(Concept zoomedConcept) {
            this.zoomedConcept = zoomedConcept;
        }

        /**
         * Returns the diagram title for usage in a view.
         */
        public String toString() {
            return diagram.getTitle();
        }
    }


    private List diagrams = new LinkedList();

    private int currStartPosition;
    private int firstFutureDiagramPosition;

    /**
     * Creates an empty list of diagrams.
     */
    public DiagramHistory() {
        init();
    }

    private void init() {
        diagrams.clear();
        currStartPosition = 0;
        firstFutureDiagramPosition = 0;
        nestingLevel = 0;
    }

    /**
     * Stores the number of levels we nest diagrams.
     */
    private int nestingLevel = 0;

    public void setNestingLevel(int level) {
        if (level < 0) {
            throw new IllegalArgumentException("Nesting level should be greater than zero, and was :" + level);
        }

        this.nestingLevel = level;
        int lastPos = getNumberOfCurrentDiagrams() - 1;
        while (lastPos < level) {
            if (!hasFutureDiagrams()) {
                break; // nothing more to get
            }
            firstFutureDiagramPosition++;
            lastPos++;
        }
        while (lastPos > level) {
            firstFutureDiagramPosition--;
            lastPos--;
        }
        fireContentsChanged();
        notifyObservers();
    }

    public int getNestingLevel() {
        return nestingLevel;
    }

    public boolean hasPastDiagrams() {
        return currStartPosition > 0;
    }


    /**
     * Implements AbstractListModel.getSize().
     */
    public int getSize() {
        return diagrams.size();
    }


    public boolean isEmpty() {
        return 0 == getSize();
    }

    /**
     * Implements AbstractListModel.getElementAt(int).
     */
    public Object getElementAt(int position) {
        return this.diagrams.get(position);
    }

    private DiagramReference getReferenceAt(int elementPosition) {
        return (DiagramReference) getElementAt(elementPosition);
    }

    /**
     * Returns true if the diagram is in the list of visited diagrams.
     */
    public boolean isInPast(int elementPosition) {
        return elementPosition < currStartPosition;
    }

    /**
     * Returns true if the diagram is in the list of displayed diagrams.
     */
    public boolean isInCurrent(DiagramReference diagram) {
        return isInCurrent(this.diagrams.indexOf(diagram));
    }

    public boolean isInCurrent(int elementPosition) {
        return elementPosition >= currStartPosition && currStartPosition >= 0 && elementPosition < firstFutureDiagramPosition;
    }

    /**
     * Returns true if the diagram is in the list of diagrams still to be visited.
     */
    public boolean isInFuture(DiagramReference diagram) {
        return isInFuture(diagrams.indexOf(diagram));
    }

    public boolean isInFuture(int elementPosition) {
        return elementPosition >= firstFutureDiagramPosition;
    }

    public boolean hasFutureDiagrams() {
        return getSize() > firstFutureDiagramPosition;
    }


    public int getNumberOfCurrentDiagrams() {
        return firstFutureDiagramPosition - currStartPosition;
    }

    public int getFirstCurrentDiagramPosition() {
        return currStartPosition;
    }

    public Diagram2D getCurrentDiagram(int pos) {
        final int elementPosition = currStartPosition + pos;
        if (isInCurrent(elementPosition)) {
            return getReferenceAt(elementPosition).getDiagram();
        }
        throw new NoSuchElementException("There are no current diagram with index:" + pos);
    }


    /**
     * Debug output.
     */
    public String toString() {
        final String newLine = "\n";
        int i = 0;
        String retVal = "Past Diagrams:\n";
        while (isInPast(i)) {
            retVal += getReferenceAt(i).toString() + newLine;
            i++;
        }
        retVal += "Current Diagrams:\n";
        while (isInCurrent(i)) {
            retVal += getReferenceAt(i).toString() + newLine;
            i++;
        }
        retVal += "Future Diagrams:\n";
        while (isInFuture(i)) {
            retVal += getReferenceAt(i).toString() + newLine;
            i++;
        }
        return retVal;
    }


    public void addDiagram(Diagram2D diagram) {

        diagrams.add(new DiagramReference(diagram, null));
        int newDiagramIndex = getSize() - 1;
        if (getNumberOfCurrentDiagrams() <= getNestingLevel() && isFirstFutureDiagram(newDiagramIndex)) {
            firstFutureDiagramPosition++;
            notifyObservers();
        }
        fireIntervalAdded(newDiagramIndex, newDiagramIndex);

    }

    private boolean isFirstFutureDiagram(int newDiagramIndex) {
        return firstFutureDiagramPosition == newDiagramIndex;
    }

    public void removeLastDiagram() {
        if (getSize() == 0) {
            throw new NoSuchElementException("The list of diagrams is already empty.");
        }
        final int lastPosition = getSize() - 1;
        if (isInCurrent(lastPosition)) {
            firstFutureDiagramPosition--;
            if (currStartPosition >= firstFutureDiagramPosition) {
                currStartPosition = Math.max(firstFutureDiagramPosition - 1, 0);
            }
        }
        diagrams.remove(lastPosition);
        fireIntervalRemoved(getSize(), getSize());
        notifyObservers();
    }

    public void moveDiagram(int from, int to) {
        if (getSize() == 0) {
            throw new NoSuchElementException("The list of diagrams is already empty.");
        }
        if (isInPast(from) || isInPast(to)) {
            throw new RuntimeException("Trying to change past");
        }
        Object item = diagrams.remove(from);
        diagrams.add(to, item);
        fireIntervalRemoved(getSize(), getSize());
        notifyObservers();
    }

    public void reset() {
        int last = getSize() - 1;
        if (last == -1) {
            return;
        }
        init();
        fireIntervalRemoved(0, last);
        notifyObservers();
    }


    private boolean canPerformNext() {
        if (isEmpty()) {
            return false;
        }
        if ((!hasFutureDiagrams()) &&
                (getNumberOfCurrentDiagrams() == 1)) {
            // nothing to go to
            ///@todo Give feedback, when know how
            return false;
        }
        return true;
    }

    public void next(Concept zoomedConcept) {
        if (!canPerformNext()) {
            return;
        }
        getReferenceAt(currStartPosition).setZoomedConcept(zoomedConcept);
        if (shouldChangeCurrentStartDiagram()) {
            currStartPosition++;
        }
        if (hasFutureDiagrams()) {
            firstFutureDiagramPosition++;
        }

        fireContentsChanged();
        notifyObservers();
    }

    private boolean shouldChangeCurrentStartDiagram() {
        return currStartPosition > 0 || (getNumberOfCurrentDiagrams() > getNestingLevel());
    }

    public void back() {
        if (firstFutureDiagramPosition <= 0) {
            throw new NoSuchElementException("No diagram left to go back to.");
        }
        int lastPos = getNumberOfCurrentDiagrams() - 1;
        if (lastPos == getNestingLevel()) { // we have our nesting level
            firstFutureDiagramPosition--;
        }
        if (hasPastDiagrams()) {
            currStartPosition--;
            // we have something to go back to, otherwise we just lose nesting
        }
        fireContentsChanged();
        notifyObservers();
    }

    public interface ConceptVisitor {
        void visitConcept(Concept concept);
    }

    public void visitZoomedConcepts(ConceptVisitor visitor) {
        for (int i = 0; isInPast(i); i++) {
            visitor.visitConcept(getReferenceAt(i).getZoomedConcept());
        }
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
        // avoid stupid ConcurrentModificationExceptions by operating on copy of list
        List observerCopy = CollectionFactory.createDefaultList();
        observerCopy.addAll(observers);
        while (!observerCopy.isEmpty()) {
            ChangeObserver observer = (ChangeObserver) observerCopy.remove(0);
            observer.update(this);
        }
    }

    /**
     * Redirects to the AbstractListModel method.
     */
    private void fireContentsChanged(int from, int to) {
        this.fireContentsChanged(this, from, to);
    }

    /**
     * Redirects to the AbstractListModel method.
     */
    private void fireIntervalAdded(int from, int to) {
        this.fireIntervalAdded(this, from, to);
    }

    /**
     * Redirects to the AbstractListModel method.
     */
    private void fireIntervalRemoved(int from, int to) {
        this.fireIntervalRemoved(this, from, to);
    }

    private void fireContentsChanged() {
        fireContentsChanged(0, getSize() - 1);
    }

    public boolean canMoveUp() {
        return hasPastDiagrams() || (getFirstCurrentDiagramPosition() == 0 && getNumberOfCurrentDiagrams() > 1);
    }


}
