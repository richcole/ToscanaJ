/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.toscanaj.model.database.AggregateQuery;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.lattice.Concept;

public abstract class AbstractConceptInterperter implements ConceptInterpreter {

	public abstract  Iterator getObjectSetIterator(Concept concept,ConceptInterpretationContext context) ;

	public Iterator getAttributeSetIterator(Concept concept, ConceptInterpretationContext context) {
		return concept.getAttributeContingentIterator();
	}
	
	public int getObjectCount(Concept concept, ConceptInterpretationContext context) {
		if (context.getObjectDisplayMode() == ConceptInterpretationContext.CONTINGENT) {
			return getObjectContingentSize(concept, context);
		} else {
			return getExtentSize(concept,context);
		}
	}

	public int getAttributeCount(Concept concept, ConceptInterpretationContext context) {
		return concept.getAttributeContingentSize();
	}

	public abstract int getObjectContingentSize(Concept concept,ConceptInterpretationContext context) ;

	public abstract int getExtentSize(Concept concept,ConceptInterpretationContext context);

	/**
	 * Gives the relation of the contingent size of the concept given and the largest contingent size queried in the
	 * given context up to now.
	 *
	 * Note that it is not ensured that all contingent sizes have been queried before this call, this is up to the
	 * caller.
	 */
	public NormedIntervalSource getIntervalSource(IntervalType type) {
		if(type == INTERVAL_TYPE_CONTINGENT) {
			return new NormedIntervalSource(){
				public double getValue(Concept concept, ConceptInterpretationContext context) {
					int contingentSize = getObjectContingentSize(concept, context);
					if (contingentSize == 0) {
						return 0; //avoids division by zero
					}
					return (double) contingentSize / (double) getMaximalContingentSize();
				}
			};
		} else if(type == INTERVAL_TYPE_EXTENT) {
			return new NormedIntervalSource(){
				public double getValue(Concept concept, ConceptInterpretationContext context) {
					int extentSize = getExtentSize(concept, context);
					if (extentSize == 0) {
						return 0; //avoids division by zero
					}
					/// @todo add way to find top compareConcept more easily
					Concept compareConcept;
					ConceptInterpretationContext compareContext;
					List nesting = context.getNestingConcepts();
					if (nesting.size() != 0) {
						// go outermost
						compareConcept = (Concept) nesting.get(0);
						compareContext = (ConceptInterpretationContext) context.getNestingContexts().get(0);
					} else {
						compareConcept = concept;
						compareContext = context;
					}
					return (double) extentSize /
							(double) getExtentSize(compareConcept.getTopConcept(), compareContext);
				}
			};
		} else if(type == INTERVAL_TYPE_FIXED) {
			return new FixedValueIntervalSource(1);
		} else {
			throw new IllegalArgumentException("Unknown interval type");
		}
	}
	

	public abstract boolean isRealized(Concept concept,ConceptInterpretationContext context) ;

	public Object[] executeQuery(Query query, Concept concept, ConceptInterpretationContext context) {
		if(!isRealized(concept, context)) {
			return null;
		}
		Object[] retVal;
		if (query == ListQuery.KEY_LIST_QUERY) {
			int objectCount = getObjectCount(concept, context);
			if( objectCount != 0) {
				retVal = new Object[objectCount];
				Iterator it = getObjectSetIterator(concept, context);
				int pos = 0;
				while (it.hasNext()) {
					Object o = it.next();
					retVal[pos] = o;
					pos++;
				}
			} else {
				return null;
			}
		} else if (query == AggregateQuery.COUNT_QUERY) {
			int objectCount = getObjectCount(concept, context);
			retVal = new Object[1];
			if( objectCount != 0) {
				retVal[0] = getObject(Integer.toString(objectCount), concept, context);
			} else {
				return null;
			}
		} else if (query == AggregateQuery.PERCENT_QUERY) {
			int objectCount = getObjectCount(concept, context);
			retVal = new Object[1];
			if( objectCount != 0) {
				boolean oldMode = context.getObjectDisplayMode();
				context.setObjectDisplayMode(ConceptInterpretationContext.EXTENT);
				int fullExtent = getObjectCount(concept.getTopConcept(), context);
				context.setObjectDisplayMode(oldMode);
				NumberFormat format = DecimalFormat.getNumberInstance();
				format.setMaximumFractionDigits(2);
				String objectValue = format.format(100 * objectCount/(double)fullExtent) + " %";
				retVal[0] = getObject(objectValue, concept, context);				
			} else {
				return null;
			}
		} else {
			return handleNonDefaultQuery(query, concept, context);
		}
		return retVal;
	}

	protected abstract Object  getObject (String value, Concept concept, ConceptInterpretationContext context);
	
	protected abstract Object[] handleNonDefaultQuery(Query query, Concept concept, ConceptInterpretationContext context); 
	
	// @todo implement this here maybe?..
	protected abstract int getMaximalContingentSize() ;

}
