/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.events;

import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.model.ConceptualSchema;

public class ConceptualSchemaChangeEvent implements Event {
    private ConceptualSchema schema;
    private Object source;

    public ConceptualSchemaChangeEvent(Object source, ConceptualSchema schema) {
        this.schema = schema;
        this.source = source;
    }

    public ConceptualSchema getConceptualSchema() {
        return schema;
    }

    public Object getSubject() {
        return source;
    }
}
