/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.events;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;

import org.tockit.events.Event;

public class DatabaseModifiedEvent implements Event<Object> {

    private final Object source;

    private final DatabaseConnection connection;

    public DatabaseModifiedEvent(final Object source,
            final DatabaseConnection connection) {
        this.source = source;
        this.connection = connection;
    }

    public Object getSubject() {
        return source;
    }

    public DatabaseConnection getConnection() {
        return connection;
    }
}
