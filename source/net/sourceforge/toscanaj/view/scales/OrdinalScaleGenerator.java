/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.database.Column;

import java.awt.Frame;
import java.sql.Types;

public class OrdinalScaleGenerator implements ScaleGenerator {
    private Frame parent;

    public OrdinalScaleGenerator(Frame parent) {
        this.parent = parent;
    }

    public String getScaleName() {
        return "Ordinal Scale";
    }

    /// @todo should check type of column, too -- we need at least two versions for int and float values (should be
    /// transparent to the user
    public boolean canHandleColumns(TableColumnPair[] columns) {
        if (columns.length != 1) {
            return false;
        }
        int columnType = columns[0].getColumn().getType();
        if (determineDataType(columnType) == OrdinalScaleEditorDialog.UNSUPPORTED) {
            return false;
        } else {
            return true;
        }
    }

    private int determineDataType(int columnType) {
        switch (columnType) {
            case Types.DOUBLE:
                return OrdinalScaleEditorDialog.FLOAT;
            case Types.FLOAT:
                return OrdinalScaleEditorDialog.FLOAT;
            case Types.REAL:
                return OrdinalScaleEditorDialog.FLOAT;
            case Types.BIGINT:
                return OrdinalScaleEditorDialog.INTEGER;
            case Types.INTEGER:
                return OrdinalScaleEditorDialog.INTEGER;
            case Types.SMALLINT:
                return OrdinalScaleEditorDialog.INTEGER;
            case Types.TINYINT:
                return OrdinalScaleEditorDialog.INTEGER;
            default:
                return OrdinalScaleEditorDialog.UNSUPPORTED;
        }
    }

    public Context generateScale(TableColumnPair[] columns, ConceptualSchema scheme, DatabaseConnection databaseConnection) {
        Column column = columns[0].getColumn();
        int scaleType = determineDataType(column.getType());
        if(scaleType == OrdinalScaleEditorDialog.UNSUPPORTED) {
        	throw new RuntimeException("Unsupported scale type");
        }
        OrdinalScaleEditorDialog scaleDialog = new OrdinalScaleEditorDialog(parent, column, scaleType);
        if (!scaleDialog.execute()) {
            return null;
        }

        return scaleDialog.createContext();
    }
}
