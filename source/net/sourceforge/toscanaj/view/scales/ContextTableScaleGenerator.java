/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import java.awt.Frame;

import org.tockit.events.EventBroker;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.context.Context;

public class ContextTableScaleGenerator implements ScaleGenerator {
    private Frame parent;
    private EventBroker eventBroker;

    public ContextTableScaleGenerator(Frame parent, EventBroker eventBroker) {
        this.parent = parent;
        this.eventBroker = eventBroker;
    }

    public String getScaleName() {
        return "Context Table";
    }

    public boolean canHandleColumns(TableColumnPair[] columns) {
        return true;
    }

    public Context generateScale(ConceptualSchema scheme, DatabaseConnection databaseConnection) {
        ContextTableScaleEditorDialog dialog = new ContextTableScaleEditorDialog(
                parent,
                scheme,
                databaseConnection,
                eventBroker
        );
        if (!dialog.execute()) {
            return null;
        }else{
			return dialog.getContext();
			
        }       
    }
}
