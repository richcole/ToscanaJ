/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.diagram.*;
import net.sourceforge.toscanaj.model.lattice.AbstractConceptImplementation;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.observer.ChangeObservable;
import net.sourceforge.toscanaj.observer.ChangeObserver;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;

import java.util.Hashtable;
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
     * Constant for setFilterMethod(int).
     *
     * @see #setFilterMethod(int)
     */
    static public int FILTER_CONTINGENT = 0;

    /**
     * Constant for setFilterMethod(int).
     *
     * @see #setFilterMethod(int)
     */
    static public int FILTER_EXTENT = 1;

    /**
     * Stores how to filter when zooming.
     *
     * @see #setFilterMethod(int)
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
        // this code is pretty tricky -- if concepts are filtered they loose
        // their ideal/filter, this has to be either avoided or fixed. If we
        // wouldn't check for (filter == null) and just call filterByXX(null)
        // instead we would loose the ideal/filter information which results
        // in wrong output (always filtering by contingent, never by extent)
        if (history.getNumberOfCurrentDiagrams() == 0) {
            // we don't have a diagram to display
            return null;
        }
        /** @todo : Calculating the objects in the diagram is currently a side
         effect of the diagram calculation --> fix by putting the
         calculation into Diagram2D. */
        this.numberOfCurrentObjects = 0;
        this.maxContingentSize = 0;
        Diagram2D retVal = getNestedDiagram(history.getNumberOfCurrentDiagrams() - 1);
        if (this.numberOfObjects == -1) {
            this.numberOfObjects = this.numberOfCurrentObjects;
        }
        return retVal;
    }

    /**
     * Returns a simple (flat) diagram created from the position in the list
     * of current diagrams.
     */
    protected Diagram2D getSimpleDiagram(int pos) {
        Diagram2D diag = history.getCurrentDiagram(pos);

        SimpleLineDiagram retVal = new SimpleLineDiagram();
        Concept filter = calculateFilterFromPastDiagrams();
        Hashtable nodeMap = new Hashtable();

        retVal.setTitle(diag.getTitle());
        retVal.setDescription(diag.getDescription());
        for (int i = 0; i < diag.getNumberOfNodes(); i++) {
            DiagramNode oldNode = diag.getNode(i);
            DiagramNode newNode = makeDiagramNode(oldNode, filter);
            retVal.addNode(newNode);
            nodeMap.put(oldNode, newNode);

            int contSize = newNode.getConcept().getObjectContingentSize();
            this.numberOfCurrentObjects = this.numberOfCurrentObjects + contSize;
            if (contSize > this.maxContingentSize) {
                this.maxContingentSize = contSize;
            }
        }
        for (int i = 0; i < diag.getNumberOfLines(); i++) {
            DiagramLine line = diag.getLine(i);
            DiagramNode from = (DiagramNode) nodeMap.get(line.getFromNode());
            DiagramNode to = (DiagramNode) nodeMap.get(line.getToNode());
            retVal.addLine(from, to);

            // add direct neighbours to concepts
            AbstractConceptImplementation concept1 =
                    (AbstractConceptImplementation) from.getConcept();
            AbstractConceptImplementation concept2 =
                    (AbstractConceptImplementation) to.getConcept();
            concept1.addSubConcept(concept2);
            concept2.addSuperConcept(concept1);
        }

        // build transitive closures for each concept
        for (int i = 0; i < retVal.getNumberOfNodes(); i++) {
            ((AbstractConceptImplementation) retVal.getNode(i).getConcept()).buildClosures();
        }

        return retVal;
    }


    /**
     * Creates a new node by filtering the old one.
     *
     * The way of filtering is defined by the filterMethod member, set with setFilterMethod(int)
     */

    private DiagramNode makeDiagramNode(DiagramNode oldNode, Concept filter) {
        Concept concept = null;
        if (filter == null) {
            concept = oldNode.getConcept();
        } else if (this.filterMethod == FILTER_CONTINGENT) {
            concept = oldNode.getConcept().filterByContingent(filter);
        } else if (this.filterMethod == FILTER_EXTENT) {
            concept = oldNode.getConcept().filterByExtent(filter);
        } else {
            throwUnknownFilterError();
        }

        return new DiagramNode("filtered:" + oldNode.getIdentifier(),
                oldNode.getPosition(),
                concept,
                oldNode.getAttributeLabelInfo(),
                oldNode.getObjectLabelInfo(),
                oldNode.getOuterNode());
    }

    private static void throwUnknownFilterError() {
        throw new RuntimeException("Unknown filter method");
    }

    /**
     * Calculates the filter from all past diagrams.
     */

    static class FilterCalculatorVisitor implements DiagramHistory.ConceptVisitor {
        Concept filter = null;
        int filterMethod;

        FilterCalculatorVisitor(int filterMethod) {
            this.filterMethod = filterMethod;
        }

        public void visitConcept(Concept concept) {
            if (filter == null) {
                filter = concept;
            } else if (filterMethod == DiagramController.FILTER_CONTINGENT) {
                filter = concept.filterByContingent(filter);
            } else if (filterMethod == DiagramController.FILTER_EXTENT) {
                filter = concept.getCollapsedConcept().filterByExtent(filter);
            } else {
                throwUnknownFilterError();
            }
        }

        public Concept getFilter() {
            return filter;
        }
    }

    private Concept calculateFilterFromPastDiagrams() {
        FilterCalculatorVisitor filterCalculator = new FilterCalculatorVisitor(this.filterMethod);
        history.visitZoomedConcepts(filterCalculator);
        return filterCalculator.getFilter();
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

    public ConceptInterpreter getDefaultInterpreter(DatabaseConnection databaseConnection) {
        return new DatabaseConnectedConceptInterpreter( databaseConnection );
    }
}