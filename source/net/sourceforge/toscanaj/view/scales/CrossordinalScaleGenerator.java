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
public class CrossordinalScaleGenerator implements ScaleGenerator{
    private Frame parent;

    public CrossordinalScaleGenerator(Frame parent){
        this.parent = parent;
    }
    public String getScaleName() {
         return "Crossordinal Scale";
     }

     /// @todo should check type of column, too -- we need at least two versions for int and float values (should be
     /// transparent to the user
     public boolean canHandleColumns(TableColumnPair[] columns) {
         if (columns.length != 1) {
             return false;
         }
         int columnType = columns[0].getColumn().getType();
         return (OrdinalScaleGeneratorPanel.determineDataType(columnType) != OrdinalScaleGeneratorPanel.UNSUPPORTED);
     }

     public Context generateScale(ConceptualSchema scheme, DatabaseConnection databaseConnection) {
         CrossordinalScaleEditorDialog scaleDialog = new CrossordinalScaleEditorDialog(parent, scheme.getDatabaseSchema(), databaseConnection);
         if (!scaleDialog.execute()) {
             return null;
         }
         return scaleDialog.createContext();
     }

}
