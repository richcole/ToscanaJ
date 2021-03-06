/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.activity;

import net.sourceforge.toscanaj.model.ConceptualSchema;

import org.tockit.events.EventBroker;

public class NewConceptualSchemaActivity implements SimpleActivity {
    private final EventBroker broker;
    private SimpleActivity testNewOkActivity;
    private SimpleActivity postNewActivity;

    public NewConceptualSchemaActivity(final EventBroker broker) {
        this.broker = broker;
    }

    public boolean doActivity() throws Exception {
        if (this.testNewOkActivity != null) {
            final boolean result = this.testNewOkActivity.doActivity();
            if (result == false) {
                return false;
            }
        }
        new ConceptualSchema(this.broker);
        if (this.postNewActivity != null) {
            this.postNewActivity.doActivity();
        }
        return true;
    }

    public void setTestNewOkActivity(final SimpleActivity testNewOkActivity) {
        this.testNewOkActivity = testNewOkActivity;
    }

    public void setPostNewActivity(final SimpleActivity postNewActivity) {
        this.postNewActivity = postNewActivity;
    }
}
