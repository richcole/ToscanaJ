/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import java.awt.Frame;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.view.context.ContextTableEditorDialog;

import org.tockit.context.model.Context;
import org.tockit.events.EventBroker;

public class ContextTableScaleGenerator implements ScaleGenerator {
    private final Frame parent;
    private final EventBroker eventBroker;

    public ContextTableScaleGenerator(final Frame parent,
            final EventBroker eventBroker) {
        this.parent = parent;
        this.eventBroker = eventBroker;
    }

    public String getScaleName() {
        return "Context Table";
    }

    public boolean canHandleColumns(final TableColumnPair[] columns) {
        return true;
    }

    public Context generateScale(final ConceptualSchema scheme,
            final DatabaseConnection databaseConnection) {
        final ContextTableEditorDialog dialog = new ContextTableEditorDialog(
                parent, scheme, databaseConnection, eventBroker, true);
        if (!dialog.execute()) {
            return null;
        } else {
            return dialog.getContext();

        }
    }
}
