/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.activity;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;

import java.io.File;

public class LoadConceptualSchemaActivity implements FileActivity {
    private EventBroker broker;
    private DatabaseConnection databaseConnection;

    public LoadConceptualSchemaActivity(EventBroker broker, DatabaseConnection databaseConnection) {
        this.broker = broker;
        this.databaseConnection = databaseConnection;
    }

    public void processFile(File file) throws Exception {
        try {
            ConceptualSchema newSchema = CSXParser.parse(broker, file);
        } catch (DataFormatException e) {
            throw e;
        }
    }

    public boolean prepareToProcess() throws Exception {
        return true;
    }
}
