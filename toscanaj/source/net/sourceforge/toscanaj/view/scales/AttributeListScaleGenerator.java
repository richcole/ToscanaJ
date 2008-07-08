package net.sourceforge.toscanaj.view.scales;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.context.WritableFCAElement;

import org.tockit.context.model.BinaryRelationImplementation;
import org.tockit.context.model.Context;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public class AttributeListScaleGenerator implements ScaleGenerator {
    private final Frame parent;

    public AttributeListScaleGenerator(final Frame parent) {
        this.parent = parent;
    }

    public String getScaleName() {
        return "Attribute List";
    }

    public boolean canHandleColumns(final TableColumnPair[] columns) {
        return true;
    }

    public Context generateScale(final ConceptualSchema scheme,
            final DatabaseConnection databaseConnection) {
        final AttributeListScaleGeneratorDialog scaleDialog = new AttributeListScaleGeneratorDialog(
                parent);
        if (!scaleDialog.execute()) {
            return null;
        }
        final ContextImplementation context = new ContextImplementation();
        context.setName(scaleDialog.getDiagramTitle());
        final BinaryRelationImplementation relation = context
                .getRelationImplementation();

        final Object[][] tableData = scaleDialog.getData();
        final int dimensions = tableData.length;
        final FCAElement[] attributes = new FCAElement[dimensions];

        for (int i = 0; i < dimensions; i++) {
            final String attributeName = createAttributeName(tableData, i);
            attributes[i] = new FCAElementImplementation(attributeName);
            context.getAttributes().add(attributes[i]);
        }

        final boolean useAllCombi = scaleDialog.getUseAllCombinations();
        for (int i = 0; i < Math.pow(2, dimensions); i++) {
            String objectData = "";
            final List<FCAElement> relatedAttributes = new ArrayList<FCAElement>();
            for (int j = 0; j < dimensions; j++) {
                if (j != 0) {
                    objectData += " AND ";
                }
                if ((i & (1 << j)) == 0) {
                    objectData += " NOT ";
                } else {
                    relatedAttributes.add(attributes[j]);
                }
                objectData += "(" + tableData[j][1] + ")";
            }

            final WritableFCAElement object = new FCAElementImplementation(
                    objectData);

            if (useAllCombi) {
                context.getObjects().add(object);
                final Iterator<FCAElement> it = relatedAttributes.iterator();
                while (it.hasNext()) {
                    final Object attrib = it.next();
                    relation.insert(object, attrib);
                }
            } else {
                try {
                    final int result = databaseConnection.queryInt(
                            "SELECT count (*) FROM "
                                    + scheme.getDatabaseInfo().getTable()
                                            .getSqlExpression() + " WHERE ( "
                                    + objectData + " );", 1);
                    if (result != 0) {
                        context.getObjects().add(object);
                        final Iterator<FCAElement> it = relatedAttributes
                                .iterator();
                        while (it.hasNext()) {
                            final Object attrib = it.next();
                            relation.insert(object, attrib);
                        }
                    }
                } catch (final DatabaseException e) {
                    throw new RuntimeException(e.getCause().getMessage());
                }
            }
        }
        return context;
    }

    private String createAttributeName(final Object[][] data, final int i) {
        final String attrLabelName = (String) data[i][0];
        return attrLabelName.trim();
    }
}
