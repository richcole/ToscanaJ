/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.activity;

import org.tockit.events.EventBroker;
import net.sourceforge.toscanaj.model.ConceptualSchema;

public class NewConceptualSchemaActivity implements SimpleActivity {
    private EventBroker broker;

    public NewConceptualSchemaActivity(EventBroker broker) {
        this.broker = broker;
    }

    public boolean doActivity() throws Exception {
        new ConceptualSchema(broker);
        return true;
    }
}
