/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.events;

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

    public Object getSource() {
        return source;
    }
}
