package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.diagram.AttributeLabelInfo;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.ObjectLabelInfo;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;

import net.sourceforge.toscanaj.model.lattice.Concept;
// testing only:
import net.sourceforge.toscanaj.model.lattice.MemoryMappedConcept;

import net.sourceforge.toscanaj.observer.ChangeObservable;
import net.sourceforge.toscanaj.observer.ChangeObserver;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.swing.AbstractListModel;

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
     * This stores the diagram references for visited, shown and forthcoming
     * diagrams and can be used as a model for JList components.
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
        private DiagramHistory() {
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
    }

    /**
     * Used to store references to diagrams, including the concept used for
     * zooming in past diagrams.
     *
     * Beneath adding the concept reference (which is null for current and future
     * diagrams) this gives the references identity, otherwise we would get problems
     * in adding a diagram twice to the history.
     */
    public class DiagramReference {
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
     * Returns the history as list model.
     *
     * This can be used directly in a JList for displaying purposes.
     */
    public DiagramHistory getDiagramHistory() {
        return history;
    }

    /**
     * Adds a diagram to the history.
     *
     * If no diagram is open yet it will be used as current diagram, else it
     * will be added to the list of future diagrams.
     */
    public void addDiagram(Diagram2D diagram){
        if(history.currentDiagrams.isEmpty()) {
            history.currentDiagrams.add(new DiagramReference(diagram,null));
        }
        else {
            history.futureDiagrams.add(new DiagramReference(diagram,null));
        }
        int lastPos = history.pastDiagrams.size() +
                      history.currentDiagrams.size() +
                      history.futureDiagrams.size() - 1;
        history.fireIntervalAdded(lastPos,lastPos);
        notifyObservers();
    }

    /**
     * Removes a diagram from the model.
     *
     * Throws NoSuchElementException if there is no such diagram. If it is tried
     * to remove the current diagram nothing will happen.
     *
     * @TODO Find something useful when removing the current diagram.
     */
    public void removeDiagram(int position) {
        if(position < history.pastDiagrams.size()) {
            history.pastDiagrams.remove(position);
            history.fireIntervalAdded(position,position);
            notifyObservers();
            return;
        }
        int pos = position - history.pastDiagrams.size();
        if(pos < history.currentDiagrams.size()) {
            return;
        }
        pos = pos - history.currentDiagrams.size();
        if( pos < history.futureDiagrams.size()) {
            history.futureDiagrams.remove(pos);
            history.fireIntervalAdded(position,position);
            notifyObservers();
            return;
        }
        throw new NoSuchElementException("Tried to remove diagram beyond range");
    }

    /**
     * Removes all diagrams: past, current and future from the history.
     */
    public void reset() {
        int last = history.getSize()-1;
        if(last == -1) {
            return;
        }
        history.pastDiagrams.clear();
        history.currentDiagrams.clear();
        history.futureDiagrams.clear();
        history.fireIntervalRemoved(0,last);
        notifyObservers();
    }

    /**
     * Returns true if there a still diagrams to visit.
     */
    public boolean hasFutureDiagrams() {
        return history.futureDiagrams.size() != 0;
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
        if(history.futureDiagrams.isEmpty()) {
            throw new NoSuchElementException("No diagram left");
        }
        DiagramReference oldRef = (DiagramReference) history.currentDiagrams.get(0);
        history.pastDiagrams.add(new DiagramReference( oldRef.getDiagram(), zoomedConcept ) );
        history.currentDiagrams.remove(0);
        history.currentDiagrams.add(history.futureDiagrams.get(0));
        history.futureDiagrams.remove(0);
        history.fireContentsChanged(0,history.getSize()-1);
        notifyObservers();
    }

    /**
     * Returns the current diagram to be displayed.
     *
     * This is currently an instance of SimpleLineDiagram which is filtered
     * by the extent of the zoomed concepts in the past diagrams. If there is
     * no diagram selected this will return null.
     */
    public Diagram2D getCurrentDiagram() {
        if(history.currentDiagrams.size() == 0) {
            // we don't have a diagram to display
            return null;
        }
        DiagramReference ref = (DiagramReference) history.currentDiagrams.get(0);
        Diagram2D diag = ref.getDiagram();
        SimpleLineDiagram retVal = new SimpleLineDiagram();
        Concept filter = null;
        Iterator it = history.pastDiagrams.iterator();
        while(it.hasNext()) {
            DiagramReference curRef = (DiagramReference) it.next();
            Concept curZC = curRef.getZoomedConcept();
            filter = curZC.filterByExtent(filter);
        }
        Hashtable nodeMap = new Hashtable();

        retVal.setTitle(diag.getTitle());
        for(int i = 0; i<diag.getNumberOfNodes(); i++) {
            DiagramNode oldNode = diag.getNode(i);
            DiagramNode newNode = new DiagramNode( oldNode.getPosition(),
                                                   oldNode.getConcept().filterByExtent(filter),
                                                   oldNode.getAttributeLabelInfo(),
                                                   oldNode.getObjectLabelInfo() );
            retVal.addNode(newNode);
            nodeMap.put(oldNode,newNode);
        }
        for(int i = 0; i < diag.getNumberOfLines(); i++) {
            DiagramLine line = diag.getLine(i);
            retVal.addLine( (DiagramNode) nodeMap.get(line.getFromNode()),
                            (DiagramNode) nodeMap.get(line.getToNode()));
        }
        return retVal;
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
     * Notifies all observers of an update.
     */
    protected void notifyObservers() {
        Iterator it = this.observers.iterator();
        while(it.hasNext()) {
            ChangeObserver observer = (ChangeObserver)it.next();
            observer.update(this);
        }
    }
}