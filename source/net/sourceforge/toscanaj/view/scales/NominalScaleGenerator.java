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
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.diagram.*;
import net.sourceforge.toscanaj.model.lattice.Attribute;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NominalScaleGenerator implements ScaleGenerator {
    private JFrame parent;
    /// @todo this should be calculated from the number of nodes (the more nodes the wider)
    private static final int DIAGRAM_WIDTH = 400;

    public NominalScaleGenerator(JFrame parent) {
        this.parent = parent;
    }

    public String getScaleName() {
        return "Nominal Scale";
    }

    public boolean canHandleColumns(TableColumnPair[] columns) {
        return columns.length == 1;
    }

    public Diagram2D generateScale(TableColumnPair[] columns, ConceptualSchema scheme, DatabaseConnection databaseConnection) {
        Column column = columns[0].getColumn();
        NominalScaleEditorDialog dialog = new NominalScaleEditorDialog(
                parent,
                column,
                databaseConnection
        );
        if (!dialog.execute()) {
            return null;
        }

        Object[] values = dialog.getValues();

        WriteableDiagram2D ret = new SimpleLineDiagram();
        ret.setTitle(dialog.getDiagramTitle());

        List conceptList = new ArrayList();
        ConceptImplementation top = makeConcept(null, null);
        DiagramNode topNode = new DiagramNode("top",
                new Point2D.Double(0, 0),
                top,
                new LabelInfo(),
                new LabelInfo(),
                null
        );
        ret.addNode(topNode);
        conceptList.add(top);
        ConceptImplementation bottom = makeConcept(null, null);
        DiagramNode bottomNode = new DiagramNode("bottom",
                new Point2D.Double(0, 100),
                top,
                new LabelInfo(),
                new LabelInfo(),
                null
        );
        ret.addNode(bottomNode);
        conceptList.add(bottom);
        int numberOfValues = values.length;
        for (int i = 0; i < numberOfValues; i++) {
            double x = -DIAGRAM_WIDTH / 2 + i * DIAGRAM_WIDTH / (double) (numberOfValues - 1);
            ConceptImplementation currentConcept = makeConcept(String.valueOf(values[i]),
                    getSQLClause(column.getName(), values, i));
            conceptList.add(currentConcept);

            DiagramNode node = new DiagramNode((new Integer(i)).toString(),
                    new Point2D.Double(x, 50),
                    currentConcept,
                    new LabelInfo(),
                    new LabelInfo(),
                    null
            );
            currentConcept.addSuperConcept(top);
            currentConcept.addSubConcept(bottom);

            ret.addNode(node);
            ret.addLine(topNode, node);
            ret.addLine(node, bottomNode);
        }
        for (Iterator it = conceptList.iterator(); it.hasNext();) {
            ConceptImplementation concept = (ConceptImplementation) it.next();
            concept.buildClosures();
        }

        return ret;
    }

    private String getSQLClause(String columnName, Object[] values, int i) {
        return columnName + "='" + values[i].toString() + "'";
    }

    private ConceptImplementation makeConcept(String label, String queryClause) {
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
