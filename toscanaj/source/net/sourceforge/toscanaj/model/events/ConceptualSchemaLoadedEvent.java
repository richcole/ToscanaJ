/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.events;

import net.sourceforge.toscanaj.model.ConceptualSchema;

import java.io.File;

public class ConceptualSchemaLoadedEvent extends NewConceptualSchemaEvent {
    private File file;

    public ConceptualSchemaLoadedEvent(Object source, ConceptualSchema schema, File file) {
        super(schema);
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
