/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
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
