/*
 * Created by IntelliJ IDEA.
 * User: pbecker
 * Date: 18/07/2002
 * Time: 10:11:45
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.model.database;

import java.util.Iterator;

public class DatabaseAggregateQuery extends DatabaseQuery {
    private DatabaseInfo info;

    public DatabaseAggregateQuery(DatabaseInfo info, String name, String header) {
        super(name, header);
        this.info = info;
    }

    public String getQueryHead() {
        // this gives an additional column replacing the key (used only in lists)
        String retValue = "SELECT count(*),";
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
