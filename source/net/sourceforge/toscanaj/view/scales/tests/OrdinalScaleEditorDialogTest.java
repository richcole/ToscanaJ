/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.Table;
import net.sourceforge.toscanaj.model.Column;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaLoadedEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.view.scales.*;

import java.util.Collections;
import java.util.List;

import util.CollectionFactory;

public class OrdinalScaleEditorDialogTest extends TestCase {
    public OrdinalScaleEditorDialogTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(OrdinalScaleEditorDialogTest.class);
    }

    public void testAddDivider(){
        OrdinalScaleEditorDialog dialog = new OrdinalScaleEditorDialog(null,
                new Column("Column", java.sql.Types.INTEGER));
        dialog.addDelimiter(1.);

        List expValues = makeListOfDoubles(new double[]{1.});
        assertEquals(expValues, dialog.getDividers());
        dialog.addDelimiter(0.);
        expValues = makeListOfDoubles(new double[]{0., 1.});
        assertEquals(expValues, dialog.getDividers());


        dialog.addDelimiter(2.);
        assertEquals(makeListOfDoubles(new double[]{0., 1., 2.}), dialog.getDividers());

        dialog.addDelimiter(1.);
        assertEquals(makeListOfDoubles(new double[]{0., 1., 2}), dialog.getDividers());


    }

    private List makeListOfDoubles(double[] expValuesArr) {
        List expValues = CollectionFactory.createDefaultList();
        for(int i=0; i<expValuesArr.length; i++){
            expValues.add(new Double(expValuesArr[i]));
        }
        return expValues;
    }
}
