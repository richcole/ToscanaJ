/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.events;

import net.sourceforge.toscanaj.model.ConceptualSchema;

import java.io.File;

public class ConceptualSchemaLoadedEvent extends NewConceptualSchemaEvent {
    private File file;

    public ConceptualSchemaLoadedEvent(Object source, ConceptualSchema schema, File file) {
        super(source, schema);
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
