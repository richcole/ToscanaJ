/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
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
