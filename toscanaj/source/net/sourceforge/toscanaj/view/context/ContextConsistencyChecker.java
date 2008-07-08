/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.context;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.fca.DiagramToContextConverter;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;

import org.tockit.context.model.Context;

public class ContextConsistencyChecker {

    public static List<String> checkConsistency(
            final ConceptualSchema conceptualSchema, final Diagram2D diagram,
            final DatabaseConnection databaseConnection, final Component parent) {
        final Context context = DiagramToContextConverter.getContext(diagram);
        return checkConsistency(conceptualSchema, context, databaseConnection,
                parent);
    }

    public static List<String> checkConsistency(
            final ConceptualSchema conceptualSchema, final Context context,
            final DatabaseConnection databaseConnection, final Component parent) {
        final List<String> problems = new ArrayList<String>();
        final DatabaseInfo dbinfo = conceptualSchema.getDatabaseInfo();

        int sumCounts = 0;
        final List<String> validClauses = new ArrayList<String>();

        // check if all objects are WHERE clauses
        Iterator<Object> it = context.getObjects().iterator();
        while (it.hasNext()) {
            final String clause = it.next().toString();
            final String query = "SELECT count(*) FROM "
                    + dbinfo.getTable().getSqlExpression() + " WHERE ("
                    + clause + ");";
            try {
                sumCounts += databaseConnection.queryInt(query, 1);
                validClauses.add(clause);
            } catch (final DatabaseException e) {
                problems.add("Object '" + clause
                        + "' is not a valid clause for the database.\n"
                        + "The database returned:\n\t"
                        + e.getCause().getMessage());
            }
        }

        // check if all conjunctions are empty
        if (problems.isEmpty()) {
            it = context.getObjects().iterator();
            while (it.hasNext()) {
                final String clause = it.next().toString();
                validClauses.remove(clause);
                final Iterator<String> it2 = validClauses.iterator();
                while (it2.hasNext()) {
                    final String otherClause = it2.next();
                    final String query = "SELECT count(*) FROM "
                            + dbinfo.getTable().getSqlExpression() + " WHERE ("
                            + clause + ") AND (" + otherClause + ");";
                    try {
                        final int count = databaseConnection.queryInt(query, 1);
                        if (count != 0) {
                            problems.add("Object clauses '" + clause
                                    + "' and '" + otherClause + "' overlap.");
                        }
                    } catch (final DatabaseException e) {
                        // should not happen
                        ErrorDialog.showError(parent, e,
                                "Error querying the database");
                    }
                }
            }
        }

        // check if disjunction of all contingents covers the data set
        if (problems.isEmpty()) {
            // doesn't make sense if we have problems so far
            final String query = "SELECT count(*) FROM "
                    + dbinfo.getTable().getSqlExpression() + ";";
            try {
                final int count = databaseConnection.queryInt(query, 1);
                if (count != sumCounts) {
                    problems.add("Object clauses do not cover database.");
                }
            } catch (final DatabaseException e) {
                // should not happen
                throw new RuntimeException("Failed to query the database.", e);
            }
        }
        return problems;
    }
}
