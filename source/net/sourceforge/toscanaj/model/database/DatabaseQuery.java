/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    };

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
    public String formatResults(ResultSet results) throws SQLException {
        String rowRes = new String();
        if (header != null) {
            rowRes += header;
        }
        Iterator colDefIt = this.columnList.iterator();
        // skip key, start with 1
        int i = 1;
        while (colDefIt.hasNext()) {
            Column col = (Column) colDefIt.next();
            i++;
            if (col.format != null) {
                DecimalFormat format = new DecimalFormat(col.format);
                rowRes += format.format(results.getDouble(i));
            } else {
                rowRes += results.getString(i);
            }
            if (col.separator != null) {
                rowRes += col.separator;
            }
        }
        return rowRes;
    }

    abstract public String getQueryHead();
}
