/*
 * Created by IntelliJ IDEA.
 * User: p198
 * Date: Jun 27, 2002
 * Time: 6:03:50 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.controller.events;

import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;

public class DatabaseConnectedEvent implements Event {

    private Object source;

    private DatabaseConnection connection;

    public DatabaseConnectedEvent(Object source, DatabaseConnection connection) {
        this.source = source;
        this.connection = connection;
    }

    public Object getSource() {
        return source;
    }

    public DatabaseConnection getConnection() {
        return connection;
    }
}
