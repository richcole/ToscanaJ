/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.fca;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.db.WhereClauseGenerator;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.DatabaseRetrievedObject;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.lattice.Concept;

import org.tockit.swing.preferences.ExtendedPreferences;

public class DatabaseConnectedConceptInterpreter<Oc, A> extends
        AbstractConceptInterpreter<Oc, A, FCAElement> {
    private static final ExtendedPreferences preferences = ExtendedPreferences
            .userNodeForClass(DatabaseConnectedConceptInterpreter.class);

    private final DatabaseInfo databaseInfo;

    private ListQuery listQuery = null;

    public DatabaseConnectedConceptInterpreter(final DatabaseInfo databaseInfo) {
        this.databaseInfo = databaseInfo;
        this.listQuery = new ListQuery(databaseInfo, "", "");
        this.listQuery.insertQueryColumn("", null, "", databaseInfo.getKey()
                .getSqlExpression(), false);
    }

    @Override
    public Iterator<FCAElement> getObjectSetIterator(
            final Concept<Oc, A> concept,
            final ConceptInterpretationContext<Oc, A> context) {
        // for us a KeyList query is nothing but a normal list query with a
        // specific setup
        return Arrays.asList(
                handleNonDefaultQuery(this.listQuery, concept, context))
                .iterator();
    }

    @Override
    protected int calculateContingentSize(final Concept<Oc, A> concept,
            final ConceptInterpretationContext<Oc, A> context) {
        try {
            final String whereClause = WhereClauseGenerator.createWhereClause(
                    concept, context.getDiagramHistory(), context
                            .getNestingConcepts(),
                    ConceptInterpretationContext.CONTINGENT, context
                            .getFilterMode());
            if (whereClause == null) {
                return 0;
            }
            final DatabaseConnection connection = DatabaseConnection
                    .getConnection();
            final String statement = "SELECT count(*) FROM "
                    + this.databaseInfo.getTable().getSqlExpression() + " "
                    + whereClause + ";";
            return connection.queryInt(statement, 1);
        } catch (final DatabaseException e) {
            throw new RuntimeException("Error querying the database", e);
        }
    }

    @Override
    protected FCAElement getObject(final String value,
            final Concept<Oc, A> concept,
            final ConceptInterpretationContext<Oc, A> context) {
        final String whereClause = WhereClauseGenerator.createWhereClause(
                concept, context.getDiagramHistory(), context
                        .getNestingConcepts(), context.getObjectDisplayMode(),
                context.getFilterMode());
        return new DatabaseRetrievedObject(whereClause, value);
    }

    @Override
    protected FCAElement[] handleNonDefaultQuery(final Query query,
            final Concept<Oc, A> concept,
            final ConceptInterpretationContext<Oc, A> context) {
        final String whereClause = WhereClauseGenerator.createWhereClause(
                concept, context.getDiagramHistory(), context
                        .getNestingConcepts(), context.getObjectDisplayMode(),
                context.getFilterMode());
        if (concept.getObjectContingentSize() != 0
                || ((context.getObjectDisplayMode() == ConceptInterpretationContext.EXTENT) && !concept
                        .isBottom())) {
            if (query.doesNeedReferenceValues()) {
                final String referenceWhereClause = WhereClauseGenerator
                        .createWhereClause(concept.getTopConcept(), context
                                .getDiagramHistory(), context
                                .getNestingConcepts(),
                                ConceptInterpretationContext.EXTENT, context
                                        .getFilterMode());
                return execute(query, whereClause, referenceWhereClause);
            } else {
                return execute(query, whereClause, null);
            }
        } else {
            return new FCAElement[0];
        }
    }

    private FCAElement[] execute(final Query query, final String whereClause,
            final String referenceWhereClause) {
        FCAElement[] retVal = null;
        if (whereClause != null) {
            String statement = query.getQueryHead() + whereClause;
            if (preferences.getBoolean("useOrderBy", false)) {
                statement += " " + query.getOrderClause();
            }
            statement += ";";
            try {
                // submit the query
                final List<String[]> queryResults = DatabaseConnection
                        .getConnection().executeQuery(statement);
                // first of all, check results for NULL values. That can happen
                // if we have the infimum
                // of the realized lattice and it has an empty contingent but an
                // object label attached.
                // In that case SQL aggregates will return NULL, if we try to
                // display this we create
                // either exceptions or some other mess. We return null instead.
                if (queryResults.size() != 0) {
                    final String[] firstResult = queryResults.get(0);
                    for (final String element : firstResult) {
                        if (element == null) {
                            return null;
                        }
                    }
                }
                retVal = new FCAElement[queryResults.size()];
                String[] reference = null;
                if (referenceWhereClause != null) {
                    // / @todo this should be cached since it gets reused for
                    // every single object label in a diagram
                    final List<String[]> referenceResults = DatabaseConnection
                            .getConnection()
                            .executeQuery(
                                    query.getQueryHead() + referenceWhereClause);
                    reference = referenceResults.iterator().next();
                }
                final Iterator<String[]> it = queryResults.iterator();
                int pos = 0;
                while (it.hasNext()) {
                    final String[] item = it.next();
                    final DatabaseRetrievedObject object = query
                            .createDatabaseRetrievedObject(whereClause, item,
                                    reference);
                    // / @todo what does this check do? what happens if it is
                    // null?
                    if (object != null) {
                        retVal[pos] = object;
                        pos++;
                    }
                }
            } catch (final DatabaseException e) {
                handleDBException(e, statement);
            }
        }
        return retVal;
    }

    /**
     * @todo throw something more specific here
     */
    private void handleDBException(final DatabaseException e,
            final String sqlStatement) {
        throw new RuntimeException(
                "Error querying the database, the following SQL expression failed:\n"
                        + sqlStatement, e);
    }
}
