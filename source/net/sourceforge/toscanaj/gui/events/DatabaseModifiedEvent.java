/*
 * Created by IntelliJ IDEA.
 * User: p198
 * Date: Jun 27, 2002
 * Time: 6:03:50 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.gui.events;

import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.controller.db.DBConnection;

public class DatabaseModifiedEvent implements Event {

    private Object source;

    private DBConnection connection;

    public DatabaseModifiedEvent(Object source, DBConnection connection) {
        this.source = source;
        this.connection = connection;
    }

    public Object getSource() {
        return source;
    }

    public DBConnection getConnection() {
        return connection;
    }
}
