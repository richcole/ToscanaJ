/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.lattice.Concept;

import java.util.Iterator;


/*
 * & - intersection
 * | - union
 * &x:X - intersection with x ranging over X
 *
 * i - inner concept
 * z - zoomed concept
 * n - nested concept
 *
 * interp(i,Z,N) = interp(i,displayMode) &
 *    &n:N interp(n,CONTINGENT) &z:Z interp(z,filterMode)
 *
 * The above equation gives a formular for the interpretation
 * of a concept, i which exists in a nesting N and a zooming Z.
 * N and Z are considered sequences.
 *
 * The above equation is manifest in the program by constructing
 * new concept interpreters using the functions:
 *
 *     createNestedInterpreter
 *     createFilteredInterperter
 * 
 * @todo it might be better not to have the objectDisplayMode but two methods for extent/contingent instead. This allows more caching
 *       since the context would be the same more often.
 */
// @todo it would be better if the concept interpreter can create the concepts, e.g. the tuple version could store the tuples.
/**
 * Maps concepts of a concrete scale to those of the matching realized scale.
 * 
 * Instead of using concepts directly, a concept interpreter can be used to replace them with
 * updated information. A typical use-case is the classic Toscana-system: the objects in the
 * predefined diagrams are SQL queries, the actual objects of interest are the ones found in the
 * database. The concept interpreter's role is to map one to the other.
 * 
 * Another role of the concept interpreter is to consider the context in which a concept is used.
 * The concept stored is usually the one of a full data set, filtering and nesting can reduce the
 * available data, thus changing the concept, potentially even turning it into one that wouldn't
 * normally be there since it has the same extent/intent as others (a so-called "unrealized concept").
 * The concept interpreter does this mapping, too. 
 * 
 * @todo check if it would be better to separate the two tasks of contextualization and realization.
 * 
 * @param <Oc> The type of the objects in the concrete scale.
 * @param <A> The type of the attributes used in both scales (always the same).
 * @param <Or> The type of the objects in the realized scale.
 */
public interface ConceptInterpreter<Oc,A,Or> {
	public static final String CONCEPT_INTERPRETER_ELEMENT_NAME = "conceptInterpreter";
	public static final String CONCEPT_INTERPRETER_CLASS_ATTRIBUTE = "class";

    public static class IntervalType {
    	// nothing to declare
    }
    public static final IntervalType INTERVAL_TYPE_FIXED = new IntervalType();
    public static final IntervalType INTERVAL_TYPE_CONTINGENT = new IntervalType();
    public static final IntervalType INTERVAL_TYPE_EXTENT = new IntervalType();
    public static final IntervalType INTERVAL_TYPE_ORTHOGONALTIY = new IntervalType();

    /** is dependent on displayMode and filterMode */
    Iterator<Or> getObjectSetIterator(Concept<Oc,A> concept, ConceptInterpretationContext<Oc,A> context);

    /** independent of displayMode and filterMode */
    Iterator<A> getAttributeContingentIterator(Concept<Oc,A> concept, ConceptInterpretationContext<Oc,A> context);

    /** independent of displayMode and filterMode */
    Iterator<A> getIntentIterator(Concept<Oc,A> concept, ConceptInterpretationContext<Oc,A> context);

    /** is dependent on displayMode and filterMode */
    int getObjectCount(Concept<Oc,A> concept, ConceptInterpretationContext<Oc,A> context);

    /** is dependent on displayMode and filterMode */
    int getAttributeCount(Concept<Oc,A> concept, ConceptInterpretationContext<Oc,A> context);

	/** these are independent of displayMode and dependent on filterMode */
	int getObjectContingentSize(Concept<Oc,A> concept, ConceptInterpretationContext<Oc,A> context);

	/** these are independent of displayMode and dependent on filterMode */
	int getExtentSize(Concept<Oc,A> concept, ConceptInterpretationContext<Oc,A> context);

	NormedIntervalSource<Oc,A> getIntervalSource(IntervalType type);

    boolean isRealized(Concept<Oc,A> concept, ConceptInterpretationContext<Oc,A> context);

    /**
     * @todo This is all a bit messy :-(
     * 
     * @param query The query to execute.
     * @param concept The concept to execute the query upon.
     * @param context The context in which the query should be executed.
     * @return The query result, which can be the realized concepts (same as getObjectSetIterator(..)) or
     *         aggregates or other mappings.
     */
    FCAElement[] executeQuery(Query query, Concept<Oc,A> concept, ConceptInterpretationContext<Oc,A> context);
    
    void showDeviation(boolean show);
    
    boolean isVisible(Concept<Oc,A> concept, ConceptInterpretationContext<Oc,A> context);
}
