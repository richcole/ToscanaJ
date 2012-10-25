/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.activity;

import java.io.File;

import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;

import org.tockit.events.EventBroker;

public class LoadConceptualSchemaActivity implements FileActivity {
    private final EventBroker broker;
    private SimpleActivity testOpenOkActivity;

    public LoadConceptualSchemaActivity(final EventBroker broker) {
        this.broker = broker;
    }

    public void processFile(final File file) throws Exception {
        CSXParser.parse(this.broker, file);
    }

    public boolean prepareToProcess() throws Exception {
        if (this.testOpenOkActivity != null) {
            return this.testOpenOkActivity.doActivity();
        } else {
            return true;
        }
    }

    public void setTestOpenOkActivity(final SimpleActivity testOpenOkActivity) {
        this.testOpenOkActivity = testOpenOkActivity;
    }

    public String[] getExtensions() {
        return new String[] { "csx" };
    }

    public String getDescription() {
        return "Conceptual Schema";
    }
}
