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
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.DatabaseRetrievedObject;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.lattice.Concept;

import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

import java.util.*;

public class DatabaseConnectedConceptInterpreter extends AbstractConceptInterperter 
										implements ConceptInterpreter, EventBrokerListener {
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

    private void clearCaches(ConceptInterpretationContext context) {
		this.contingentSizes.remove(context);
		this.extentSizes.remove(context);
    }

    public void processEvent(Event e) {
        clearCaches((ConceptInterpretationContext) e.getSubject());
    }


	protected Object  getObject (String value, Concept concept, ConceptInterpretationContext context) {
		String whereClause = WhereClauseGenerator.createWhereClause(concept,
										context.getDiagramHistory(),
										context.getNestingConcepts(),
										context.getObjectDisplayMode(),
										context.getFilterMode());
		return new DatabaseRetrievedObject(whereClause, value);		
	}
	
	protected Object[] handleNonDefaultQuery(Query query, Concept concept, ConceptInterpretationContext context) {
		String whereClause = WhereClauseGenerator.createWhereClause(concept,
									context.getDiagramHistory(),
									context.getNestingConcepts(),
									context.getObjectDisplayMode(),
									context.getFilterMode());											
		if (concept.getObjectContingentSize() != 0 ||
				((context.getObjectDisplayMode() == ConceptInterpretationContext.EXTENT) && !concept.isBottom())
		) {
			if(query.doesNeedReferenceValues()){
				String referenceWhereClause = WhereClauseGenerator.createWhereClause(
											concept.getTopConcept(),
											context.getDiagramHistory(),
											context.getNestingConcepts(),
											ConceptInterpretationContext.EXTENT,
											context.getFilterMode());
				return execute(query, whereClause, referenceWhereClause);
			} else {
				return execute(query, whereClause, null);
			}
		} else {
			return null;
		}
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
