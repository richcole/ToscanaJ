/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.db.WhereClauseGenerator;
import net.sourceforge.toscanaj.controller.fca.events.ConceptInterpretationContextChangedEvent;
import net.sourceforge.toscanaj.model.database.AggregateQuery;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.DatabaseRetrievedObject;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.lattice.Concept;

import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class DatabaseConnectedConceptInterpreter implements ConceptInterpreter, EventBrokerListener {
    private DatabaseInfo databaseInfo;

	private Hashtable contingentSizes = new Hashtable();
	private Hashtable extentSizes = new Hashtable();
    
    private ListQuery listQuery = null;

    public DatabaseConnectedConceptInterpreter(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
        this.listQuery = new ListQuery(databaseInfo, "", "");
        this.listQuery.insertQueryColumn("", null, "", databaseInfo.getKey().getSqlExpression(), false);
    }

	/**
	 * @todo this implementation is most likely extremely inefficient.
	 */
    public Iterator getObjectSetIterator(Concept concept, ConceptInterpretationContext context) {
    	return Arrays.asList(executeQuery(this.listQuery, concept, context)).iterator();
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

    public int getObjectContingentSize(Concept concept, ConceptInterpretationContext context) {
    	try {
			Hashtable sizes = getContingentSizesCache(context);
			Integer cacheVal = (Integer) sizes.get(concept);
			if (cacheVal != null) {
				return cacheVal.intValue();
			}
			int count = queryCount(concept, context, ConceptInterpretationContext.CONTINGENT);
			sizes.put(concept, new Integer(count));
			return count;
		} catch (DatabaseException e) {
			throw new RuntimeException("Error querying the database", e);
		}
    }

    private int queryCount(Concept concept, ConceptInterpretationContext context, boolean countType) throws DatabaseException {
        String whereClause = WhereClauseGenerator.createWhereClause(concept,
                context.getDiagramHistory(),
                context.getNestingConcepts(),
                countType,
                context.getFilterMode());
        if (whereClause == null) {
            return 0;
        }
        DatabaseConnection connection = DatabaseConnection.getConnection();
        String statement = "SELECT count(*) FROM " + databaseInfo.getTable().getSqlExpression() + " " + whereClause;
        return connection.queryInt(statement, 1);
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

    public int getAttributeCount(Concept concept, ConceptInterpretationContext context) {
        return concept.getAttributeContingentSize();
    }

    /**
     * Gives the relation of the contingent size of the concept given and the largest contingent size queried in the
     * given context up to now.
     *
     * Note that it is not ensured that all contingent sizes have been queried before this call, this is up to the
     * caller.
     */
    public double getRelativeObjectContingentSize(Concept concept, ConceptInterpretationContext context) {
        int contingentSize = getObjectContingentSize(concept, context);
        if (contingentSize == 0) {
            return 0; //avoids division by zero
        }
        return (double) contingentSize / (double) getMaximalContingentSize();
    }

    /**
     * This returns the maximal contingent found up to now.
     */
    private int getMaximalContingentSize() {
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

    public double getRelativeExtentSize(Concept concept, ConceptInterpretationContext context) {
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
        while (!compareConcept.isTop()) {
            Concept other = compareConcept;
            Iterator it = compareConcept.getUpset().iterator();
            do {
                other = (Concept) it.next();
            } while (other == compareConcept);
            compareConcept = other;
        }
        return (double) extentSize /
                (double) getExtentSize(compareConcept, compareContext);
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

    private void clearCaches(ConceptInterpretationContext context) {
		this.contingentSizes.remove(context);
		this.extentSizes.remove(context);
    }

    public void processEvent(Event e) {
        clearCaches((ConceptInterpretationContext) e.getSubject());
    }

    public Object[] executeQuery(Query query, Concept concept, ConceptInterpretationContext context) {
		if(!isRealized(concept, context)) {
			return null;
		}
		boolean objectDisplayMode = context.getObjectDisplayMode();
		boolean filterMode = context.getFilterMode();
		String whereClause = WhereClauseGenerator.createWhereClause(concept,
				context.getDiagramHistory(),
				context.getNestingConcepts(),
				objectDisplayMode,
				filterMode);
		Object[] retVal;
		if (query == ListQuery.KEY_LIST_QUERY) {
			int objectCount = getObjectCount(concept, context);
			retVal = new Object[objectCount];
			if( objectCount != 0) {
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
				retVal[0] = new DatabaseRetrievedObject(whereClause, String.valueOf(objectCount));
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
				retVal[0] = new DatabaseRetrievedObject(whereClause, format.format(100 * objectCount/(double)fullExtent) + " %");
			} else {
				return null;
			}
		} else {
	        if (concept.getObjectContingentSize() != 0 ||
	                ((objectDisplayMode == ConceptInterpretationContext.EXTENT) && !concept.isBottom())
	        ) {
				if(query.doesNeedReferenceValues()){
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
					String referenceWhereClause = WhereClauseGenerator.createWhereClause(top,
							context.getDiagramHistory(),
							context.getNestingConcepts(),
							ConceptInterpretationContext.EXTENT,
							filterMode);
					return execute(query, whereClause, referenceWhereClause);
				} else {
					return execute(query, whereClause, null);
				}
	        } else {
	            return null;
	        }
		}
		return retVal;
    }

    private Object[] execute(Query query, String whereClause, String referenceWhereClause) {
        Object[] retVal = null;
        if (whereClause != null) {
        	String statement = query.getQueryHead() + whereClause;
            try {
                // submit the query
                List queryResults = DatabaseConnection.getConnection().executeQuery(statement);
                retVal = new Object[queryResults.size()];
				Vector reference = null;
				if(referenceWhereClause != null){
					/// @todo this should be cached since it gets reused for every single object label in a diagram
					List referenceResults = DatabaseConnection.getConnection().executeQuery(query.getQueryHead() + referenceWhereClause);
					reference = (Vector) referenceResults.iterator().next();
				}
                Iterator it = queryResults.iterator();
                int pos = 0;
                while (it.hasNext()) {
                    Vector item = (Vector) it.next();
                    DatabaseRetrievedObject object =
                            query.createDatabaseRetrievedObject(whereClause, item, reference);
                    /// @todo what does this check do? what happens if it is null?
                    if (object != null) {
                        retVal[pos] = object;
                        pos++;
                    }
                }
            } catch (DatabaseException e) {
                handleDBException(e, statement);
            }
        }
        return retVal;
    }

	/**
	 * @todo throw something more specific here
	 */
    private void handleDBException(DatabaseException e, String sqlStatement) {
    	throw new RuntimeException("Error querying the database, the following SQL expression failed:\n" +
    							    sqlStatement , e);
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

    private int calculateExtentSize(Concept concept, ConceptInterpretationContext context) {
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
    
	public void clearCache() {
		this.contingentSizes.clear();
		this.extentSizes.clear();
	}
}
