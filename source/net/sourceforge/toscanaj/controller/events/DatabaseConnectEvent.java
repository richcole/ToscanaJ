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
import net.sourceforge.toscanaj.model.DatabaseInfo;

public class DatabaseConnectEvent implements Event {

    private Object source;
    private DatabaseInfo info;

    public DatabaseConnectEvent(Object source, DatabaseInfo info){
        this.source = source;
        this.info = info;
    }

    public Object getSource() {
        return source;
    }

    public DatabaseInfo getInfo() {
        return info;
    }

    public void setInfo(DatabaseInfo info) {
        this.info = info;
    }
}
