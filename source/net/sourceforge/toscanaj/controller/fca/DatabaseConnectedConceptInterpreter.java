/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.controller.db.*;
import net.sourceforge.toscanaj.controller.fca.events.ConceptInterpretationContextChangedEvent;
import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.model.database.*;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;

import java.util.*;
import java.sql.SQLException;

public class DatabaseConnectedConceptInterpreter implements ConceptInterpreter, BrokerEventListener {

    private DatabaseConnection databaseConnection;

    private DatabaseInfo databaseInfo;

    private Hashtable extentSizes = new Hashtable();
    private Hashtable contingentSizes = new Hashtable();

    public DatabaseConnectedConceptInterpreter(DatabaseConnection databaseConnection,
                                               DatabaseInfo databaseInfo) {
        this.databaseConnection = databaseConnection;
        this.databaseInfo = databaseInfo;
    }

    public Iterator getObjectSetIterator(Concept concept, ConceptInterpretationContext context) {
        DatabaseConnectedConcept dbConcept = (DatabaseConnectedConcept) concept;
        boolean displayMode = context.getObjectDisplayMode();
        if (displayMode == ConceptInterpretationContext.CONTINGENT) {
            return dbConcept.getObjectContingentIterator();
        } else if (displayMode == ConceptInterpretationContext.EXTENT) {
            return dbConcept.getExtentIterator();
        } else {
            throw new RuntimeException("Can't happen");
        }
    }

    public Iterator getAttributeSetIterator(Concept concept, ConceptInterpretationContext context) {
        return concept.getAttributeContingentIterator();
    }

    public int getObjectCount(Concept concept, ConceptInterpretationContext context) {
        try {
            return getCount(concept, context, context.getObjectDisplayMode());
        } catch (DatabaseException e) {
            e.getOriginal().printStackTrace();
            throw new RuntimeException("Error accessing the database");
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
        DatabaseConnectedConcept dbConcept = (DatabaseConnectedConcept) concept;
        String whereClause = WhereClauseGenerator.createWhereClause(dbConcept,
                context.getDiagramHistory(),
                context.getNestingConcepts(),
                countType,
                context.getFilterMode());
        if (whereClause == null) {
            return 0;
        }
        DatabaseConnection connection = DatabaseConnection.getConnection();
        String statement = "SELECT count(*) FROM " + databaseInfo.getTableName() + " " + whereClause;
        int count = connection.queryNumber(statement, 1);
        sizes.put(concept, new Integer(count));
        return count;
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

    public double getRelativeObjectContingentSize(Concept concept, ConceptInterpretationContext context, int reference) {
        /// @todo implement
        return 1;
    }

    public double getRelativeExtentSize(Concept concept, ConceptInterpretationContext context, int reference) {
        try {
            int extentSize = getCount(concept, context, ConceptInterpretationContext.EXTENT);
            if (reference == REFERENCE_DIAGRAM) {
                /// @todo add way to find top concept more easily
                while (!concept.isTop()) {
                    Concept other = concept;
                    Iterator it = concept.getUpset().iterator();
                    do {
                        other = (Concept) it.next();
                    } while (other == concept);
                    concept = other;
                }
                if (extentSize == 0) {
                    return 0; //avoids division by zero
                }
                return (double) extentSize / (double) getCount(concept, context, ConceptInterpretationContext.EXTENT);
            } else {
                /// @todo implement
                return 1;
            }
        } catch (DatabaseException e) {
            e.getOriginal().printStackTrace();
            throw new RuntimeException("Error querying database");
        }
    }

    public boolean isRealized(Concept concept, ConceptInterpretationContext context) {
        /// @todo do check only lower neighbours
        try {
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
            return true;
        } catch (DatabaseException e) {
            e.getOriginal().printStackTrace();
            throw new RuntimeException("Error querying the database");
        }
    }

    private void clearCaches(ConceptInterpretationContext context) {
        extentSizes.remove(context);
        contingentSizes.remove(context);
    }

    public void processEvent(Event e) {
        clearCaches((ConceptInterpretationContext) e.getSource());
    }

    public List executeQuery(Query query, Concept concept, ConceptInterpretationContext context) {
        DatabaseConnectedConcept dbConcept = (DatabaseConnectedConcept) concept;
        boolean objectDisplayMode = context.getObjectDisplayMode();
        boolean filterMode = context.getFilterMode();
        if (dbConcept.getObjectClause() != null ||
                ((objectDisplayMode == ConceptInterpretationContext.EXTENT) && !dbConcept.isBottom())
        ) {
            String whereClause = WhereClauseGenerator.createWhereClause(dbConcept,
                    context.getDiagramHistory(),
                    context.getNestingConcepts(),
                    objectDisplayMode,
                    filterMode);
            return execute(query, whereClause);
        } else {
            return null;
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
        /// @todo Find something useful to do here.
        if (e.getOriginal() != null) {
            System.err.println(e.getMessage());
            e.getOriginal().printStackTrace();
        } else {
            e.printStackTrace(System.err);
        }
    }

}
