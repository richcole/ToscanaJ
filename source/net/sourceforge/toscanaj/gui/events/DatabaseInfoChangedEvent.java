/*
 * Created by IntelliJ IDEA.
 * User: p198
 * Date: Jun 27, 2002
 * Time: 6:11:52 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package net.sourceforge.toscanaj.gui.events;

import net.sourceforge.toscanaj.model.DatabaseInfo;
import net.sourceforge.toscanaj.model.ConceptualSchema;

public class DatabaseInfoChangedEvent extends ConceptualSchemaChangeEvent {

    private DatabaseInfo info;

    public DatabaseInfoChangedEvent(Object source, ConceptualSchema schema, DatabaseInfo info) {
        super(source, schema);
        this.info = info;
    }

    public DatabaseInfo getInfo() {
        return info;
    }
}
