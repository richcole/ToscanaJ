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

public class DistinctListQuery extends Query {
    private DatabaseInfo info;

    public DistinctListQuery(DatabaseInfo info, String name, String header) {
        super(name, header);
        this.info = info;
    }

    public String getQueryHead() {
        String retValue = "SELECT DISTINCT ";
        Iterator it = fieldList.iterator();
        while (it.hasNext()) {
            QueryField field = (QueryField) it.next();
            retValue += field.getQueryPart();
            if (it.hasNext()) {
                retValue += ", ";
            }
        }
        retValue += " FROM " + info.getTableName() + " ";
        return retValue;
    }

    public DatabaseRetrievedObject createDatabaseRetrievedObject(String whereClause, Vector values) {
        String displayString = this.formatResults(values, 0);
        DatabaseRetrievedObject retVal = new DatabaseRetrievedObject(whereClause, displayString);
        String specialWhereClause = whereClause.substring(0, whereClause.lastIndexOf(';'));
        Iterator it = fieldList.iterator();
        Iterator it2 = values.iterator();
        while (it.hasNext() && it2.hasNext()) {
            QueryField field = (QueryField) it.next();
            String value = (String) it2.next();
            specialWhereClause += " AND (" + field.getQueryPart() + "='" + value + "')";
        }
        specialWhereClause += ";";
        retVal.setSpecialWhereClause(specialWhereClause);
        return retVal;
    }
}
