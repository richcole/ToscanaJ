/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.model.database.AggregateQuery;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.lattice.Concept;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

///@todo this class does not allow nesting and filtering at the moment (or does it?)

public class DirectConceptInterpreter implements ConceptInterpreter {
    private Hashtable contingents = new Hashtable();
    private Hashtable extents = new Hashtable();

    public Iterator getObjectSetIterator(Concept concept, ConceptInterpretationContext context) {
        if (context.getObjectDisplayMode() == ConceptInterpretationContext.CONTINGENT) {
            return getContingent(concept, context).iterator();
        } else {
            return getExtent(concept, context).iterator();
        }
    }

    public Iterator getAttributeSetIterator(Concept concept, ConceptInterpretationContext context) {
        return concept.getAttributeContingentIterator();
    }

    public int getObjectCount(Concept concept, ConceptInterpretationContext context) {
        return getCount(concept, context, context.getObjectDisplayMode());
    }

    public int getAttributeCount(Concept concept, ConceptInterpretationContext context) {
        return concept.getAttributeContingentSize();
    }

    private int getMaximalContingentSize() {
        /// @todo implement
        return 1;
    }

    public NormedIntervalSource getIntervalSource(IntervalType type) {
        if(type == INTERVAL_TYPE_CONTINGENT) {
            return new NormedIntervalSource(){
                public double getValue(Concept concept, ConceptInterpretationContext context) {
                    int contingentSize = getCount(concept, context, ConceptInterpretationContext.CONTINGENT);
                    if (contingentSize == 0) {
                        return 0; //avoids division by zero
                    }
                    return (double) contingentSize / (double) getMaximalContingentSize();
                }
            };
        } else if(type == INTERVAL_TYPE_EXTENT) {
            return new NormedIntervalSource(){
                public double getValue(Concept concept, ConceptInterpretationContext context) {
                    int extentSize = getCount(concept, context, ConceptInterpretationContext.EXTENT);
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
                        compareContext = new ConceptInterpretationContext(context.getDiagramHistory(), context.getEventBroker());
                    } else {
                        compareConcept = concept;
                        compareContext = context;
                    }
                    while (!compareConcept.isTop()) {
                        Concept other;
                        Iterator it = compareConcept.getUpset().iterator();
                        do {
                            other = (Concept) it.next();
                        } while (other == compareConcept);
                        compareConcept = other;
                    }
                    int maxExtent = getCount(compareConcept, compareContext, ConceptInterpretationContext.EXTENT);
                    return (double) extentSize / (double) maxExtent;
                }
            };
        } else if(type == INTERVAL_TYPE_FIXED) {
            return new FixedValueIntervalSource(1);
        } else {
            throw new IllegalArgumentException("Unknown interval type");
        }
    }

    private int getCount(Concept concept, ConceptInterpretationContext context, boolean extent) {
        if (extent == ConceptInterpretationContext.CONTINGENT) {
            return getContingent(concept, context).size();
        } else {
            return getExtent(concept, context).size();
        }
    }

    public boolean isRealized(Concept concept, ConceptInterpretationContext context) {
        /// @todo there might be some reuse with the same method from the DB version
        int extentSize = getCount(concept, context, ConceptInterpretationContext.EXTENT);
        for (Iterator iterator = concept.getDownset().iterator(); iterator.hasNext();) {
            Concept other = (Concept) iterator.next();
            if (other == concept) {
                continue;
            }
            int otherExtentSize = getCount(other, context, ConceptInterpretationContext.EXTENT);
            if (otherExtentSize == extentSize) {
                return false;
            }
        }
        List outerConcepts = context.getNestingConcepts();
        for (Iterator iterator = outerConcepts.iterator(); iterator.hasNext();) {
            Concept outerConcept = (Concept) iterator.next();
            for (Iterator iterator2 = outerConcept.getDownset().iterator(); iterator2.hasNext();) {
                Concept otherConcept = (Concept) iterator2.next();
                if (otherConcept != outerConcept) {
                    for (Iterator iterator3 = this.contingents.keySet().iterator(); iterator3.hasNext();) {
                        ConceptInterpretationContext otherContext = (ConceptInterpretationContext) iterator3.next();
                        List nesting = otherContext.getNestingConcepts();
                        if (nesting.size() != 0) {
                            if (nesting.get(nesting.size() - 1).equals(otherConcept)) {
                                int otherExtentSize = getCount(concept, otherContext, ConceptInterpretationContext.EXTENT);
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

    private Collection getContingent(Concept concept, ConceptInterpretationContext context) {
        Hashtable contextContingents = (Hashtable) contingents.get(context);
        if (contextContingents == null) {
            contextContingents = new Hashtable();
            contingents.put(context, contextContingents);
        }
		TreeSet contingent = (TreeSet) contingents.get(concept);
        if (contingent == null) {
            contingent = calculateContingent(concept, context);
            contextContingents.put(concept, contingent);
        }
        return contingent;
    }

    private TreeSet calculateContingent(Concept concept, ConceptInterpretationContext context) {
		TreeSet retVal = new TreeSet();
        Iterator objectContingentIterator = concept.getObjectContingentIterator();
        while (objectContingentIterator.hasNext()) {
            Object o = objectContingentIterator.next();
            retVal.add(o);
        }
        nestObjects(retVal, context, true);
        filterObjects(retVal, context);
        return retVal;
    }

    private void filterObjects(final TreeSet currentSet, ConceptInterpretationContext context) {
        DiagramHistory.ConceptVisitor visitor;
        final HashSet toRemove = new HashSet();
        if (context.getFilterMode() == ConceptInterpretationContext.EXTENT) {
            visitor = new DiagramHistory.ConceptVisitor() {
                public void visitConcept(Concept concept) {
                    for (Iterator iterator = currentSet.iterator(); iterator.hasNext();) {
                        Object o = iterator.next();
                        boolean found = false;
                        Iterator extentIterator = concept.getExtentIterator();
                        while (extentIterator.hasNext()) {
                            Object o2 = extentIterator.next();
                            if (o.equals(o2)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            toRemove.add(o);
                        }
                    }
                }
            };
        } else {
            visitor = new DiagramHistory.ConceptVisitor() {
                public void visitConcept(Concept concept) {
                    for (Iterator iterator = currentSet.iterator(); iterator.hasNext();) {
                        Object o = iterator.next();
                        boolean found = false;
                        Iterator contingentIterator = concept.getObjectContingentIterator();
                        while (contingentIterator.hasNext()) {
                            Object o2 = contingentIterator.next();
                            if (o.equals(o2)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            toRemove.add(o);
                        }
                    }
                }
            };
        }
        context.getDiagramHistory().visitZoomedConcepts(visitor);
        for (Iterator iterator = toRemove.iterator(); iterator.hasNext();) {
            Object o = iterator.next();
            currentSet.remove(o);
        }
    }

    private void nestObjects(TreeSet currentSet, ConceptInterpretationContext context, boolean contingentOnly) {
        Iterator mainIt = context.getNestingConcepts().iterator();
        while (mainIt.hasNext()) {
            Concept concept = (Concept) mainIt.next();
            HashSet toRemove = new HashSet();
            for (Iterator iterator = currentSet.iterator(); iterator.hasNext();) {
                Object o = iterator.next();
                boolean found = false;
                Iterator objectIterator;
                if(contingentOnly) {
                	objectIterator = concept.getObjectContingentIterator();
                } else {
                	objectIterator = concept.getExtentIterator();
                }
                while (objectIterator.hasNext()) {
                    Object o2 = objectIterator.next();
                    if (o.equals(o2)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    toRemove.add(o);
                }
            }
            for (Iterator iterator = toRemove.iterator(); iterator.hasNext();) {
                Object o = iterator.next();
				currentSet.remove(o);
            }
        }
    }

    private Collection getExtent(Concept concept, ConceptInterpretationContext context) {
    	if(context == null) {
    		throw new RuntimeException("Missing context on call to getExtent(..)");
    	}
        Hashtable contextExtents = (Hashtable) extents.get(context);
        if (contextExtents == null) {
            contextExtents = new Hashtable();
            extents.put(context, contextExtents);
        }
        TreeSet extent = (TreeSet) contextExtents.get(concept);
        if (extent == null) {
            extent = calculateExtent(concept, context);
            contextExtents.put(concept, extent);
        }
        return extent;
    }

    private TreeSet calculateExtent(Concept concept, ConceptInterpretationContext context) {
        TreeSet retVal = new TreeSet();
        Iterator extentContingentIterator = concept.getExtentIterator();
        while (extentContingentIterator.hasNext()) {
            Object o = extentContingentIterator.next();
            retVal.add(o);
        }
        nestObjects(retVal, context, false);
        filterObjects(retVal, context);
        return retVal;
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
			int objectCount = getObjectCount(concept, context);
			retVal = new Object[1];
			if( objectCount != 0) {
				retVal[0] = new Integer(objectCount);
			} else {
				return null;
			}
		} else if (query == AggregateQuery.PERCENT_QUERY) {
			int objectCount = getObjectCount(concept, context);
			retVal = new Object[1];
			if( objectCount != 0) {
				Concept top = concept;
				while(top.getUpset().size() > 1) {
					Iterator it = top.getUpset().iterator();
					Concept upper = (Concept) it.next();
					if(upper != top) {
						top = upper;
					} else {
						top = (Concept) it.next();
					}
				}
				boolean oldMode = context.getObjectDisplayMode();
				context.setObjectDisplayMode(ConceptInterpretationContext.EXTENT);
				int fullExtent = getObjectCount(top,context);
				context.setObjectDisplayMode(oldMode);
				NumberFormat format = DecimalFormat.getNumberInstance();
				format.setMaximumFractionDigits(2);
				retVal[0] = format.format(100 * objectCount/(double)fullExtent) + " %";
			} else {
				return null;
			}
		} else {
			throw new RuntimeException("Query not supported by this class (" + this.getClass().getName() + ")");
		}
		return retVal;
    }

    public int getObjectContingentSize(Concept concept, ConceptInterpretationContext context) {
		return getCount(concept, context, ConceptInterpretationContext.CONTINGENT);
    }

    public int getExtentSize(Concept concept, ConceptInterpretationContext context) {
		return getCount(concept, context, ConceptInterpretationContext.EXTENT);
    }
}
