/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.activity;

import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import org.tockit.events.EventBroker;

import java.io.File;

public class LoadConceptualSchemaActivity implements FileActivity {
    private EventBroker broker;
    private SimpleActivity testOpenOkActivity;

    public LoadConceptualSchemaActivity(EventBroker broker) {
        this.broker = broker;
    }

    public void processFile(File file) throws Exception {
        try {
            ConceptualSchema newSchema = CSXParser.parse(broker, file);
        } catch (DataFormatException e) {
            throw e;
        }
    }

    public boolean prepareToProcess() throws Exception {
    	if(this.testOpenOkActivity != null) {
    		return this.testOpenOkActivity.doActivity();
    	} else {
        	return true;
    	}
    }

	public void setTestOpenOkActivity(SimpleActivity testOpenOkActivity) {
		this.testOpenOkActivity = testOpenOkActivity;
	}
	
    public String[] getExtensions() {
        return new String[]{"csx"};
    }
    
    public String getDescription() {
        return "Conceptual Schema";
    }
}
