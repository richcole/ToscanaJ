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
import net.sourceforge.toscanaj.model.Query;
import net.sourceforge.toscanaj.model.XML_SyntaxError;
import net.sourceforge.toscanaj.gui.LabeledScrollPaneView;

import javax.swing.*;

import util.Assert;
import util.NullIterator;
import util.CollectionFactory;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

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

    static class DummyConcept implements Concept{
        public Element toXML() {
            return null;
        }

        public void readXML(Element elem) throws XML_SyntaxError {
        }

        public boolean isRealised() {
            return false;
        }

        public int getIntentSize() {
            return 0;
        }

        public double getIntentSizeRelative() {
            return 0;
        }

        public int getExtentSize() {
            return 0;
        }

        public double getExtentSizeRelative() {
            return 0;
        }

        public int getAttributeContingentSize() {
            return 0;
        }

        public double getAttributeContingentSizeRelative() {
            return 0;
        }

        public int getObjectContingentSize() {
            return 0;
        }

        public double getObjectContingentSizeRelative() {
            return 0;
        }

        public Iterator getIntentIterator() {
            return NullIterator.makeNull();
        }

        public Iterator getExtentIterator() {
            return NullIterator.makeNull();
        }

        public Iterator getAttributeContingentIterator() {
            return NullIterator.makeNull();
        }

        public List executeQuery(Query query, boolean contingentOnly) {
            return CollectionFactory.createDefaultList();
        }

        public Iterator getObjectContingentIterator() {
            return NullIterator.makeNull();
        }

        public Concept filterByExtent(Concept other) {
            return this;
        }

        public Concept filterByContingent(Concept other) {
            return this;
        }

        public Concept getCollapsedConcept() {
            return this;
        }

        public boolean isTop() {
            return false;
        }

        public boolean isBottom() {
            return false;
        }

        public boolean hasSuperConcept(Concept concept) {
            return false;
        }

        public boolean hasSubConcept(Concept concept) {
            return false;
        }
    }

    public Diagram2D generateScale(TableColumnPair[] columns, ConceptualSchema scheme) {
        Assert.isTrue(canHandleColumns(columns));
        TableColumnPair pair = columns[0];
        OrdinalScaleEditorDialog scaleDialog =  new OrdinalScaleEditorDialog(parent, pair);
        if(!scaleDialog.execute()){
            return null;
        }

        WriteableDiagram2D ret = new SimpleLineDiagram();
        ret.setTitle(scaleDialog.getDiagramTitle());

         Concept concept = new DummyConcept();
         DiagramNode node = new DiagramNode(
                 new Point2D.Double(0., 0.),
                 concept,
                 null,
                 null,
                 null
         );
        ret.addNode(node);

        return ret;
    }

    public Diagram2D generateScale(Diagram2D oldVersion) {
        return null;
    }
}
