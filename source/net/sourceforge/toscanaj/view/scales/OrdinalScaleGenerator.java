/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.WriteableDiagram2D;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.DatabaseConnectedConcept;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.gui.LabeledScrollPaneView;

import javax.swing.*;

import util.Assert;

import java.awt.*;

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

    public Diagram2D generateScale(TableColumnPair[] columns, ConceptualSchema scheme) {
        Assert.isTrue(canHandleColumns(columns));
        TableColumnPair pair = columns[0];
        JDialog scaleDialog =  new JDialog(parent);
        scaleDialog.setModal(true);
        scaleDialog.setTitle("Ordinal scale for :"+pair.getColumn().getName());
        scaleDialog.getContentPane().setLayout(new BorderLayout());

        JTextField titleEditor = new JTextField();
        scaleDialog.getContentPane().add(new LabeledScrollPaneView("Title", titleEditor), BorderLayout.CENTER);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout());

        buttonPane.add(new JButton("OK"));
        buttonPane.add(new JButton("Cancel"));
        scaleDialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
        scaleDialog.pack();
        scaleDialog.show();

        WriteableDiagram2D ret = new SimpleLineDiagram();
        ret.setTitle("Ordinal scale for "+pair.getColumn());


        return ret;
    }

    public Diagram2D generateScale(Diagram2D oldVersion) {
        return null;
    }
}
