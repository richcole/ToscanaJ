package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.diagram.NestedLineDiagram;
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
     * Stores the number of objects in the current diagram.
     */
    private int numberOfCurrentObjects = 0;

    /**
     * Stores the maximal contingent size in the current diagram.
     */
    private int maxContingentSize = 0;

    /**
     * Stores the number of objects in the schema.
     *
     * This will currently be calculated by using the number of objects in the
     * first diagram displayed.
     */
    private int numberOfObjects = -1;

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
            if( history.futureDiagrams.isEmpty() ) {
                break; // nothing more to get
            }
            history.currentDiagrams.add(history.futureDiagrams.get(0));
            history.futureDiagrams.remove(0);
            lastPos++;
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
     *
     * This is only true if we have past diagrams, if we have nesting we can
     * call back() but the user should not be allowed to, since this changes
     * nesting levels but is not an undo.
     *
     * @see back()
     */
    public boolean undoIsPossible() {
        return !history.pastDiagrams.isEmpty();
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
            notifyObservers();
        }
        else {
            history.futureDiagrams.add(new DiagramReference(diagram,null));
        }
        int lastPos = history.pastDiagrams.size() +
                      history.currentDiagrams.size() +
                      history.futureDiagrams.size() - 1;
        history.fireIntervalAdded(lastPos,lastPos);
    }

    /**
     * Removes a specific diagram from the model.
     *
     * Throws NoSuchElementException if there is no such diagram. If it is tried
     * to remove a currently displayed diagram nothing will happen.
     *
     * @TODO Find something useful when removing a current diagram.
     * @TODO Do some testing on this, it might be broken.
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
            return;
        }
        throw new NoSuchElementException("Tried to remove diagram beyond range");
    }

    /**
     * Removes the last diagram from the list.
     *
     * If this is a currently visible diagram this will include an undo operation.
     *
     * If no diagram is left a NoSuchElementException will be raised.
     */
    public void removeLastDiagram() {
        if( !history.futureDiagrams.isEmpty() ) {
            history.futureDiagrams.remove(history.futureDiagrams.size()-1);
            history.fireIntervalRemoved(history.getSize(),history.getSize());
            return;
        }
        // no future diagrams, check if we can undo
        if( history.pastDiagrams.size() + history.currentDiagrams.size() > 1 ) {
            back();
            history.futureDiagrams.remove(0);
            history.fireIntervalRemoved(history.getSize(),history.getSize());
            return;
        }
        // no future diagrams, no undo -- do we have at least one diagram?
        if( !history.currentDiagrams.isEmpty() ) {
            history.currentDiagrams.clear();
            history.fireIntervalRemoved(history.getSize(),history.getSize());
            notifyObservers();
            return;
        }
        throw new NoSuchElementException("The list of diagrams is already empty.");
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
     * This is the undo operation for next(). If we have multiple current diagrams
     * but no past diagrams left this will reduce the nesting level by one until
     * new diagrams are added.
     *
     * @see next()
     * @see undoIsPossible()
     */
    public void back() {
        if(history.pastDiagrams.size() + history.currentDiagrams.size() < 2) {
            throw new NoSuchElementException("No diagram left to go back to.");
        }
        int lastPos = history.currentDiagrams.size() - 1;
        if( lastPos == this.nestingLevel ) { // we have our nesting level
            history.futureDiagrams.add(0,history.currentDiagrams.get(lastPos));
            history.currentDiagrams.remove(lastPos);
        }
        if( !history.pastDiagrams.isEmpty() ) {
            // we have something to go back to, otherwise we just lose nesting
            history.currentDiagrams.add(0,history.pastDiagrams.get(
                                        history.pastDiagrams.size()-1));
            history.pastDiagrams.remove(history.pastDiagrams.size()-1);
        }
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
        /** @TODO: Calculating the objects in the diagram is currently a side
                   effect of the diagram calculation --> fix by putting the
                   calculation into Diagram2D. */
        this.numberOfCurrentObjects = 0;
        this.maxContingentSize = 0;
        Diagram2D retVal = getNestedDiagram(history.currentDiagrams.size()-1);
        if(this.numberOfObjects == -1) {
            this.numberOfObjects = this.numberOfCurrentObjects;
        }
        return retVal;
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
                filter = curZC.getCollapsedConcept().filterByExtent(filter);
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
            int contSize = newNode.getConcept().getObjectContingentSize();
            this.numberOfCurrentObjects = this.numberOfCurrentObjects + contSize;
            if(contSize > this.maxContingentSize) {
                this.maxContingentSize = contSize;
            }
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
    protected Diagram2D getNestedDiagram(int pos) {
        if(pos == 0) {
            // we have only a flat diagram left
            return getSimpleDiagram(0);
        }
        // else created nested diagram recursively
        DiagramReference ref = (DiagramReference)history.currentDiagrams.get(pos);
        return new NestedLineDiagram(getNestedDiagram(pos-1), ref.getDiagram() );
    }

    /**
     * Returns the number of objects currently displayed.
     */
    public int getNumberOfCurrentObjects() {
        return this.numberOfCurrentObjects;
    }

    /**
     * Returns the maximal number of objects in any contingent in the current
     * diagram.
     */
    public int getMaximalObjectContingentSize() {
        return this.maxContingentSize;
    }

    /**
     * Returns the number of objects in the whole schema.
     */
    public int getNumberOfObjects() {
        return this.numberOfObjects;
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
     *
     * This should not be called if the changes affect only future diagrams.
     */
    protected void notifyObservers() {
        Iterator it = this.observers.iterator();
        while(it.hasNext()) {
            ChangeObserver observer = (ChangeObserver)it.next();
            observer.update(this);
        }
    }
}