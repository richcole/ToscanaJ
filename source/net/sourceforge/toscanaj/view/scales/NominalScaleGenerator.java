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
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.lattice.Attribute;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;

public class NominalScaleGenerator implements ScaleGenerator {
	private Frame parent;
	/// @todo this should be calculated from the number of nodes (the more nodes the wider)
	private static final int DIAGRAM_WIDTH = 400;

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
		TableColumnPair[] columns,
		ConceptualSchema scheme,
		DatabaseConnection databaseConnection) {
		Column column = columns[0].getColumn();
		NominalScaleEditorDialog dialog =
			new NominalScaleEditorDialog(parent, column, databaseConnection, scheme.getDatabaseSchema());
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

	private ConceptImplementation makeConcept(
		String label,
		String queryClause) {
		ConceptImplementation retVal = new ConceptImplementation();
		if (label != null) {
			retVal.addAttribute(new Attribute(label, null));
		}
		if (queryClause != null) {
			retVal.addObject(queryClause);
		}
		return retVal;
	}
}
