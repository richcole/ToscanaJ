/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupelware.source.sql;

import java.io.File;

import javax.swing.JFrame;

import org.tockit.tupelware.gui.IndexSelectionDialog;
import org.tockit.tupelware.model.TupelSet;
import org.tockit.tupelware.source.TupelSource;


public class SqlQueryEngine implements TupelSource {
    private int[] objectIndices;
    private TupelSet tupels;

    public String getMenuName() {
        return "Query from database...";
    }

    public void show(JFrame parent, File lastLocation) {
        DatabaseConnectionDialog connectionDialog = new DatabaseConnectionDialog(parent);
        connectionDialog.show();
        this.tupels = connectionDialog.getTupels();
        if(this.tupels != null) {
            IndexSelectionDialog objectSetDialog = new IndexSelectionDialog(parent, "Select object set", this.tupels.getVariableNames());
            objectSetDialog.show();
            this.objectIndices = objectSetDialog.getSelectedIndices();
        }
    }

    public TupelSet getTupels() {
        return this.tupels;
    }

    public int[] getObjectIndices() {
        return this.objectIndices;
    }

    public File getSelectedFile() {
        return null;
    }
}
