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

import java.util.*;
import java.sql.SQLException;
import java.text.DecimalFormat;

/**
 * @todo make XMLizable, add conversion into ConceptualSchema
 */
public abstract class Query {
    protected class QueryField {
        private String name;
        private String format;
        private String separator;
        private String queryPart;

        public QueryField(String name, String format, String separator, String queryPart) {
            this.name = name;
            this.format = format;
            this.separator = separator;
            this.queryPart = queryPart;
        }

        public String getName() {
            return name;
        }

        public String getFormat() {
            return format;
        }

        public String getSeparator() {
            return separator;
        }

        public String getQueryPart() {
            return queryPart;
        }
    }

    private String name;

    private String header;
    protected List fieldList = new ArrayList();

    public Query(String name, String header) {
        this.name = name;
        this.header = header;
    }

    public String getName() {
        return this.name;
    }

    public void insertQueryColumn(String columnName, String columnFormat,
                                  String separator, String queryPart) {
        QueryField field = new QueryField(columnName, columnFormat, separator, queryPart);
        fieldList.add(field);
    }

    public List execute(String whereClause) {
        List retVal = new ArrayList();
        if (whereClause != null) {
            try {
                String statement = this.getQueryHead() + whereClause;

                // submit the query
                List queryResults = DatabaseConnection.getConnection().executeQuery(statement);
                Iterator it = queryResults.iterator();
                while (it.hasNext()) {
                    Vector item = (Vector) it.next();
                    DatabaseRetrievedObject object = createDatabaseRetrievedObject(whereClause, item);
                    if (object != null) {
                        retVal.add(object);
                    }
                }
            } catch (DatabaseException e) {
                handleDBException(e);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return retVal;
    }

    protected abstract DatabaseRetrievedObject createDatabaseRetrievedObject(String whereClause, Vector values) throws SQLException;

    private void handleDBException(DatabaseException e) {
        /// @todo Find something useful to do here.
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
        Iterator colDefIt = this.fieldList.iterator();
        // skip key, start with 1
        int i = startPosition;
        while (colDefIt.hasNext()) {
            QueryField field = (QueryField) colDefIt.next();
            String value = values.get(i).toString();
            i++;
            if (field.getFormat() != null) {
                DecimalFormat format = new DecimalFormat(field.getFormat());
                rowRes += format.format(Double.parseDouble(value));
            } else {
                rowRes += value;
            }
            if (field.getSeparator() != null) {
                rowRes += field.getSeparator();
            }
        }
        return rowRes;
    }

    abstract public String getQueryHead();
}
