/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.activity;

import net.sourceforge.toscanaj.model.ConceptualSchema;

public class NewConceptualSchemaActivity implements SimpleActivity {
    private ConceptualSchema conceptualSchema;

    public NewConceptualSchemaActivity(ConceptualSchema conceptualSchema) {
        this.conceptualSchema = conceptualSchema;
    }

    public boolean doActivity() throws Exception {
        conceptualSchema.reset();
        return true;
    }
}
