/*
 * Created by IntelliJ IDEA.
 * User: pbecker
 * Date: 18/07/2002
 * Time: 10:12:28
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.model.database;

import java.util.Iterator;

public class DatabaseListQuery extends DatabaseQuery {
    public boolean isDistinct;
    private DatabaseInfo info;

    public DatabaseListQuery(DatabaseInfo info, String name, String header, boolean isDistinct) {
        super(name, header);
        this.info = info;
        this.isDistinct = isDistinct;
    }

    public String getQueryHead() {
        String retValue = "SELECT ";
        if (isDistinct) {
            retValue += "DISTINCT ";
        }
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
