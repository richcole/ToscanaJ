/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.activity;

import java.io.File;

import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.util.xmlize.XMLWriter;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class SaveConceptualSchemaActivity implements FileActivity,
        EventBrokerListener {
    private ConceptualSchema conceptualSchema;

    public SaveConceptualSchemaActivity(
            final ConceptualSchema conceptualSchema,
            final EventBroker eventBroker) {
        this.conceptualSchema = conceptualSchema;
        eventBroker.subscribe(this, ConceptualSchemaChangeEvent.class,
                Object.class);

    }

    public void processFile(final File file) throws Exception {
        XMLWriter.write(file, this.conceptualSchema);
        this.conceptualSchema.setLocation(file.toURI().toURL());
    }

    public boolean prepareToProcess() throws Exception {
        return true;
    }

    public void processEvent(final Event e) {
        if (e instanceof ConceptualSchemaChangeEvent) {
            final ConceptualSchemaChangeEvent schemaEvent = (ConceptualSchemaChangeEvent) e;
            this.conceptualSchema = schemaEvent.getConceptualSchema();
        }

    }

    public String[] getExtensions() {
        return new String[] { "csx" };
    }

    public String getDescription() {
        return "Conceptual Schema";
    }
}
