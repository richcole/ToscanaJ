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
import net.sourceforge.toscanaj.model.diagram.*;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.model.lattice.Attribute;
import util.Assert;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.util.*;

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
        return columns.length == 1;
    }

    public Diagram2D generateScale(TableColumnPair[] columns, ConceptualSchema scheme, DatabaseConnection databaseConnection) {
        Assert.isTrue(canHandleColumns(columns));
        TableColumnPair pair = columns[0];
        OrdinalScaleEditorDialog scaleDialog = new OrdinalScaleEditorDialog(parent, pair.getColumn());
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
                                "(" + pair.getColumn().getName() + "<=" + String.valueOf(dividers.get(1)) + ")");
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
                                                               getSQLClause(pair, dividers, i));
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

        return ret;
    }

    private String getSQLClause(TableColumnPair pair, List dividers, int i) {
        String retVal = "(" + pair.getColumn().getName() + ">" + String.valueOf(dividers.get(i)) + ")";
        if(i < dividers.size()-1 ) {
            retVal += "AND (" + pair.getColumn().getName() + "<=" + String.valueOf(dividers.get(i+1)) + ")";
        }
        return retVal;
    }

    private ConceptImplementation makeConcept(String label, String queryClause) {
        ConceptImplementation retVal = new ConceptImplementation();
        if(label != null) {
            retVal.addAttribute(new Attribute(label, null));
        }
        retVal.addObject(queryClause);
        return retVal;
    }

    public Diagram2D generateScale(Diagram2D oldVersion) {
        return null;
    }
}
