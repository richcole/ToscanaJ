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

public class DatabaseConnectedConceptInterpreter implements ConceptInterpreter, EventBrokerListener {
    private DatabaseInfo databaseInfo;

    private Hashtable extentSizes = new Hashtable();
    private Hashtable contingentSizes = new Hashtable();
    
    private ListQuery listQuery = null;

    public DatabaseConnectedConceptInterpreter(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
        this.listQuery = new ListQuery(databaseInfo, "", "");
        this.listQuery.insertQueryColumn("", null, "", databaseInfo.getKey());
    }

    public Iterator getObjectSetIterator(Concept concept, ConceptInterpretationContext context) {
    	return executeQuery(this.listQuery, concept, context).iterator();
    }

    public Iterator getAttributeSetIterator(Concept concept, ConceptInterpretationContext context) {
        return concept.getAttributeContingentIterator();
    }

    public int getObjectCount(Concept concept, ConceptInterpretationContext context) {
        try {
            return getCount(concept, context, context.getObjectDisplayMode());
        } catch (DatabaseException e) {
            throw new RuntimeException("Error accessing the database", e);
        }
    }

    private int getCount(Concept concept, ConceptInterpretationContext context, boolean countType) throws DatabaseException {
        Hashtable sizes;
        if (countType == ConceptInterpretationContext.CONTINGENT) {
            sizes = getContingentSizesCache(context);
        } else {
            sizes = getExtentSizesCache(context);
        }
        Integer cacheVal = (Integer) sizes.get(concept);
        if (cacheVal != null) {
            return cacheVal.intValue();
        }
        int count = queryCount(concept, context, countType);
        sizes.put(concept, new Integer(count));
        return count;
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
        String statement = "SELECT count(*) FROM " + databaseInfo.getSQLTableName() + " " + whereClause;
        return connection.queryInt(statement, 1);
    }

    private Hashtable getContingentSizesCache(ConceptInterpretationContext context) {
        Hashtable retVal = (Hashtable) contingentSizes.get(context);
        if (retVal == null) {
            retVal = new Hashtable();
            contingentSizes.put(context, retVal);
            /// @todo wouldn't it be better to implement equals/hashvalue on the context object
            context.getEventBroker().subscribe(this,
                    ConceptInterpretationContextChangedEvent.class,
                    ConceptInterpretationContext.class);
        }
        return retVal;
    }

    private Hashtable getExtentSizesCache(ConceptInterpretationContext context) {
        Hashtable retVal = (Hashtable) extentSizes.get(context);
        if (retVal == null) {
            retVal = new Hashtable();
            extentSizes.put(context, retVal);
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
    public double getRelativeObjectContingentSize(Concept concept, ConceptInterpretationContext context, int reference) {
        try {
            int contingentSize = getCount(concept, context, ConceptInterpretationContext.CONTINGENT);
            if (reference == REFERENCE_DIAGRAM) {
                if (contingentSize == 0) {
                    return 0; //avoids division by zero
                }
                return (double) contingentSize / (double) getMaximalContingentSize();
            } else {
                /// @todo implement or remove the distinction
                return 1;
            }
        } catch (DatabaseException e) {
            throw new RuntimeException("Error querying database", e);
        }
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

    public double getRelativeExtentSize(Concept concept, ConceptInterpretationContext context, int reference) {
        try {
            int extentSize = getCount(concept, context, ConceptInterpretationContext.EXTENT);
            if (extentSize == 0) {
                return 0; //avoids division by zero
            }
            if (reference == REFERENCE_DIAGRAM) {
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
                        (double) getCount(compareConcept, compareContext, ConceptInterpretationContext.EXTENT);
            } else {
                /// @todo implement or remove the distinction
                return 1;
            }
        } catch (DatabaseException e) {
            throw new RuntimeException("Error querying database", e);
        }
    }

    public boolean isRealized(Concept concept, ConceptInterpretationContext context) {
        /// @todo do check only lower neighbours
        /// @todo consider going back to creating the lattice product, this is too much hacking
        try {
        	// first we check the inner diagram if anything below the concept has the same extent
        	// size (iff any subconcept has the same extent size, the concept is not realized) 
            int extentSize = getCount(concept, context, ConceptInterpretationContext.EXTENT);
            for (Iterator iterator = concept.getDownset().iterator(); iterator.hasNext();) {
                Concept otherConcept = (Concept) iterator.next();
                if (otherConcept == concept) {
                    continue;
                }
                int otherExtentSize = getCount(otherConcept, context, ConceptInterpretationContext.EXTENT);
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
                        for (Iterator iterator3 = this.extentSizes.keySet().iterator(); iterator3.hasNext();) {
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
                        for (Iterator iterator3 = this.contingentSizes.keySet().iterator(); iterator3.hasNext();) {
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
        } catch (DatabaseException e) {
            throw new RuntimeException("Error querying the database", e);
        }
    }

    private void clearCaches(ConceptInterpretationContext context) {
        extentSizes.remove(context);
        contingentSizes.remove(context);
    }

    public void processEvent(Event e) {
        clearCaches((ConceptInterpretationContext) e.getSubject());
    }

    public List executeQuery(Query query, Concept concept, ConceptInterpretationContext context) {
        boolean objectDisplayMode = context.getObjectDisplayMode();
        boolean filterMode = context.getFilterMode();
        if (concept.getObjectContingentSize() != 0 ||
                ((objectDisplayMode == ConceptInterpretationContext.EXTENT) && !concept.isBottom())
        ) {
            String whereClause = WhereClauseGenerator.createWhereClause(concept,
                    context.getDiagramHistory(),
                    context.getNestingConcepts(),
                    objectDisplayMode,
                    filterMode);
            return execute(query, whereClause);
        } else {
            return new ArrayList();
        }
    }

    private List execute(Query query, String whereClause) {
        List retVal = new ArrayList();
        if (whereClause != null) {
            try {
                String statement = query.getQueryHead() + whereClause;

                // submit the query
                List queryResults = DatabaseConnection.getConnection().executeQuery(statement);
                Iterator it = queryResults.iterator();
                while (it.hasNext()) {
                    Vector item = (Vector) it.next();
                    DatabaseRetrievedObject object =
                            query.createDatabaseRetrievedObject(whereClause, item);
                    if (object != null) {
                        retVal.add(object);
                    }
                }
            } catch (DatabaseException e) {
                handleDBException(e);
            }
        }
        return retVal;
    }

    private void handleDBException(DatabaseException e) {
    	throw new RuntimeException("Error querying the database", e);
    }

}
