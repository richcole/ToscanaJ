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
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.database.Column;

public class ContextTableScaleGenerator implements ScaleGenerator {
    private Frame parent;

    public ContextTableScaleGenerator(Frame parent) {
        this.parent = parent;
    }

    public String getScaleName() {
        return "Context Table";
    }

    public boolean canHandleColumns(TableColumnPair[] columns) {
        return true;
    }

    public Context generateScale(TableColumnPair[] columns, ConceptualSchema scheme, DatabaseConnection databaseConnection) {
        Column column = columns[0].getColumn();
        ContextTableScaleEditorDialog dialog = new ContextTableScaleEditorDialog(
                parent,
                scheme,
                databaseConnection
        );
        if (!dialog.execute()) {
            return null;
        }else{
			return dialog.getContext();
			
        }       
    }
}
