/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.WhereClauseGenerator;
import net.sourceforge.toscanaj.controller.db.DatabaseException;

import java.util.List;
import java.util.Collection;
import java.util.Iterator;

import util.CollectionFactory;

public class DatabaseConnectedConceptInterpreter implements ConceptInterpreter {

    DatabaseConnection databaseConnection;

    DatabaseInfo databaseInfo;

    public DatabaseConnectedConceptInterpreter(DatabaseConnection databaseConnection, DatabaseInfo databaseInfo)
    {
        this.databaseConnection = databaseConnection;
        this.databaseInfo = databaseInfo;
    }

    public Iterator getObjectSetIterator(Concept concept, ConceptInterpretationContext context) {
        DatabaseConnectedConcept dbConcept = (DatabaseConnectedConcept) concept;
        boolean displayMode = context.getObjectDisplayMode();
        if( displayMode == ConceptInterpretationContext.CONTINGENT ) {
            return dbConcept.getObjectContingentIterator();
        } else if( displayMode == ConceptInterpretationContext.EXTENT ) {
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
        DatabaseConnectedConcept dbConcept = (DatabaseConnectedConcept) concept;
        String whereClause = WhereClauseGenerator.createWhereClause(dbConcept,
                                    context.getDiagramHistory(),
                                    CollectionFactory.createDefaultList(),
                                    countType,
                                    context.getFilterMode());
        if(whereClause == null) {
            return 0;
        }
        DatabaseConnection connection = DatabaseConnection.getConnection();
        String statement = "SELECT count(*) FROM " + databaseInfo.getTableName() + " " + whereClause;
        return connection.queryNumber(statement, 1);
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
            if( reference == REFERENCE_DIAGRAM ) {
                while(!concept.isTop()) {
                    concept = (Concept) concept.getUpset().iterator().next();
                }
                return (double)extentSize/(double)getCount(concept, context, ConceptInterpretationContext.EXTENT);
            }
            else {
                /// @todo implement
                return 1;
            }
        } catch (DatabaseException e) {
            e.getOriginal().printStackTrace();
            throw new RuntimeException("Error querying database");
        }
    }
}
