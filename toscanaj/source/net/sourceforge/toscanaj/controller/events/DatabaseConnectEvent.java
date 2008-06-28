/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.controller.events;

import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import org.tockit.events.Event;

public class DatabaseConnectEvent implements Event<Object> {

    private Object source;
    private DatabaseInfo info;

    public DatabaseConnectEvent(Object source, DatabaseInfo info) {
        this.source = source;
        this.info = info;
    }

    public Object getSubject() {
        return this.source;
    }

    public DatabaseInfo getInfo() {
        return this.info;
    }

    public void setInfo(DatabaseInfo info) {
        this.info = info;
    }
}
