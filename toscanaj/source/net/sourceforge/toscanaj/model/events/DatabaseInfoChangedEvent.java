/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.events;

import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;

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
