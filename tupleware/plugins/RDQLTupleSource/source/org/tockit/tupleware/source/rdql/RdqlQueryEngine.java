/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.source.rdql;

import java.io.File;

import javax.swing.JFrame;

import org.tockit.plugin.Plugin;
import org.tockit.tupleware.gui.IndexSelectionDialog;
import org.tockit.relations.model.Relation;
import org.tockit.tupleware.source.TupleSource;
import org.tockit.tupleware.source.TupleSourceRegistry;

public class RdqlQueryEngine implements TupleSource, Plugin {
	private int[] objectIndices;
	private Relation tupleSet;
	private File selectedFile;

	public String getMenuName() {
		return "Query RDF or N3 file...";
	}

	public void show(JFrame parent, File lastLocation) {
		RdfQueryDialog rdfQueryDialog = new RdfQueryDialog(parent);
		rdfQueryDialog.show();
		this.tupleSet = rdfQueryDialog.getTuples();
		System.out.println("GOT TUPLE SET: " + this.tupleSet);
		if(this.tupleSet != null) {
			IndexSelectionDialog objectSetDialog = new IndexSelectionDialog(parent, "Select object set", this.tupleSet.getDimensionNames());
			objectSetDialog.show();
			this.objectIndices = objectSetDialog.getSelectedIndices();
		}
	}

	public Relation getTuples() {
		return tupleSet;
	}

	public int[] getObjectIndices() {
		return objectIndices;
	}

	public File getSelectedFile() {
		return selectedFile;
	}
	
	public void load() {
		TupleSourceRegistry.registerTupleSource(this);
	}
}
