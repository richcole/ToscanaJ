/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.lattice.Lattice;

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
                scheme,
                databaseConnection
        );
        if (!dialog.execute()) {
            return null;
        }else{
			LatticeGenerator lgen = new GantersAlgorithm();
			Lattice lattice = lgen.createLattice(dialog.getContext());
			return NDimLayoutOperations.createDiagram(lattice, dialog.getDiagramTitle(), new DefaultDimensionStrategy());
        }       
    }

    public Diagram2D generateScale(Diagram2D oldVersion) {
        return null;
    }
}
