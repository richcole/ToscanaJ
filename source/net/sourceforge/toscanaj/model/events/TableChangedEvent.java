/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.events;

import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.model.database.Table;

public class TableChangedEvent implements Event {
    Object source;
    Table table;

    public TableChangedEvent(Object source, Table table) {
        this.source = source;
        this.table = table;
    }

    public Table getTable() {
        return table;
    }

    public Object getSource() {
        return source;
    }
}
