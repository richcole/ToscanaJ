/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.Query;
import net.sourceforge.toscanaj.model.diagram.*;
import net.sourceforge.toscanaj.model.lattice.AbstractConceptImplementation;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.DummyConcept;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import util.Assert;
import util.CollectionFactory;
import util.NullIterator;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class OrdinalScaleGenerator implements ScaleGenerator {
    private JFrame parent;

    public OrdinalScaleGenerator(JFrame parent) {
        this.parent = parent;
    }

    public String getScaleName() {
        return "Ordinal Scale";
    }

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
        String id = "Ordinal";
        double x = 0.;
        double y = 0.;
        AbstractConceptImplementation top = makeConcept();
        DiagramNode topNode = new DiagramNode(id,
                new Point2D.Double(x, y),
                top,
                new LabelInfo(),
                new LabelInfo(),
                null
        );
        ret.addNode(topNode);
        AbstractConceptImplementation prevConcept = top;
        DiagramNode prevNode = topNode;
        conceptList.add(top);
        for (int i = 0; i < dividers.size(); i++) {
            y += 30;
            AbstractConceptImplementation currentConcept = makeConcept("<"+String.valueOf(dividers.get(i)));
            conceptList.add(currentConcept);

            DiagramNode node = new DiagramNode(id,
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
            AbstractConceptImplementation concept = (AbstractConceptImplementation) it.next();
            concept.buildClosures();
        }


        return ret;
    }

    private AbstractConceptImplementation makeConcept() {
        return new DummyConcept();
    }

    private AbstractConceptImplementation makeConcept(String label) {
        final List list = CollectionFactory.createDefaultList();
        list.add(label);
        return new DummyConcept(list);
    }

    public Diagram2D generateScale(Diagram2D oldVersion) {
        return null;
    }
}
