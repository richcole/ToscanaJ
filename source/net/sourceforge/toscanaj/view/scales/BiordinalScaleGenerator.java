package net.sourceforge.toscanaj.view.scales;

import java.awt.Frame;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.Context;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public class BiordinalScaleGenerator implements ScaleGenerator{
	private Frame parent;
	
	public BiordinalScaleGenerator(Frame parent){
		this.parent = parent;
	}
	public String getScaleName() {
		 return "Biordinal Scale";
	 }

	 /// @todo should check type of column, too -- we need at least two versions for int and float values (should be
	 /// transparent to the user
	 public boolean canHandleColumns(TableColumnPair[] columns) {
		 if (columns.length != 1) {
			 return false;
		 }
		 int columnType = columns[0].getColumn().getType();
		 if (OrdinalScaleGeneratorPanel.determineDataType(columnType) == OrdinalScaleGeneratorPanel.UNSUPPORTED) {
			 return false;
		 } else {
			 return true;
		 }
	 }

	 public Context generateScale(ConceptualSchema scheme, DatabaseConnection databaseConnection) {
		 BiordinalScaleEditorDialog scaleDialog = new BiordinalScaleEditorDialog(parent, scheme.getDatabaseSchema(), databaseConnection);
		 if (!scaleDialog.execute()) {
			 return null;
		 }
		 return scaleDialog.createContext();
	 }

}