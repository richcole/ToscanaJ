/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.source.sql;

import java.io.File;

import javax.swing.JFrame;

import org.tockit.relations.model.Relation;
import org.tockit.tupleware.gui.IndexSelectionDialog;
import org.tockit.tupleware.source.TupleSource;


public class SqlQueryEngine implements TupleSource {
    private File lastFile;
    private int[] objectIndices;
    private Relation tuples;

    public String getMenuName() {
        return "Query from database...";
    }

    public void show(JFrame parent, File lastLocation) {
        DatabaseConnectionDialog connectionDialog = new DatabaseConnectionDialog(parent, lastLocation);
        connectionDialog.show();
        this.tuples = connectionDialog.getTuples();
        if(this.tuples != null) {
            IndexSelectionDialog objectSetDialog = new IndexSelectionDialog(parent, "Select object set", this.tuples.getDimensionNames());
            objectSetDialog.show();
            this.objectIndices = objectSetDialog.getSelectedIndices();
            this.lastFile = connectionDialog.getLastFile();
        }
    }

    public Relation getTuples() {
        return this.tuples;
    }

    public int[] getObjectIndices() {
        return this.objectIndices;
    }

    public File getSelectedFile() {
        return this.lastFile;
    }
}
