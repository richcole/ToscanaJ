/*
 * Created by IntelliJ IDEA.
 * User: p198
 * Date: Jun 27, 2002
 * Time: 6:11:52 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.gui.events;

import net.sourceforge.toscanaj.model.DBScheme;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.events.Event;

public class DBSchemeChangedEvent implements Event {

    private DBScheme dbScheme;
    private Object   source;

    public DBSchemeChangedEvent(Object source, DBScheme dbScheme) {
        this.source = source;
        this.dbScheme = dbScheme;
    }

    public DBScheme getDBScheme() {
        return dbScheme;
    }

    public Object getSource() {
        return source;
    }
}
