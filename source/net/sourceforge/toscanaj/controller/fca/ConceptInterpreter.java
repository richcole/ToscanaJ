/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

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
 * The above equation is manifest in the program by constucting
 * new concept interpreters using the functions:
 *
 *     createNestedInterpreter
 *     createFilteredInterperter
 * 
 * @todo it might be better not to have the objectDisplayMode but two methods for extent/contingent instead. This allows more caching
 *       since the context would be the same more often.
 */
// @todo it would be better if the concept interpreter can create the concepts, e.g. the tuple version could store the tuples.
public interface ConceptInterpreter {
	public static final String CONCEPT_INTERPRETER_ELEMENT_NAME = "conceptInterpreter";
	public static final String CONCEPT_INTERPRETER_CLASS_ATTRIBUTE = "class";

    public static class IntervalType {}
    public static final IntervalType INTERVAL_TYPE_FIXED = new IntervalType();
    public static final IntervalType INTERVAL_TYPE_CONTINGENT = new IntervalType();
    public static final IntervalType INTERVAL_TYPE_EXTENT = new IntervalType();
    public static final IntervalType INTERVAL_TYPE_ORTHOGONALTIY = new IntervalType();

    /** is dependent on displayMode and filterMode */
    Iterator getObjectSetIterator(Concept concept, ConceptInterpretationContext context);

    /** is dependent on displayMode and filterMode */
    Iterator getAttributeSetIterator(Concept concept, ConceptInterpretationContext context);

    /** is dependent on displayMode and filterMode */
    int getObjectCount(Concept concept, ConceptInterpretationContext context);

    /** is dependent on displayMode and filterMode */
    int getAttributeCount(Concept concept, ConceptInterpretationContext context);

	/** these are independent of displayMode and dependent on filterMode */
	int getObjectContingentSize(Concept concept, ConceptInterpretationContext context);

	/** these are independent of displayMode and dependent on filterMode */
	int getExtentSize(Concept concept, ConceptInterpretationContext context);

	NormedIntervalSource getIntervalSource(IntervalType type);

    boolean isRealized(Concept concept, ConceptInterpretationContext context);

    Object[] executeQuery(Query query, Concept concept, ConceptInterpretationContext context);
    
    void showDeviation(boolean show);
    
    boolean isVisible(Concept concept, ConceptInterpretationContext context);
}
