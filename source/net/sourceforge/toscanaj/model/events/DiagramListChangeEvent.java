/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.events;

import net.sourceforge.toscanaj.model.ConceptualSchema;

public class DiagramListChangeEvent extends ConceptualSchemaChangeEvent {
    public DiagramListChangeEvent(Object source, ConceptualSchema schema) {
        super(source, schema);
    }
}
