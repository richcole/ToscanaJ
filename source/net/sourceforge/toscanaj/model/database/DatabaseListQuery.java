/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.database;

import java.util.Iterator;

public class DatabaseListQuery extends DatabaseQuery {
    private DatabaseInfo info;

    public DatabaseListQuery(DatabaseInfo info, String name, String header) {
        super(name, header);
        this.info = info;
    }

    public String getQueryHead() {
        String retValue = "SELECT ";
        retValue += info.getKey() + ", ";
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
}
