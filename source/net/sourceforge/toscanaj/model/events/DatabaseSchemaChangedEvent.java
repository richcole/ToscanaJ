/*
 * Created by IntelliJ IDEA.
 * User: p198
 * Date: Jun 27, 2002
 * Time: 6:11:52 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.model.events;

import net.sourceforge.toscanaj.model.database.DatabaseSchema;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.events.Event;

public class DatabaseSchemaChangedEvent implements Event {

    private DatabaseSchema dbScheme;
    private Object   source;

    public DatabaseSchemaChangedEvent(Object source, DatabaseSchema dbScheme) {
        this.source = source;
        this.dbScheme = dbScheme;
    }

    public DatabaseSchema getDBScheme() {
        return dbScheme;
    }

    public Object getSource() {
        return source;
    }
}
