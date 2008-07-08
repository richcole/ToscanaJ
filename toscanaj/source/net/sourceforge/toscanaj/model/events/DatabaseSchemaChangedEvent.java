/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.events;

import net.sourceforge.toscanaj.model.database.DatabaseSchema;

import org.tockit.events.Event;

public class DatabaseSchemaChangedEvent implements Event {

    private final DatabaseSchema dbScheme;
    private final Object source;

    public DatabaseSchemaChangedEvent(final Object source,
            final DatabaseSchema dbScheme) {
        this.source = source;
        this.dbScheme = dbScheme;
    }

    public DatabaseSchema getDBScheme() {
        return dbScheme;
    }

    public Object getSubject() {
        return source;
    }
}
