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
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.Context;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;

/**
 * @todo this generator can easily generate scales which are not nominal, and then in
 *   succession not valid -- objects can appear multiple times if you use ORs or multiple
 *   columns where they share values
 */
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
            FCAElement object = new FCAElementImplementation(sqlFrag.getSqlClause());
			String attributeName = sqlFrag.getAttributeLabel();
            FCAElement attribute = new FCAElementImplementation(attributeName);

			context.getObjects().add(object);
			context.getAttributes().add(attribute);
			context.getRelationImplementation().insert(object, attribute);
			
			if(topNodeClause == null) {
				topNodeClause = "NOT (" + object + ")";
			} else {
				topNodeClause += " AND NOT (" + object + ")";
			}
		}
		
        FCAElement topNodeObject = new FCAElementImplementation(topNodeClause);
		context.getObjects().add(topNodeObject);
		
		return context;
	}
}
