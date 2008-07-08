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
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;

import org.tockit.context.model.Context;

/**
 * @todo this generator can easily generate scales which are not nominal, and
 *       then in succession not valid -- objects can appear multiple times if
 *       you use ORs or multiple columns where they share values
 */
public class NominalScaleGenerator implements ScaleGenerator {
    private final Frame parent;

    public NominalScaleGenerator(final Frame parent) {
        this.parent = parent;
    }

    public String getScaleName() {
        return "Nominal Scale";
    }

    public boolean canHandleColumns(final TableColumnPair[] columns) {
        return columns.length == 1;
    }

    public Context generateScale(final ConceptualSchema scheme,
            final DatabaseConnection databaseConnection) {
        final NominalScaleEditorDialog dialog = new NominalScaleEditorDialog(
                parent, databaseConnection, scheme.getDatabaseSchema());
        if (!dialog.execute()) {
            return null;
        }

        final ContextImplementation context = new ContextImplementation();
        context.setName(dialog.getDiagramTitle());
        final Object[] values = dialog.getValues();

        String topNodeClause = null;

        for (final Object value : values) {
            final NominalScaleEditorDialog.SqlFragment sqlFrag = (NominalScaleEditorDialog.SqlFragment) value;
            final FCAElement object = new FCAElementImplementation(sqlFrag
                    .getSqlClause());
            final String attributeName = sqlFrag.getAttributeLabel();
            final FCAElement attribute = new FCAElementImplementation(
                    attributeName);

            context.getObjects().add(object);
            context.getAttributes().add(attribute);
            context.getRelationImplementation().insert(object, attribute);

            if (topNodeClause == null) {
                topNodeClause = "NOT (" + object + ")";
            } else {
                topNodeClause += " AND NOT (" + object + ")";
            }
        }

        final FCAElement topNodeObject = new FCAElementImplementation(
                topNodeClause);
        context.getObjects().add(topNodeObject);

        return context;
    }
}
