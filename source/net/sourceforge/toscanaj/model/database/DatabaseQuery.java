/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.db.WhereClauseGenerator;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;
import net.sourceforge.toscanaj.model.lattice.Concept;

import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

public abstract class DatabaseQuery extends Query {
    public class Column {
        String name;
        String format;
        String separator;
        String queryPart;
    }

    public String header;
    public List columnList = new LinkedList();

    public DatabaseQuery(String name, String header) {
        super(name);
        this.header = header;
    }

    public void insertQueryColumn(String columnName, String columnFormat,
                                  String separator, String queryPart) {
        Column col = new Column();
        col.name = columnName;
        col.format = columnFormat;
        col.separator = separator;
        col.queryPart = queryPart;
        columnList.add(col);
    }

    public List execute(Concept concept, boolean contingentOnly) {
        if( !(concept instanceof DatabaseConnectedConcept) ) {
            throw new RuntimeException("Expected DatabaseConnectedConcept for DatabaseQuery");
        }
        DatabaseConnectedConcept dbConcept = (DatabaseConnectedConcept) concept;
        List retVal = new ArrayList();
        // do a query only if there will be something to query
        // either: there is a contingent in this concept or we query extent and we
        // have subconcepts (at least one should have a contingent, otherwise this
        // concept shouldn't exist)
        if (dbConcept.getObjectClause() != null || (!contingentOnly && !concept.isBottom())) {
            WhereClauseGenerator clauseGenerator = new WhereClauseGenerator();
            String whereClause = clauseGenerator.createWhereClause(dbConcept, null, null, contingentOnly);
            if (whereClause != null) {
                try {
                    String statement = this.getQueryHead() + whereClause;

                    // submit the query
                    List queryResults = DatabaseConnection.getConnection().executeQuery(statement);
                    Iterator it = queryResults.iterator();
                    while (it.hasNext()) {
                        Vector item = (Vector) it.next();
                        DatabaseRetrievedObject object = createDatabaseRetrievedObject(whereClause, item);
                        if(object != null) {
                            retVal.add(object);
                        }
                    }
                } catch (DatabaseException e) {
                    handleDBException(e);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return retVal;
    }

    protected abstract DatabaseRetrievedObject createDatabaseRetrievedObject(String whereClause, Vector values) throws SQLException;

    private void handleDBException(DatabaseException e) {
        /// @TODO Find something useful to do here.
        if (e.getOriginal() != null) {
            System.err.println(e.getMessage());
            e.getOriginal().printStackTrace();
        } else {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Formats a row of a result set for this query.
     *
     * The input is a ResultSet which is supposed to point to an existing
     * row. Column one is supposed to be the first column of the query
     * definition and so on.
     *
     * The return value is a String which returns a formatted version of the
     * row
     */
    public String formatResults(Vector values, int startPosition) throws SQLException {
        String rowRes = new String();
        if (header != null) {
            rowRes += header;
        }
        Iterator colDefIt = this.columnList.iterator();
        // skip key, start with 1
        int i = startPosition;
        while (colDefIt.hasNext()) {
            Column col = (Column) colDefIt.next();
            String value = values.get(i).toString();
            i++;
            if (col.format != null) {
                DecimalFormat format = new DecimalFormat(col.format);
                rowRes += format.format(Double.parseDouble(value));
            } else {
                rowRes += value;
            }
            if (col.separator != null) {
                rowRes += col.separator;
            }
        }
        return rowRes;
    }

    abstract public String getQueryHead();
}
