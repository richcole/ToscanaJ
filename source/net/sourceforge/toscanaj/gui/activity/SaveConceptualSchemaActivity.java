/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.activity;

import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.util.xmlize.XMLWriter;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import java.io.File;

public class SaveConceptualSchemaActivity implements FileActivity, EventBrokerListener {
    private ConceptualSchema conceptualSchema;

    public SaveConceptualSchemaActivity(ConceptualSchema conceptualSchema, EventBroker eventBroker) {
        this.conceptualSchema = conceptualSchema;
        eventBroker.subscribe(this, ConceptualSchemaChangeEvent.class, Object.class);

    }

    public void processFile(File file) throws Exception {
        XMLWriter.write(file, conceptualSchema);
    }

    public boolean prepareToProcess() throws Exception {
        return true;
    }

    public void processEvent(Event e) {
        if (e instanceof ConceptualSchemaChangeEvent) {
            ConceptualSchemaChangeEvent schemaEvent = (ConceptualSchemaChangeEvent) e;
            conceptualSchema = schemaEvent.getConceptualSchema();
        }

    }

    public String[] getExtensions() {
        return new String[]{"csx"};
    }

    public String getDescription() {
        return "Conceptual Schema";
    }
}
