/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

import java.util.Iterator;
import java.util.Vector;
import java.sql.SQLException;

public class DatabaseDistinctListQuery extends DatabaseQuery {
    private DatabaseInfo info;

    public DatabaseDistinctListQuery(DatabaseInfo info, String name, String header) {
        super(name, header);
        this.info = info;
    }

    public String getQueryHead() {
        String retValue = "SELECT DISTINCT ";
        Iterator it = columnList.iterator();
        while (it.hasNext()) {
            Column col = (Column) it.next();
            retValue += col.queryPart;
            if (it.hasNext()) {
                retValue += ", ";
            }
        }
        retValue += " FROM " + info.getTableName() + " ";
        return retValue;
    }

    protected DatabaseRetrievedObject createDatabaseRetrievedObject(Vector values) throws SQLException {
        String displayString = this.formatResults(values, 0);
        DatabaseRetrievedObject retVal = new DatabaseRetrievedObject(displayString);
        return retVal;
    }
}
