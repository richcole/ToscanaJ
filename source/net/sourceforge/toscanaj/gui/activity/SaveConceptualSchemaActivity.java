/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.activity;

import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.XML_Writer;
import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.events.EventBroker;

import java.io.File;

public class SaveConceptualSchemaActivity implements FileActivity {
    private ConceptualSchema conceptualSchema;

    public SaveConceptualSchemaActivity(ConceptualSchema conceptualSchema) {
        this.conceptualSchema = conceptualSchema;
    }

    public void processFile(File file) throws Exception {
        XML_Writer.write(file, conceptualSchema);
    }

    public boolean prepareToProcess() throws Exception {
        return true;
    }
}
