/*
 * Created by IntelliJ IDEA.
 * User: rjcole
 * Date: Jun 29, 2002
 * Time: 6:28:40 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.model.events;

import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.model.Table;

public class TableChangedEvent implements Event {
    Object source;
    Table table;

    public TableChangedEvent(Object source, Table table) {
        this.source = source;
        this.table = table;
    }

    public Table getTable() {
        return table;
    }

    public Object getSource() {
        return source;
    }
}
