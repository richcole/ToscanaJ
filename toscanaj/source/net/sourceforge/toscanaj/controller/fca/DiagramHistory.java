/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.AbstractListModel;

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.observer.ChangeObservable;
import net.sourceforge.toscanaj.observer.ChangeObserver;

/**
 * This stores the diagram references for visited, shown and forthcoming
 * diagrams and can be used as a model for JList components.
 * 
 * @todo Use event broker instead of being an observable.
 */
public class DiagramHistory extends AbstractListModel implements
ChangeObservable {

    private final List<Object> diagrams = new ArrayList<Object>();

    private int currStartPosition;
    private int firstFutureDiagramPosition;

    /**
     * Creates an empty list of diagrams.
     */
    public DiagramHistory() {
        init();
    }

    private void init() {
        this.diagrams.clear();
        this.currStartPosition = 0;
        this.firstFutureDiagramPosition = 0;
        this.nestingLevel = 0;
    }

    /**
     * Stores the number of levels we nest diagrams.
     */
    private int nestingLevel = 0;

    public void setNestingLevel(final int level) {
        if (level < 0) {
            throw new IllegalArgumentException(
                    "Nesting level should be greater than zero, and was :"
                    + level);
        }

        this.nestingLevel = level;
        int lastPos = getNumberOfCurrentDiagrams() - 1;
        while (lastPos < level) {
            if (!hasFutureDiagrams()) {
                break; // nothing more to get
            }
            this.firstFutureDiagramPosition++;
            lastPos++;
        }
        while (lastPos > level) {
            this.firstFutureDiagramPosition--;
            lastPos--;
        }
        fireContentsChanged();
        notifyObservers();
    }

    /**
     * Returns the number of diagrams nested in an outer diagram.
     */
    public int getNestingLevel() {
        return this.nestingLevel;
    }

    public boolean hasPastDiagrams() {
        return this.currStartPosition > 0;
    }

    /**
     * Implements AbstractListModel.getSize().
     */
    public int getSize() {
        return this.diagrams.size();
    }

    public boolean isEmpty() {
        return 0 == getSize();
    }

    /**
     * Implements AbstractListModel.getElementAt(int).
     */
    public Object getElementAt(final int position) {
        return this.diagrams.get(position);
    }

    public DiagramReference getReferenceAt(final int elementPosition) {
        return (DiagramReference) getElementAt(elementPosition);
    }

    /**
     * Returns true if the diagram is in the list of visited diagrams.
     */
    public boolean isInPast(final int elementPosition) {
        return (elementPosition < this.currStartPosition)
        && (elementPosition >= 0);
    }

    /**
     * Returns true if the diagram is in the list of displayed diagrams.
     */
    public boolean isInCurrent(final DiagramReference diagram) {
        for (int i = this.currStartPosition; i < this.firstFutureDiagramPosition; i++) {
            if (this.diagrams.get(i) == diagram) {
                return true;
            }
        }
        return false;
    }

    public boolean isInCurrent(final int elementPosition) {
        return isInCurrent((DiagramReference) this.diagrams
                .get(elementPosition));
    }

    /**
     * Returns true if the diagram is in the list of diagrams still to be
     * visited.
     */
    public boolean isInFuture(final DiagramReference diagram) {
        for (int i = this.firstFutureDiagramPosition; i < this.diagrams.size(); i++) {
            if (this.diagrams.get(i) == diagram) {
                return true;
            }
        }
        return false;
    }

    public boolean isInFuture(final int elementPosition) {
        return isInFuture((DiagramReference) this.diagrams.get(elementPosition));
    }

    public boolean hasFutureDiagrams() {
        return getSize() > this.firstFutureDiagramPosition;
    }

    public int getNumberOfCurrentDiagrams() {
        return this.firstFutureDiagramPosition - this.currStartPosition;
    }

    public int getFirstCurrentDiagramPosition() {
        return this.currStartPosition;
    }

    public Diagram2D getCurrentDiagram(final int pos) {
        final int elementPosition = this.currStartPosition + pos;
        if (isInCurrent(elementPosition)) {
            return getReferenceAt(elementPosition).getDiagram();
        }
        throw new NoSuchElementException(
                "There are no current diagram with index:" + pos);
    }

    /**
     * Debug output.
     */
    @Override
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

    public void addDiagram(final Diagram2D diagram) {
        if (getSize() == 0) {
            insertDiagram(0, diagram);
        } else {
            insertDiagram(getSize(), diagram);
        }
    }

    private boolean isFirstFutureDiagram(final int newDiagramIndex) {
        return this.firstFutureDiagramPosition == newDiagramIndex;
    }

    public void removeLastDiagram() {
        if (getSize() == 0) {
            throw new NoSuchElementException(
            "The list of diagrams is already empty.");
        }
        final int lastPosition = getSize() - 1;
        if (isInCurrent(lastPosition)) {
            this.firstFutureDiagramPosition--;
            if (this.currStartPosition >= this.firstFutureDiagramPosition) {
                this.currStartPosition = Math.max(
                        this.firstFutureDiagramPosition - 1, 0);
            }
        }
        this.diagrams.remove(lastPosition);
        fireIntervalRemoved(getSize(), getSize());
        notifyObservers();
    }

    public void moveDiagram(final int from, final int to) {
        if (getSize() == 0) {
            throw new NoSuchElementException(
            "The list of diagrams is already empty.");
        }
        if (isInPast(from) || isInPast(to)) {
            throw new RuntimeException("Trying to change past");
        }
        final Object item = this.diagrams.remove(from);
        this.diagrams.add(to, item);
        fireIntervalRemoved(getSize(), getSize());
        notifyObservers();
    }

    public void reset() {
        final int last = getSize() - 1;
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
        if ((!hasFutureDiagrams()) && (getNumberOfCurrentDiagrams() == 1)) {
            // nothing to go to
            return false;
        }
        return true;
    }

    public void next(final Concept zoomedConcept) {
        if (!canPerformNext()) {
            throw new RuntimeException("No next diagram to go to");
        }
        getReferenceAt(this.currStartPosition).setFilterConcept(zoomedConcept);
        if (shouldChangeCurrentStartDiagram()) {
            this.currStartPosition++;
        }
        if (hasFutureDiagrams()) {
            this.firstFutureDiagramPosition++;
        }

        fireContentsChanged();
        notifyObservers();
    }

    private boolean shouldChangeCurrentStartDiagram() {
        return this.currStartPosition > 0
        || (getNumberOfCurrentDiagrams() > getNestingLevel());
    }

    public void back() {
        if (this.firstFutureDiagramPosition <= 0) {
            throw new NoSuchElementException("No diagram left to go back to.");
        }
        final int lastPos = getNumberOfCurrentDiagrams() - 1;
        if (lastPos == getNestingLevel()) { // we have our nesting level
            this.firstFutureDiagramPosition--;
        }
        if (hasPastDiagrams()) {
            this.currStartPosition--;
            // we have something to go back to, otherwise we just lose nesting
        }
        fireContentsChanged();
        notifyObservers();
    }

    public interface ConceptVisitor<O, A> {
        void visitConcept(Concept<O, A> concept);
    }

    public void visitZoomedConcepts(final ConceptVisitor<?, ?> visitor) {
        for (int i = 0; isInPast(i); i++) {
            visitor.visitConcept(getReferenceAt(i).getFilterConcept());
        }
    }

    /**
     * Stores the observers of the controller.
     */
    private final List<ChangeObserver> observers = new ArrayList<ChangeObserver>();

    /**
     * Implements ChangeObservable.addObserver(ChangeObserver).
     */
    public void addObserver(final ChangeObserver observer) {
        this.observers.add(observer);
    }

    /**
     * Implements ChangeObservable.removeObserver(ChangeObserver).
     */
    public void removeObserver(final ChangeObserver observer) {
        this.observers.remove(observer);
    }

    /**
     * Notifies all observers of an update that changes the current diagram.
     * This should not be called if the changes affect only future diagrams.
     */
    protected void notifyObservers() {
        // avoid stupid ConcurrentModificationExceptions by operating on copy of
        // list
        final List<ChangeObserver> observerCopy = new ArrayList<ChangeObserver>();
        observerCopy.addAll(this.observers);
        while (!observerCopy.isEmpty()) {
            final ChangeObserver observer = observerCopy.remove(0);
            observer.update(this);
        }
    }

    /**
     * Redirects to the AbstractListModel method.
     */
    private void fireContentsChanged(final int from, final int to) {
        this.fireContentsChanged(this, from, to);
    }

    /**
     * Redirects to the AbstractListModel method.
     */
    private void fireIntervalAdded(final int from, final int to) {
        this.fireIntervalAdded(this, from, to);
    }

    /**
     * Redirects to the AbstractListModel method.
     */
    private void fireIntervalRemoved(final int from, final int to) {
        this.fireIntervalRemoved(this, from, to);
    }

    private void fireContentsChanged() {
        fireContentsChanged(0, getSize() - 1);
    }

    public boolean canMoveUp() {
        return hasPastDiagrams();
    }

    public String getTextualDescription() {
        String comments = "";
        final String lineSeparator = System.getProperty("line.separator");
        final DiagramHistory diagramHistory = DiagramController.getController()
        .getDiagramHistory();
        final int firstCurrentPos = diagramHistory
        .getFirstCurrentDiagramPosition();
        if (firstCurrentPos > 1) {
            comments += "Visited diagrams and selected attributes:"
                + lineSeparator;
        } else if (firstCurrentPos == 1) {
            comments += "Visited diagram and selected attributes:"
                + lineSeparator;
        } else {
            // user has no visited diagrams. display nothing
        }
        for (int i = 0; i <= firstCurrentPos - 1; i++) {
            final DiagramReference diagramReference = diagramHistory
            .getReferenceAt(i);
            comments += (i + 1) + ") "
            + diagramReference.getDiagram().getTitle() + lineSeparator;
            final Concept concept = diagramReference.getFilterConcept();
            final Iterator attrIt = concept.getIntentIterator();
            while (attrIt.hasNext()) {
                final FCAElement curAttr = (FCAElement) attrIt.next();
                comments += "   - " + curAttr.getData().toString()
                + lineSeparator;
            }
        }
        final int numCurDiag = diagramHistory.getNumberOfCurrentDiagrams();
        comments += lineSeparator;
        if (numCurDiag > 1) {
            comments += "Current diagrams:" + lineSeparator;
        } else {
            comments += "Current diagram:" + lineSeparator;
        }
        for (int i = 0; i < numCurDiag; i++) {
            comments += (i + 1)
            + ") "
            + diagramHistory.getElementAt(i + firstCurrentPos)
            .toString() + lineSeparator;
        }
        return comments;

    }

    public void insertDiagram(final int index, final Diagram2D diagram2D) {
        if (isInPast(index)) {
            throw new RuntimeException("Trying to change past");
        }

        this.diagrams.add(index, new DiagramReference(diagram2D, null));
        if (getNumberOfCurrentDiagrams() <= getNestingLevel()
                && isFirstFutureDiagram(index)) {
            this.firstFutureDiagramPosition++;
            notifyObservers();
        }
        fireIntervalAdded(index, index);
    }
}
