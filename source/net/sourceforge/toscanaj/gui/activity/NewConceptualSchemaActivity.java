/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.activity;

import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.gui.events.ConceptualSchemaChangeEvent;

public class NewConceptualSchemaActivity implements SimpleActivity {
    private EventBroker broker;

    public NewConceptualSchemaActivity(EventBroker broker) {
        this.broker = broker;
    }

    public boolean doActivity() throws Exception {
        broker.processEvent(new ConceptualSchemaChangeEvent(
                this,
                new ConceptualSchema(broker))
        );
        return true;
    }
}
