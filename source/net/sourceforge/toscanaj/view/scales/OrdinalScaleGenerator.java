/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.fca.DiagramToContextConverter;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.diagram.*;
import net.sourceforge.toscanaj.model.lattice.Attribute;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrdinalScaleGenerator implements ScaleGenerator {
    private JFrame parent;

    public OrdinalScaleGenerator(JFrame parent) {
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
        String columnName = column.getName();
        OrdinalScaleEditorDialog scaleDialog = new OrdinalScaleEditorDialog(parent, columnName, scaleType);
        if (!scaleDialog.execute()) {
            return null;
        }

        List dividers = scaleDialog.getDividers();


        WriteableDiagram2D ret = new SimpleLineDiagram();
        ret.setTitle(scaleDialog.getDiagramTitle());


        List conceptList = new ArrayList();
        double x = 0.;
        double y = 0.;
        /// @todo handle or avoid the case where there is no divider
        ConceptImplementation top = makeConcept(null,
                "(" + columnName + "<=" + String.valueOf(dividers.get(0)) + ")");
        DiagramNode topNode = new DiagramNode("top",
                new Point2D.Double(x, y),
                top,
                new LabelInfo(),
                new LabelInfo(),
                null
        );
        ret.addNode(topNode);
        ConceptImplementation prevConcept = top;
        DiagramNode prevNode = topNode;
        conceptList.add(top);
        for (int i = 0; i < dividers.size(); i++) {
            y += 60;
            ConceptImplementation currentConcept = makeConcept(">" + String.valueOf(dividers.get(i)),
                    getSQLClause(columnName, dividers, i));
            conceptList.add(currentConcept);

            DiagramNode node = new DiagramNode((new Integer(i)).toString(),
                    new Point2D.Double(x, y),
                    currentConcept,
                    new LabelInfo(),
                    new LabelInfo(),
                    null
            );
            prevConcept.addSubConcept(currentConcept);
            currentConcept.addSuperConcept(prevConcept);

            ret.addNode(node);
            ret.addLine(prevNode, node);
            prevNode = node;
            prevConcept = currentConcept;
        }
        for (Iterator it = conceptList.iterator(); it.hasNext();) {
            ConceptImplementation concept = (ConceptImplementation) it.next();
            concept.buildClosures();
        }
		return DiagramToContextConverter.getContext(ret);
    }

    private String getSQLClause(String columnName, List dividers, int i) {
        String retVal = "(" + columnName + ">" + String.valueOf(dividers.get(i)) + ")";
        if (i < dividers.size() - 1) {
            retVal += "AND (" + columnName + "<=" + String.valueOf(dividers.get(i + 1)) + ")";
        }
        return retVal;
    }

    private ConceptImplementation makeConcept(String label, String queryClause) {
        ConceptImplementation retVal = new ConceptImplementation();
        if (label != null) {
            retVal.addAttribute(new Attribute(label, null));
        }
        retVal.addObject(queryClause);
        return retVal;
    }
}
