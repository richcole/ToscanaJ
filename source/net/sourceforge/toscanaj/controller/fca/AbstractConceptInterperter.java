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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

import net.sourceforge.toscanaj.controller.fca.events.ConceptInterpretationContextChangedEvent;
import net.sourceforge.toscanaj.model.database.AggregateQuery;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.lattice.Concept;

public abstract class AbstractConceptInterperter implements ConceptInterpreter, EventBrokerListener {
	private Hashtable contingentSizes = new Hashtable();
	private Hashtable extentSizes = new Hashtable();
	
	private class DeviationValuesRef {
		private int neutralSize;
		private int outerSize;
		private double expectedSize;
		private DeviationValuesRef (int neutrialSize, int outerSize, double expectedSize) {
			this.neutralSize = neutrialSize;
			this.outerSize = outerSize;
			this.expectedSize = expectedSize;
		}
		public double getExpectedSize() {
			return expectedSize;
		}
		public int getNeutralSize() {
			return neutralSize;
		}
		public int getOuterSize() {
			return outerSize;
		}
	}

	public abstract Iterator getObjectSetIterator(Concept concept,ConceptInterpretationContext context) ;

	protected abstract Object getObject(String value, Concept concept, ConceptInterpretationContext context);
	
	protected abstract Object[] handleNonDefaultQuery(Query query, Concept concept, ConceptInterpretationContext context); 
	
	protected abstract int calculateContingentSize (Concept concept, ConceptInterpretationContext context);

	protected int calculateExtentSize(Concept concept, ConceptInterpretationContext context) {
		List outerConcepts = context.getNestingConcepts();
		if(outerConcepts.size() > 1) {
			throw new RuntimeException("multiple levels of nesting not yet supported");
		}
		if(outerConcepts.size() == 1) {
			int retVal = 0;
			Concept outerConcept = (Concept) outerConcepts.get(0);
			ConceptInterpretationContext parentContext = (ConceptInterpretationContext) context.getNestingContexts().get(0);
			for(Iterator it = outerConcept.getDownset().iterator(); it.hasNext(); ) {
				Concept currentOuterConcept = (Concept) it.next();
				ConceptInterpretationContext currentContext = parentContext.createNestedContext(currentOuterConcept);
				retVal += getLocalObjectContingentSize(concept, currentContext);
			}
			return retVal;
		} else {
			return getLocalObjectContingentSize(concept, context);
		}
	}

	private int getLocalObjectContingentSize(Concept concept, ConceptInterpretationContext context) {
		int retVal = 0;
		Iterator it = concept.getDownset().iterator();
		while (it.hasNext()) {
			Concept subconcept = (Concept) it.next();
			retVal += getObjectContingentSize(subconcept, context);
		}
		return retVal;
	}

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

	public int getObjectContingentSize(Concept concept, ConceptInterpretationContext context) {
			Hashtable sizes = getContingentSizesCache(context);
			Integer cacheVal = (Integer) sizes.get(concept);
			if (cacheVal != null) {
				return cacheVal.intValue();
			}
			int count = calculateContingentSize(concept, context);
			sizes.put(concept, new Integer(count));
			return count;
	}
	
	public int getExtentSize(Concept concept, ConceptInterpretationContext context) {
		Hashtable sizes = getExtentSizesCache(context);
		Integer cacheVal = (Integer) sizes.get(concept);
		if (cacheVal != null) {
			return cacheVal.intValue();
		}
		int count = calculateExtentSize(concept, context);
		sizes.put(concept, new Integer(count));
		return count;
	}
	

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
					Concept compareConcept = context.getOutermostTopConcept(concept);
					ConceptInterpretationContext compareContext = context.getOutermostContext();
					return (double) extentSize /
							(double) getExtentSize(compareConcept.getTopConcept(), compareContext);
				}
			};
        } else if(type == INTERVAL_TYPE_FIXED) {
            return new FixedValueIntervalSource(1);
        } else if(type == INTERVAL_TYPE_CONTINGENT_ORTHOGONALTIY) {
            return new NormedIntervalSource() {
                public double getValue(Concept concept, ConceptInterpretationContext context) {
                    if(context.getNestingContexts().size() == 0) {
                        return 0.5;
                    }
                    DeviationValuesRef deviationValues = calculateExpectedSize(concept, context, false);
                    double expectedSize = deviationValues.getExpectedSize();
                    int contingentSize = getObjectContingentSize(concept, context);
                    if(contingentSize == expectedSize) {
                        return 0.5;                                        
                    }
                    if(contingentSize < expectedSize) {
                        return 0.5 * contingentSize / expectedSize;                                        
                    }
                    int max = Math.min(deviationValues.getNeutralSize(), deviationValues.getOuterSize());
                    double range = max - expectedSize;
                    return 0.5 + 0.5 * (contingentSize - expectedSize) / range;                                        
                }
            };
        } else if(type == INTERVAL_TYPE_EXTENT_ORTHOGONALTIY) {
            return new NormedIntervalSource() {
                public double getValue(Concept concept, ConceptInterpretationContext context) {
                    if(context.getNestingContexts().size() == 0) {
                        return 0.5;
                    }
					DeviationValuesRef deviationValues = calculateExpectedSize(concept, context, true);
                    double expectedSize = deviationValues.getExpectedSize();
                    int extentSize = getExtentSize(concept, context);
                    if(extentSize == expectedSize) {
                        return 0.5;                                        
                    }
                    if(extentSize < expectedSize) {
                        return 0.5 * extentSize / expectedSize;                                        
                    }
                    int max = Math.min(deviationValues.getNeutralSize(), deviationValues.getOuterSize());
                    double range = max - expectedSize;
                    return 0.5 + 0.5 * (extentSize - expectedSize)/range;                                        
                }
            };
		} else {
			throw new IllegalArgumentException("Unknown interval type");
		}
	}
	
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
			retVal = executeObjectCountQuery(concept, context);
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
		} else if (query == AggregateQuery.DEVIATION_QUERY) {
			retVal = new Object[1];
			if(context.getNestingContexts().size() == 0) {
				return executeObjectCountQuery(concept, context);
			}
			DeviationValuesRef deviationValues =
                    calculateExpectedSize(concept, context, 
                            context.getObjectDisplayMode() == ConceptInterpretationContext.EXTENT); 
			double expectedSize = deviationValues.getExpectedSize();
			int objectCount = getObjectCount(concept, context); 
			if ((objectCount == 0) && (expectedSize == 0.0)) {
				return null;
			}
			if (objectCount == expectedSize) {
				return executeObjectCountQuery(concept, context);
			}
			NumberFormat format = DecimalFormat.getNumberInstance();
			format.setMaximumFractionDigits(1);
			String returnValue = objectCount + " [exp: " + format.format(expectedSize) + "]";
			retVal[0] = getObject(returnValue, concept, context); 
		} else {
			return handleNonDefaultQuery(query, concept, context);
		}
		return retVal;
	}

	private DeviationValuesRef calculateExpectedSize(Concept concept,
									ConceptInterpretationContext context,
									boolean isExtent) {
		int neutralSize;
		int outerSize;
		Concept nestingConcept = (Concept) context.getNestingConcepts().get(0);
		if (isExtent) {
			neutralSize = getExtentSize(concept, context.getOutermostContext());
			outerSize = getExtentSize(nestingConcept, context.getOutermostContext());
		}
		else {
			neutralSize = getObjectContingentSize(concept, context.getOutermostContext());
			outerSize = getObjectContingentSize(nestingConcept, context.getOutermostContext());				
		}
		int numberOfAllObjectsInDiagram = getExtentSize(context.getOutermostTopConcept(concept), 
														context.getOutermostContext());
		double expectedSize = neutralSize * outerSize / (double) numberOfAllObjectsInDiagram;
		return new DeviationValuesRef(neutralSize, outerSize, expectedSize);
	}
	
	private Object[] executeObjectCountQuery (Concept concept, ConceptInterpretationContext context) {
		int objectCount = getObjectCount(concept, context);
		if( objectCount != 0) {
			Object[] retVal = new Object[1];
			retVal[0] = getObject(Integer.toString(objectCount), concept, context);
			return retVal;
		} else {
			return null;
		}
		
	}

	/**
	 * This returns the maximal contingent found up to now.
	 */
	protected int getMaximalContingentSize() {
		int maxVal = 0;
		for (Iterator iterator = this.contingentSizes.values().iterator(); iterator.hasNext();) {
			Hashtable contSizes = (Hashtable) iterator.next();
			for (Iterator iterator2 = contSizes.values().iterator(); iterator2.hasNext();) {
				Integer curVal = (Integer) iterator2.next();
				if (curVal.intValue() > maxVal) {
					maxVal = curVal.intValue();
				}
			}
		}
		return maxVal;
	}

	public boolean isRealized(Concept concept, ConceptInterpretationContext context) {
		/// @todo do check only lower neighbours
		/// @todo consider going back to creating the lattice product, this is too much hacking

		// first we check the inner diagram if anything below the concept has the same extent
		// size (iff any subconcept has the same extent size, the concept is not realized) 
		int extentSize = getExtentSize(concept, context);
		for (Iterator iterator = concept.getDownset().iterator(); iterator.hasNext();) {
			Concept otherConcept = (Concept) iterator.next();
			if (otherConcept == concept) {
				continue;
			}
			int otherExtentSize = getExtentSize(otherConcept, context);
			if (otherExtentSize == extentSize) {
				return false;
			}
		}
		// the way it works for the outer diagram is a bit wild -- we assume either all contingent or all
		// extents have already been calculated (which one depends on the view setting), so the cache for
		// either the contingent or the extent sizes should contain all interpretation contexts for the
		// lower nodes of the outer diagrams. Now we look for all whose nesting concepts are subconcepts
		// of the nesting concepts we are in, then check for the extent of the current concept in these
		// contexts -- they are all subconcepts of our concept along the outer diagram.
		List outerConcepts = context.getNestingConcepts();
		for (Iterator iterator = outerConcepts.iterator(); iterator.hasNext();) {
			Concept outerConcept = (Concept) iterator.next();
			for (Iterator iterator2 = outerConcept.getDownset().iterator(); iterator2.hasNext();) {
				Concept otherConcept = (Concept) iterator2.next();
				if (otherConcept != outerConcept) {
					for (Iterator iterator3 = this.contingentSizes.keySet().iterator(); iterator3.hasNext();) {
						ConceptInterpretationContext otherContext = (ConceptInterpretationContext) iterator3.next();
						List nesting = otherContext.getNestingConcepts();
						if (nesting.size() != 0) {
							if (nesting.get(nesting.size() - 1).equals(otherConcept)) {
								int otherExtentSize = getExtentSize(concept, otherContext);
								if (otherExtentSize == extentSize) {
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}


	private Hashtable getContingentSizesCache(ConceptInterpretationContext context) {
		Hashtable retVal = (Hashtable) contingentSizes.get(context);
		if (retVal == null) {
			retVal = new Hashtable();
			contingentSizes.put(context, retVal);
			/// @todo can we get around this by being smarter about the hashcodes ???
			context.getEventBroker().subscribe(this,
					ConceptInterpretationContextChangedEvent.class,
					ConceptInterpretationContext.class);
		}
		return retVal;
	}
	
	public void processEvent(Event e) {
		clearCaches((ConceptInterpretationContext) e.getSubject());
	}

	private void clearCaches(ConceptInterpretationContext context) {
		this.contingentSizes.remove(context);
		this.extentSizes.remove(context);
	}

	private Hashtable getExtentSizesCache(ConceptInterpretationContext context) {
		Hashtable retVal = (Hashtable) this.extentSizes.get(context);
		if (retVal == null) {
			retVal = new Hashtable();
			extentSizes.put(context, retVal);
			/// @todo can we get around this by being smarter about the hashcodes ???
			context.getEventBroker().subscribe(this,
					ConceptInterpretationContextChangedEvent.class,
					ConceptInterpretationContext.class);
		}
		return retVal;
	}

	public void clearCache() {
		this.contingentSizes.clear();
		this.extentSizes.clear();
	}
	

}
