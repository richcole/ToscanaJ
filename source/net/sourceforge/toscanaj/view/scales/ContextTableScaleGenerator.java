/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.diagram.WriteableDiagram2D;

import javax.swing.*;

public class ContextTableScaleGenerator implements ScaleGenerator {
    private JFrame parent;

    public ContextTableScaleGenerator(JFrame parent) {
        this.parent = parent;
    }

    public String getScaleName() {
        return "Context Table";
    }

    public boolean canHandleColumns(TableColumnPair[] columns) {
        return true;
    }

    public Diagram2D generateScale(TableColumnPair[] columns, ConceptualSchema scheme, DatabaseConnection databaseConnection) {
        Column column = columns[0].getColumn();
        ContextTableScaleEditorDialog dialog = new ContextTableScaleEditorDialog(
                parent,
                databaseConnection
        );
        if (!dialog.execute()) {
            return null;
        }

        WriteableDiagram2D ret = new SimpleLineDiagram();
        return ret;
    }

    public Diagram2D generateScale(Diagram2D oldVersion) {
        return null;
    }
}
