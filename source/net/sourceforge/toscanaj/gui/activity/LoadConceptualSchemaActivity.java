/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.activity;

import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;

import java.io.File;

public class LoadConceptualSchemaActivity implements FileActivity {
    private ConceptualSchema conceptualSchema;

    public LoadConceptualSchemaActivity(ConceptualSchema conceptualSchema) {
        this.conceptualSchema = conceptualSchema;
    }

    public void processFile(File file) throws Exception {
        ConceptualSchema newSchema = null;
        try {
            newSchema = CSXParser.parse(file);
        } catch (DataFormatException e) {
            throw e;
        }
        conceptualSchema.copyContents(newSchema);
    }

    public boolean prepareToProcess() throws Exception {
        return true;
    }
}
