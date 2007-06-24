/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id:RdqlQueryEngine.java 1929 2007-06-24 04:50:48Z peterbecker $
 */
package org.tockit.tupleware.source.rdf;

import java.io.File;

import javax.swing.JFrame;

import org.tockit.plugin.Plugin;
import org.tockit.tupleware.gui.IndexSelectionDialog;
import org.tockit.relations.model.Relation;
import org.tockit.tupleware.source.TupleSource;
import org.tockit.tupleware.source.TupleSourceRegistry;

import com.hp.hpl.jena.rdf.model.Model;

public class RdfQueryEngine implements TupleSource, Plugin {
	private int[] objectIndices;
	private Relation tupleSet;
	private File selectedFile;
	private Model rdfModel;

	public String getMenuName() {
		return "Query RDF Model...";
	}

	public void show(JFrame parent, File lastLocation) {
		RdfQueryDialog rdfQueryDialog = new RdfQueryDialog(parent, rdfModel);
		rdfQueryDialog.setVisible(true);
		this.tupleSet = rdfQueryDialog.getTuples();
		if(this.tupleSet != null) {
			this.rdfModel = rdfQueryDialog.getRdfModel();
			IndexSelectionDialog objectSetDialog = new IndexSelectionDialog(parent, "Select object set", this.tupleSet.getDimensionNames());
			objectSetDialog.setVisible(true);
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
