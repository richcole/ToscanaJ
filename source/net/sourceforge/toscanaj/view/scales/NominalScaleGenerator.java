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
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;

import javax.swing.*;

public class NominalScaleGenerator implements ScaleGenerator {
    private JFrame parent;

    public NominalScaleGenerator(JFrame parent) {
        this.parent = parent;
    }

    public String getScaleName() {
        return "Nominal Scale";
    }

    public boolean canHandleColumns(TableColumnPair[] columns) {
        return columns.length == 1;
    }

    public Diagram2D generateScale(TableColumnPair[] columns, ConceptualSchema scheme, DatabaseConnection databaseConnection) {
        NominalScaleEditorDialog dialog = new NominalScaleEditorDialog(
                parent,
                columns[0].getColumn(),
                databaseConnection
        );
        if(!dialog.execute()) {
            return null;
        }
        return new SimpleLineDiagram();
    }

    public Diagram2D generateScale(Diagram2D oldVersion) {
        return null;
    }
}
