package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.diagram.AttributeLabelInfo;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.NestedLineDiagram;
import net.sourceforge.toscanaj.model.diagram.ObjectLabelInfo;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;

import net.sourceforge.toscanaj.model.lattice.AbstractConceptImplementation;
import net.sourceforge.toscanaj.model.lattice.Concept;

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
     * Constant for setFilterMethod(int).
     *
     * @see setFilterMethod(int)
     */
    static public int FILTER_CONTINGENT = 0;

    /**
     * Constant for setFilterMethod(int).
     *
     * @see setFilterMethod(int)
     */
    static public int FILTER_EXTENT = 1;

    /**
     * Stores how to filter when zooming.
     *
     * @see setFilterMethod(int)
     */
    private int filterMethod = FILTER_EXTENT;

    /**
     * Stores the only instance of this class.
     */
    static private DiagramController singleton = new DiagramController();

    /**
     * Stores the diagram history.
     */
    private DiagramHistory history = new DiagramHistory();

    /**
     * Stores the number of levels we nest diagrams.
     */
    private int nestingLevel = 0;

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
        this.nestingLevel = level;
        int lastPos = history.currentDiagrams.size() - 1;
        while( lastPos < level ) {
            if( !history.futureDiagrams.isEmpty() ) {
                history.currentDiagrams.add(history.futureDiagrams.get(0));
                history.futureDiagrams.remove(0);
                lastPos++;
            }
        }
        while( lastPos > level ) {
            history.futureDiagrams.add(0, history.currentDiagrams.get(lastPos));
            history.currentDiagrams.remove(lastPos);
            lastPos--;
        }
        history.fireContentsChanged(0,history.getSize()-1);
        notifyObservers();
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
     */
    public boolean undoIsPossible() {
        return history.pastDiagrams.size() != 0;
    }

    /**
     * Sets if we filter on the extent or object contingent of concepts zoomed
     * into.
     *
     * If this is set to FILTER_EXTENT all objects in the extent of a zoomed
     * concept will be used in the next, if set to FILTER_CONTINGENT only the
     * objects in the object contingent will be used.
     *
     * This can be changed after the zooming operation and will affect all
     * diagrams used for zooming.
     */
    public void setFilterMethod(int method) {
        this.filterMethod = method;
        notifyObservers();
    }

    /**
     * Adds a diagram to the history.
     *
     * If no diagram is open yet it will be used as current diagram, else it
     * will be added to the list of future diagrams.
     */
    public void addDiagram(Diagram2D diagram){
        if(history.currentDiagrams.size() <= this.nestingLevel) {
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
     *
     * @see back()
     */
    public void next(Concept zoomedConcept)
           throws NoSuchElementException
    {
        if(history.futureDiagrams.isEmpty()) {
            if(history.currentDiagrams.size() == 1) {
                // nothing to go to
                throw new NoSuchElementException("No diagram left");
            }
            DiagramReference oldRef = (DiagramReference) history.currentDiagrams.get(0);
            history.pastDiagrams.add(new DiagramReference( oldRef.getDiagram(), zoomedConcept ) );
            history.currentDiagrams.remove(0);
        }
        else {
            // move one of the future diagrams in
            DiagramReference oldRef = (DiagramReference) history.currentDiagrams.get(0);
            history.pastDiagrams.add(new DiagramReference( oldRef.getDiagram(), zoomedConcept ) );
            history.currentDiagrams.remove(0);
            history.currentDiagrams.add(history.futureDiagrams.get(0));
            history.futureDiagrams.remove(0);
        }
        history.fireContentsChanged(0,history.getSize()-1);
        notifyObservers();
    }

    /**
     * Goes one step back in the history.
     *
     * This is the undo operation for next().
     *
     * @see next()
     */
    public void back() {
        if(history.pastDiagrams.isEmpty()) {
            throw new NoSuchElementException("No visited diagram left");
        }
        int lastPos = history.currentDiagrams.size() - 1;
        if( lastPos == this.nestingLevel ) { // we have our nesting level
            history.futureDiagrams.add(0,history.currentDiagrams.get(
                                       history.currentDiagrams.size()-1));
            history.currentDiagrams.remove(history.currentDiagrams.size()-1);
        }
        history.currentDiagrams.add(0,history.pastDiagrams.get(
                                    history.pastDiagrams.size()-1));
        history.pastDiagrams.remove(history.pastDiagrams.size()-1);
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
        // this code is pretty tricky -- if concepts are filtered they loose
        // their ideal/filter, this has to be either avoided or fixed. If we
        // wouldn't check for (filter == null) and just call filterByXX(null)
        // instead we would loose the ideal/filter information which results
        // in wrong output (always filtering by contingent, never by extent)
        if(history.currentDiagrams.size() == 0) {
            // we don't have a diagram to display
            return null;
        }
        return getNestedDiagram(history.currentDiagrams.size()-1, 1);
    }

    /**
     * Returns a simple (flat) diagram created from the position in the list
     * of current diagrams.
     */
    protected Diagram2D getSimpleDiagram(int pos) {
        DiagramReference ref = (DiagramReference) history.currentDiagrams.get(pos);
        Diagram2D diag = ref.getDiagram();
        SimpleLineDiagram retVal = new SimpleLineDiagram();
        Concept filter = null;
        Iterator it = history.pastDiagrams.iterator();
        while(it.hasNext()) {
            DiagramReference curRef = (DiagramReference) it.next();
            Concept curZC = curRef.getZoomedConcept();
            if(filter == null) {
                filter = curZC;
            }
            else if(this.filterMethod == FILTER_CONTINGENT) {
                filter = curZC.filterByContingent(filter);
            }
            else {
                filter = curZC.filterByExtent(filter);
            }
        }
        Hashtable nodeMap = new Hashtable();

        retVal.setTitle(diag.getTitle());
        for(int i = 0; i<diag.getNumberOfNodes(); i++) {
            DiagramNode oldNode = diag.getNode(i);
            DiagramNode newNode;
            if(filter == null) {
                newNode = new DiagramNode( oldNode.getPosition(),
                                           oldNode.getConcept(),
                                           oldNode.getAttributeLabelInfo(),
                                           oldNode.getObjectLabelInfo() );
            }
            else if(this.filterMethod == FILTER_CONTINGENT) {
                newNode = new DiagramNode( oldNode.getPosition(),
                                           oldNode.getConcept().filterByContingent(filter),
                                           oldNode.getAttributeLabelInfo(),
                                           oldNode.getObjectLabelInfo() );
            }
            else {
                newNode = new DiagramNode( oldNode.getPosition(),
                                           oldNode.getConcept().filterByExtent(filter),
                                           oldNode.getAttributeLabelInfo(),
                                           oldNode.getObjectLabelInfo() );
            }
            retVal.addNode(newNode);
            nodeMap.put(oldNode,newNode);
        }
        for(int i = 0; i < diag.getNumberOfLines(); i++) {
            DiagramLine line = diag.getLine(i);
            DiagramNode from = (DiagramNode) nodeMap.get(line.getFromNode());
            DiagramNode to = (DiagramNode) nodeMap.get(line.getToNode());
            retVal.addLine( from, to );

            // add direct neighbours to concepts
            AbstractConceptImplementation concept1 =
                             (AbstractConceptImplementation) from.getConcept();
            AbstractConceptImplementation concept2 =
                             (AbstractConceptImplementation) to.getConcept();
            concept1.addSubConcept(concept2);
            concept2.addSuperConcept(concept1);
        }

        // build transitive closures for each concept
        for(int i = 0; i < retVal.getNumberOfNodes(); i++) {
            ((AbstractConceptImplementation) retVal.getNode(i).getConcept()).buildClosures();
        }

        return retVal;
    }

    /**
     * Returns a nested diagram using the list of current diagrams.
     *
     * The parameter pos determines the position in the list of current diagrams.
     */
    protected Diagram2D getNestedDiagram(int pos, int level) {
        if(pos == 0) {
            // we have only a flat diagram left
            return getSimpleDiagram(0);
        }
        // else created nested diagram recursively
        DiagramReference ref = (DiagramReference)history.currentDiagrams.get(pos);
        return new NestedLineDiagram(getNestedDiagram(pos-1, level+1), ref.getDiagram(), level);
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