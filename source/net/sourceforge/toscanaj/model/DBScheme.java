/*
 * Created by IntelliJ IDEA.
 * User: rjcole
 * Date: Jun 28, 2002
 * Time: 3:40:32 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.model;

import net.sourceforge.toscanaj.controller.db.DBConnection;
import net.sourceforge.toscanaj.util.STD_Iterator;

import java.util.List;
import java.util.ArrayList;

public class DBScheme {

    List tables;

    public DBScheme() {
        this.tables = new ArrayList();
    }

    void addTable(Table table) {
        this.tables.add(table);
    };

    public List getTables() {
        return tables;
    }

    public void readFromDBConnection(DBConnection connection) {

        STD_Iterator it = new STD_Iterator(connection.getTableNames());

        this.tables.clear();
        for(it.reset();!it.atEnd();it.next()) {
            String tableName = (String)it.val();
            Table  table = new Table(tableName, ""); //@todo get key name
            STD_Iterator colIt = new STD_Iterator(
                connection.getColumnNames(tableName)
            );
            for(colIt.reset(); !it.atEnd(); it.next()) {
                table.addColumn(new Column((String)it.val()));
            }
            addTable(table);
        }
    }
}
