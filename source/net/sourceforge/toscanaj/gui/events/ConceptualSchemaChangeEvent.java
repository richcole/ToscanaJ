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
    private ConceptualSchema source;

    public ConceptualSchemaChangeEvent(ConceptualSchema source) {
        this.source = source;
    }

    public ConceptualSchema getConceptualSchema() {
        return source;
    }

    public Object getSource() {
        return source;
    }
}
