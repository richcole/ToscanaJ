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
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.DatabaseRetrievedObject;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.lattice.Concept;

import java.util.*;

public class DatabaseConnectedConceptInterpreter extends AbstractConceptInterperter {
    private DatabaseInfo databaseInfo;

    private ListQuery listQuery = null;

    public DatabaseConnectedConceptInterpreter(DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
        this.listQuery = new ListQuery(databaseInfo, "", "");
        this.listQuery.insertQueryColumn("", null, "", databaseInfo.getKey().getSqlExpression(), false);
    }

    public Iterator getObjectSetIterator(Concept concept, ConceptInterpretationContext context) {
    	// for us a KeyList query is nothing but a normal list query with a specific setup
    	return Arrays.asList(handleNonDefaultQuery(this.listQuery, concept, context)).iterator();
    }

	protected int calculateContingentSize (Concept concept, ConceptInterpretationContext context) {
		try {
			String whereClause = WhereClauseGenerator.createWhereClause(concept,
					context.getDiagramHistory(),
					context.getNestingConcepts(),
					ConceptInterpretationContext.CONTINGENT,
					context.getFilterMode());
			if (whereClause == null) {
				return 0;
			}
			DatabaseConnection connection = DatabaseConnection.getConnection();
			String statement = "SELECT count(*) FROM " + databaseInfo.getTable().getSqlExpression() + " " + whereClause;
			return connection.queryInt(statement, 1);
		} catch (DatabaseException e) {
			throw new RuntimeException("Error querying the database", e);
		}
	}

	protected Object getObject(String value, Concept concept, ConceptInterpretationContext context) {
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
}
