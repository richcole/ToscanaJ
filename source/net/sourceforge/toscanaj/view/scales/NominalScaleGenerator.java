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
import net.sourceforge.toscanaj.model.ContextImplementation;
import net.sourceforge.toscanaj.model.lattice.Attribute;

public class NominalScaleGenerator implements ScaleGenerator {
	private Frame parent;

	public NominalScaleGenerator(Frame parent) {
		this.parent = parent;
	}

	public String getScaleName() {
		return "Nominal Scale";
	}

	public boolean canHandleColumns(TableColumnPair[] columns) {
		return columns.length == 1;
	}

	public Context generateScale(
		ConceptualSchema scheme,
		DatabaseConnection databaseConnection) {
		NominalScaleEditorDialog dialog =
			new NominalScaleEditorDialog(parent, databaseConnection, scheme.getDatabaseSchema());
		if (!dialog.execute()) {
			return null;
		}

		ContextImplementation context = new ContextImplementation();
		context.setName(dialog.getDiagramTitle());
		Object[] values = dialog.getValues();
		
		String topNodeClause = null;

		for (int i = 0; i < values.length; i++) {
		    NominalScaleEditorDialog.SqlFragment sqlFrag = (NominalScaleEditorDialog.SqlFragment) values[i];
			String object = sqlFrag.getSqlClause();
			String attributeName = sqlFrag.getAttributeLabel();
			Attribute attribute = new Attribute(attributeName);

			context.getObjects().add(object);
			context.getAttributes().add(attribute);
			context.getRelationImplementation().insert(object, attribute);
			
			if(topNodeClause == null) {
				topNodeClause = "NOT (" + object + ")";
			} else {
				topNodeClause += " AND NOT (" + object + ")";
			}
		}
		
		context.getObjects().add(topNodeClause);
		
		return context;
	}
}
