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
import net.sourceforge.toscanaj.controller.db.DBConnection;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.gui.events.ConceptualSchemaChangeEvent;

import java.io.File;

public class LoadConceptualSchemaActivity implements FileActivity {
    private EventBroker broker;
    private DBConnection databaseConnection;

    public LoadConceptualSchemaActivity(EventBroker broker, DBConnection databaseConnection) {
        this.broker = broker;
        this.databaseConnection = databaseConnection;
    }

    public void processFile(File file) throws Exception {
        try {
            ConceptualSchema newSchema = CSXParser.parse(broker, file, databaseConnection);
            broker.processEvent(new ConceptualSchemaChangeEvent(this, newSchema));
            databaseConnection.connect(newSchema.getDatabaseInfo());
        } catch (DataFormatException e) {
            throw e;
        }
    }

    public boolean prepareToProcess() throws Exception {
        return true;
    }
}
