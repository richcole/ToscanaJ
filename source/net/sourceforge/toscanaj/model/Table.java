/*
 * Created by IntelliJ IDEA.
 * User: p198
 * Date: Jun 27, 2002
 * Time: 5:52:40 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.model;

import java.util.List;

public class Table {

    private String name;
    private String key;
    private List   columns;

    public Table(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public List getColumns() {
        return columns;
    }
}
